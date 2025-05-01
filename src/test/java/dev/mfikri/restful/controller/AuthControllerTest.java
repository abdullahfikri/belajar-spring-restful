package dev.mfikri.restful.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.TokenResponse;
import dev.mfikri.restful.model.WebResponse;
import dev.mfikri.restful.repository.UserRepository;
import dev.mfikri.restful.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void loginFailedValidation() throws Exception{
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

    @Test
    void loginFailedUserNotFound() throws Exception{
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testtest");
        request.setPassword("testtest");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertEquals("Username or password wrong", response.getErrors());
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setName("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testtest");
        request.setPassword("wrongpassword");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertEquals("Username or password wrong", response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setName("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("testtest");
        request.setPassword("testtest");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDB = userRepository.findById("testtest").orElse(null);
            assertNotNull(userDB);
            assertEquals(userDB.getToken(), response.getData().getToken());
            assertEquals(userDB.getTokenExpiredAt(), response.getData().getExpiredAt());

        });
    }

    @Test
    void logoutFailed() throws Exception{
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
            assertEquals("Unauthorized request", response.getErrors());
        });
    }

    @Test
    void logoutSuccess() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        user.setName("testtest");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals("OK", response.getData());

            User userDB = userRepository.findById("testtest").orElse(null);
            assertNotNull(userDB);
            assertNull(userDB.getToken());
            assertNull(userDB.getTokenExpiredAt());
        });
    }
}