package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.RegisterUserRequest;
import dev.mfikri.restful.model.UpdateUserRequest;
import dev.mfikri.restful.model.UserResponse;

public interface UserService {
    void register(RegisterUserRequest request);
    UserResponse get(User user);
    UserResponse update(User user, UpdateUserRequest updateUserRequest);
}
