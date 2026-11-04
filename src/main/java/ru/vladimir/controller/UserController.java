package ru.vladimir.controller;

import java.net.URI;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.model.User;
import ru.vladimir.service.UserService;

@RepositoryRestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> create(@RequestBody UserRequest request) {
        User savedUser = userService.addUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<User> patch(@PathVariable("id") Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request, true));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> replace(@PathVariable("id") Long id, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request, false));
    }
}
