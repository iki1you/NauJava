package ru.vladimir.service;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.User;
import ru.vladimir.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByLogin(username);
    }

    public User addUser(UserRequest request) {
        if (!StringUtils.hasText(request.login()) || !StringUtils.hasText(request.firstName())
                || !StringUtils.hasText(request.lastName())) {
            throw new IllegalArgumentException("Заполните логин, имя и фамилию.");
        }
        if (request.login().length() > 50 || request.firstName().length() > 100
                || request.lastName().length() > 100) {
            throw new IllegalArgumentException("Логин должен содержать до 50 символов, имя и фамилия — до 100.");
        }
        User user = new User();
        user.setLogin(request.login());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPasswordHash(encodePassword(request.password()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserRequest request, boolean partial) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        if (request.password() != null) {
            user.setPasswordHash(encodePassword(request.password()));
        }
        if (!partial || request.login() != null) {
            user.setLogin(request.login());
        }
        if (!partial || request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (!partial || request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        return userRepository.save(user);
    }

    private String encodePassword(String password) {
        if (password == null || password.isBlank()
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Пароль обязателен и должен содержать не более 72 байт UTF-8.");
        }
        return passwordEncoder.encode(password);
    }
}
