package ru.vladimir.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladimir.dto.UserRequest;
import ru.vladimir.service.UserService;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute UserRequest user, Model model) {
        try {
            userService.addUser(user);
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("message", exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            model.addAttribute("message", "Пользователь с таким логином уже существует.");
        }
        return "registration";
    }
}
