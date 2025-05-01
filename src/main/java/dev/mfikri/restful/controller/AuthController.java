package dev.mfikri.restful.controller;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.TokenResponse;
import dev.mfikri.restful.model.WebResponse;
import dev.mfikri.restful.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse login = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(login).build();
    }

    @DeleteMapping(path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout (User user) {
        authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }
}
