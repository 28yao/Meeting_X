package com.meeting.booking.room;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理员会议室管理接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminRoomIntegrationTest {

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
    void adminCanCreateUpdateAndDeleteRoom() throws Exception {
        String name = "测试会议室_" + System.currentTimeMillis();
        String createBody = String.format(
                "{\"name\":\"%s\",\"capacity\":10,\"floor\":\"3F\",\"roomType\":\"中型\",\"equipment\":\"投影\"}",
                name);

        MvcResult createResult = mockMvc.perform(post("/admin/rooms")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.status").value(RoomStatus.NORMAL))
                .andReturn();

        long roomId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        mockMvc.perform(patch("/admin/rooms/" + roomId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"MAINTENANCE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(RoomStatus.MAINTENANCE));

        mockMvc.perform(put("/admin/rooms/" + roomId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"capacity\":12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.capacity").value(12));

        mockMvc.perform(delete("/admin/rooms/" + roomId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void cannotDeleteRoomWithFutureBooking() throws Exception {
        MvcResult listResult = mockMvc.perform(get("/admin/rooms")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode rooms = objectMapper.readTree(listResult.getResponse().getContentAsString()).get("data");
        long roomId = rooms.get(0).get("id").asLong();

        LocalDateTime start = BookingTestSlots.nextStart();
        LocalDateTime end = start.plusHours(1);
        String bookingBody = String.format(
                "{\"roomId\":%d,\"title\":\"删除房间测试\",\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                roomId, start.format(ISO), end.format(ISO));

        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingBody))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/admin/rooms/" + roomId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40903));
    }

    @Test
    void employeeCannotCreateRoom() throws Exception {
        String empToken = loginAsEmployee();
        mockMvc.perform(post("/admin/rooms")
                        .header("Authorization", "Bearer " + empToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"非法\",\"capacity\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listRooms() throws Exception {
        mockMvc.perform(get("/admin/rooms")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    private String loginAsEmployee() throws Exception {
        String username = "room_emp_" + System.currentTimeMillis();
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
