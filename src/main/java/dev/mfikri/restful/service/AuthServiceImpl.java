package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.TokenResponse;
import dev.mfikri.restful.repository.UserRepository;
import dev.mfikri.restful.util.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public AuthServiceImpl(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));
        if (BCrypt.checkpw(request.getPassword(), user.getPassword())){
            // success
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            // failed
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    @Override
    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 60 * 24 * 30);
    }
}
