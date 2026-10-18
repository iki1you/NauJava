package ru.vladimir.service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.annotation.PostConstruct;
import ru.vladimir.config.Config;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final Config config;

    public ProjectServiceImpl(ProjectRepository projectRepository, Config config) {
        this.projectRepository = projectRepository;
        this.config = config;
    }

    @PostConstruct
    public void init() {
        System.out.println(config.getAppName() + " initialized version: " + config.getAppVersion());
    }

    @Override
    public void createProject(String name, String description) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        projectRepository.save(project);
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAllByDeletedAtIsNull();
    }

    @Override
    public List<Project> findAllByStatus(ProjectStatus status) {
        return projectRepository.findAllByStatusAndDeletedAtIsNull(status);
    }

    @Override
    public void updateProject(Long id, String name, String description, ProjectStatus status) {
        Project project = findById(id);
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);
        projectRepository.save(project);
    }

    @Override
    public void deleteById(Long id) {
        Project project = findById(id);
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }
}
