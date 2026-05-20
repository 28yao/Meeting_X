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

import com.meeting.booking.support.BookingTestSlots;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 预约与会议室接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingIntegrationTest {

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
    void listAvailableRooms() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);
        mockMvc.perform(get("/rooms/available")
                        .param("date", date.toString())
                        .param("startTime", "10:00")
                        .param("endTime", "10:30")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void createBookingSuccess() throws Exception {
        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusMinutes(30);
        String body = buildCreateBody(1L, "集成测试会议", start, end);
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookingId").exists());
    }

    @Test
    void createOverlapBookingFails() throws Exception {
        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusMinutes(30);
        String body = buildCreateBody(1L, "冲突测试A", start, end);
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40901));
    }

    private String buildCreateBody(Long roomId, String title, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format(
                "{\"roomId\":%d,\"title\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, title, start.format(fmt), end.format(fmt));
    }
}
