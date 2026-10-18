package ru.vladimir.service;

import java.util.List;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;

public interface ProjectService {
    void createProject(String name, String description);

    Project findById(Long id);

    List<Project> findAll();

    List<Project> findAllByStatus(ProjectStatus status);

    void updateProject(Long id, String name, String description, ProjectStatus status);

    void deleteById(Long id);
}
