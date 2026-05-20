package com.meeting.booking.user;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 管理员用户管理接口集成测试。
 *
 * @author liuxinsi
 * @date 2026-05-20
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void loginAdmin() throws Exception {
        adminToken = loginAs("admin", "admin123");
    }

    @Test
    void adminCanCreateAndUpdateUser() throws Exception {
        String username = "emp_" + System.currentTimeMillis();
        String createBody = String.format(
                "{\"username\":\"%s\",\"password\":\"emp12345\",\"displayName\":\"测试员工\","
                        + "\"role\":\"EMPLOYEE\",\"enabled\":true}",
                username);

        MvcResult createResult = mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long userId = created.get("data").get("id").asLong();

        mockMvc.perform(put("/admin/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"测试员工-已改\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayName").value("测试员工-已改"));

        String employeeToken = loginAs(username, "emp12345");
        assertNotNull(employeeToken);

        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicateUsernameFails() throws Exception {
        String body = "{\"username\":\"admin\",\"password\":\"x123456\","
                + "\"displayName\":\"重复\",\"role\":\"EMPLOYEE\"}";
        mockMvc.perform(post("/admin/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(40904));
    }

    @Test
    void listUsers() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
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
