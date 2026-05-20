package com.meeting.booking.notification;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 站内通知接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class NotificationIntegrationTest {

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
    void bookingCreatesNotificationAndMarkRead() throws Exception {
        int unreadBefore = fetchUnreadCount();

        LocalDateTime start = uniqueSlot("notify-booking");
        LocalDateTime end = start.plusMinutes(30);
        String createBody = buildCreateBody(1L, "通知测试会议", start, end);
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk());

        int unreadAfterCreate = fetchUnreadCount();
        assertTrue(unreadAfterCreate >= unreadBefore + 1);

        MvcResult listResult = mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").exists())
                .andReturn();
        JsonNode listRoot = new ObjectMapper()
                .readTree(listResult.getResponse().getContentAsString());
        long notificationId = listRoot.get("data").get(0).get("id").asLong();

        mockMvc.perform(post("/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        int unreadAfterRead = fetchUnreadCount();
        assertTrue(unreadAfterRead < unreadAfterCreate);
    }

    @Test
    void markAllRead() throws Exception {
        mockMvc.perform(post("/notifications/read-all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0));
    }

    private int fetchUnreadCount() throws Exception {
        MvcResult result = mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = new ObjectMapper().readTree(result.getResponse().getContentAsString());
        return root.get("data").get("count").asInt();
    }

    private LocalDateTime uniqueSlot(String salt) {
        int slotIndex = Math.abs(salt.hashCode() + (int) (System.nanoTime() % 10000)) % 24;
        int minutes = 9 * 60 + slotIndex * 15;
        return LocalDate.now().plusDays(2).atStartOfDay().plusMinutes(minutes);
    }

    private String buildCreateBody(Long roomId, String title, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format(
                "{\"roomId\":%d,\"title\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, title, start.format(fmt), end.format(fmt));
    }
}
