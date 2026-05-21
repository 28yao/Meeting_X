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

import com.meeting.booking.support.BookingTestSlots;

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
        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusMinutes(30);
        String createBody = buildCreateBody(1L, "通知测试会议", start, end);
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk());

        MvcResult listResult = mockMvc.perform(get("/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].title").value("预约成功"))
                .andReturn();
        JsonNode listRoot = new ObjectMapper()
                .readTree(listResult.getResponse().getContentAsString());
        long notificationId = listRoot.get("data").get("items").get(0).get("id").asLong();
        assertTrue(!listRoot.get("data").get("items").get(0).get("read").asBoolean());

        mockMvc.perform(post("/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notifications/unread-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").exists());
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

    private String buildCreateBody(Long roomId, String title, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return String.format(
                "{\"roomId\":%d,\"title\":\"%s\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, title, start.format(fmt), end.format(fmt));
    }
}
