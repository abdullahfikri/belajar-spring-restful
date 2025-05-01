package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginUserRequest request);
    void logout(User user);
}
