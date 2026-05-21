package com.meeting.booking.booking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.booking.support.BookingTestSlots;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理员预约管理接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminBookingIntegrationTest {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void loginAdmin() throws Exception {
        adminToken = loginAs("admin", "admin123");
    }

    @Test
    void adminCanListUpdateAndCancelBooking() throws Exception {
        long roomId = resolveFirstRoomId();
        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusHours(1);
        String createBody = String.format(
                "{\"roomId\":%d,\"title\":\"管理员预约测试\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, start.format(ISO), end.format(ISO));

        MvcResult createResult = mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andReturn();
        long bookingId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("bookingId").asLong();

        mockMvc.perform(get("/admin/bookings")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray());

        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newEnd = newStart.plusHours(1);
        String updateBody = String.format(
                "{\"roomId\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, newStart.format(ISO), newEnd.format(ISO));
        mockMvc.perform(put("/admin/bookings/" + bookingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookingId").value(bookingId));

        mockMvc.perform(post("/admin/bookings/" + bookingId + "/cancel")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateToConflictSlotFails() throws Exception {
        long roomId = resolveFirstRoomId();
        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusHours(1);

        String body1 = String.format(
                "{\"roomId\":%d,\"title\":\"冲突A\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, start.format(ISO), end.format(ISO));
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body1))
                .andExpect(status().isOk());

        LocalDateTime start2 = BookingTestSlots.nextStart();
        LocalDateTime end2 = start2.plusHours(1);
        String body2 = String.format(
                "{\"roomId\":%d,\"title\":\"冲突B\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, start2.format(ISO), end2.format(ISO));
        MvcResult second = mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                .andExpect(status().isOk())
                .andReturn();
        long bookingId2 = objectMapper.readTree(second.getResponse().getContentAsString())
                .get("data").get("bookingId").asLong();

        String conflictUpdate = String.format(
                "{\"roomId\":%d,\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, start.format(ISO), end.format(ISO));
        mockMvc.perform(put("/admin/bookings/" + bookingId2)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictUpdate))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40901));
    }

    @Test
    void employeeCannotAccessAdminBookings() throws Exception {
        String empToken = loginAsEmployee();
        mockMvc.perform(get("/admin/bookings")
                        .header("Authorization", "Bearer " + empToken))
                .andExpect(status().isForbidden());
    }

    private long resolveFirstRoomId() throws Exception {
        MvcResult rooms = mockMvc.perform(get("/admin/rooms")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(rooms.getResponse().getContentAsString())
                .get("data").get("items").get(0).get("id").asLong();
    }

    private String loginAsEmployee() throws Exception {
        String username = "bk_emp_" + System.currentTimeMillis();
        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format(
                                "{\"username\":\"%s\",\"role\":\"EMPLOYEE\",\"enabled\":true}",
                                username)))
                .andExpect(status().isOk());
        return loginAs(username, "123456");
    }

    private String loginAs(String username, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                                username, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return root.get("data").get("token").asText();
    }
}
