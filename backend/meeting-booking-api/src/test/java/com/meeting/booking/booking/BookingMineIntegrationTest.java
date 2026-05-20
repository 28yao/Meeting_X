package com.meeting.booking.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 我的预约列表与取消接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingMineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void login() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = new ObjectMapper().readTree(loginResult.getResponse().getContentAsString());
        token = root.get("data").get("token").asText();
        assertNotNull(token);
    }

    @Test
    void listMineAndCancelSuccess() throws Exception {
        LocalDateTime start = uniqueSlot("mine-cancel");
        LocalDateTime end = start.plusMinutes(30);
        String createBody = buildCreateBody(1L, "我的预约测试", start, end);
        MvcResult createResult = mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode createRoot = new ObjectMapper()
                .readTree(createResult.getResponse().getContentAsString());
        long bookingId = createRoot.get("data").get("bookingId").asLong();

        MvcResult listResult = mockMvc.perform(get("/bookings/mine")
                        .param("page", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode listRoot = new ObjectMapper()
                .readTree(listResult.getResponse().getContentAsString());
        JsonNode matched = findItemByBookingId(listRoot.get("data").get("items"), bookingId);
        org.junit.jupiter.api.Assertions.assertTrue(matched.get("cancellable").asBoolean());

        mockMvc.perform(post("/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MvcResult listAfterCancel = mockMvc.perform(get("/bookings/mine")
                        .param("page", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode afterRoot = new ObjectMapper()
                .readTree(listAfterCancel.getResponse().getContentAsString());
        JsonNode cancelled = findItemByBookingId(afterRoot.get("data").get("items"), bookingId);
        org.junit.jupiter.api.Assertions.assertEquals("CANCELLED", cancelled.get("displayStatus").asText());

        mockMvc.perform(post("/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(40303));
    }

    private LocalDateTime uniqueSlot(String salt) {
        int slotIndex = Math.abs(salt.hashCode() + (int) (System.nanoTime() % 10000)) % 24;
        int minutes = 9 * 60 + slotIndex * 15;
        return LocalDate.now().plusDays(2).atStartOfDay().plusMinutes(minutes);
    }

    private JsonNode findItemByBookingId(JsonNode items, long bookingId) {
        for (JsonNode item : items) {
            if (item.get("bookingId").asLong() == bookingId) {
                return item;
            }
        }
        throw new AssertionError("booking not found in list: " + bookingId);
    }

    private String buildCreateBody(Long roomId, String title, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format(
                "{\"roomId\":%d,\"title\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, title, start.format(fmt), end.format(fmt));
    }
}
