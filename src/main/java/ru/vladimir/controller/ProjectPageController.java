package ru.vladimir.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vladimir.service.ProjectService;

@Controller
@RequestMapping("/ui/projects")
public class ProjectPageController {
    private final ProjectService projectService;

    public ProjectPageController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "projects";
    }
}
