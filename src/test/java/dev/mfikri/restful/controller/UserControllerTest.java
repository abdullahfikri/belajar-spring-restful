package dev.mfikri.restful.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.RegisterUserRequest;
import dev.mfikri.restful.model.UpdateUserRequest;
import dev.mfikri.restful.model.UserResponse;
import dev.mfikri.restful.model.WebResponse;
import dev.mfikri.restful.repository.UserRepository;
import dev.mfikri.restful.service.UserService;
import dev.mfikri.restful.util.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

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
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("john2122");
        request.setPassword("secretpassword");
        request.setName("John Doe");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK", response.getData());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("john2122");
        user.setPassword(BCrypt.hashpw("1234password", BCrypt.gensalt()));
        user.setName("Another John");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("john2122");
        request.setPassword("secretpassword");
        request.setName("John Doe");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("Username already registered", response.getErrors());
        });
    }

    //TODO: test max and min size validation


    @Test
    void getUserUnauthorized() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
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
    void getUserUnauthorizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
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
    void getUserUnauthorizedTokenExpired() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        user.setName("testtest");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
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
    void getUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        user.setName("testtest");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getUsername());
            assertNotNull(response.getData().getName());
            assertEquals("testtest", response.getData().getUsername());
            assertEquals("testtest", response.getData().getName());
        });
    }

    @Test
    void updateUserUnauthorized() throws Exception{
        UpdateUserRequest request = new UpdateUserRequest();
        mockMvc.perform(
                patch("/api/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
                        .content(objectMapper.writeValueAsString(request))
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
    void updateUserUnauthorizedTokenNotSend() throws Exception{
        UpdateUserRequest request = new UpdateUserRequest();
        mockMvc.perform(
                patch("/api/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
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
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setUsername("testtest");
        user.setPassword(BCrypt.hashpw("testtest", BCrypt.gensalt()));
        user.setName("testtest");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("testupdated");
        request.setPassword("testupdated");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getUsername());
            assertNotNull(response.getData().getName());
            assertEquals("testtest", response.getData().getUsername());
            assertEquals("testupdated", response.getData().getName());

            User userDB = userRepository.findById("testtest").orElse(null);
            assertNotNull(userDB);
            assertEquals("testupdated", userDB.getName());
            assertTrue(BCrypt.checkpw("testupdated", userDB.getPassword()));
        });
    }
}