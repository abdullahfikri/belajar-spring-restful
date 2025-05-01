package dev.mfikri.restful.service;

import dev.mfikri.restful.entity.User;
import dev.mfikri.restful.model.LoginUserRequest;
import dev.mfikri.restful.model.RegisterUserRequest;
import dev.mfikri.restful.model.UpdateUserRequest;
import dev.mfikri.restful.model.UserResponse;
import dev.mfikri.restful.repository.UserRepository;
import dev.mfikri.restful.util.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ValidationService validationService;

    public UserServiceImpl(UserRepository userRepository, ValidationService validationService) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public void register(RegisterUserRequest request) {
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    @Override
    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Override
    public UserResponse update(User user, UpdateUserRequest updateUserRequest) {
        validationService.validate(updateUserRequest);

        if (Objects.nonNull(updateUserRequest.getName())){
            user.setName(updateUserRequest.getName());
        }

        if (Objects.nonNull(updateUserRequest.getPassword())){
            user.setPassword(BCrypt.hashpw(updateUserRequest.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
