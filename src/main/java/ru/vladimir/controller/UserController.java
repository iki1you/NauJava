package ru.vladimir.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.User;
import ru.vladimir.repository.UserRepository;

@RepositoryRestController
@Transactional
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> create(@RequestBody UserRequest request) {
        User user = new User();
        user.setPasswordHash(encodePassword(request.password()));
        user.setLogin(request.login());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> patch(@PathVariable("id") Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(update(id, request, true));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> replace(@PathVariable("id") Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(update(id, request, false));
    }

    private User update(Long id, UserRequest request, boolean partial) {
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
