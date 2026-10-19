package ru.vladimir.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.model.Task;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.TaskRepository;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public SearchController(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/projects")
    public List<Project> findProjectsByStatus(@RequestParam("status") ProjectStatus status) {
        return projectRepository.findAllByStatusAndDeletedAtIsNull(status);
    }

    @GetMapping("/tasks")
    public List<Task> findTasksByProjectName(@RequestParam("projectName") String projectName) {
        return taskRepository.findByProjectName(projectName);
    }
}
