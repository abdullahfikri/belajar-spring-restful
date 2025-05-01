package dev.mfikri.restful.controller;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.RegisterUserRequest;
import dev.mfikri.restful.model.UpdateUserRequest;
import dev.mfikri.restful.model.UserResponse;
import dev.mfikri.restful.model.WebResponse;
import dev.mfikri.restful.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<String> register (@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user) {
        UserResponse response = userService.get(user);
        return WebResponse.<UserResponse>builder().data(response).build();
    }

    @PatchMapping(path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.update(user, updateUserRequest);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

}
