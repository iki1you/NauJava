package ru.vladimir.repository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryProjectRepository implements ProjectRepository {
    private final List<Project> projectContainer;
    private long nextId = 1;

    public InMemoryProjectRepository(List<Project> projectContainer) {
        this.projectContainer = projectContainer;
    }

    @Override
    public void create(Project project) {
        project.setId(nextId++);
        projectContainer.add(project);
    }

    @Override
    public Project read(Long id) {
        return projectContainer.stream()
                .filter(project -> project.getId().equals(id))
                .filter(project -> project.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + id));
    }

    @Override
    public List<Project> readAll() {
        return projectContainer.stream()
                .filter(project -> project.getDeletedAt() == null)
                .toList();
    }

    @Override
    public List<Project> readAllByStatus(ProjectStatus status) {
        return projectContainer.stream()
                .filter(project -> project.getDeletedAt() == null)
                .filter(project -> project.getStatus() == status)
                .toList();
    }

    @Override
    public void update(Project project) {
        for (int index = 0; index < projectContainer.size(); index++) {
            Project currentProject = projectContainer.get(index);

            if (currentProject.getId().equals(project.getId()) && currentProject.getDeletedAt() == null) {
                projectContainer.set(index, project);
                return;
            }
        }

        throw new NoSuchElementException("Project not found: " + project.getId());
    }

    @Override
    public void delete(Long id) {
        Project project = read(id);
        project.setDeletedAt(Instant.now());
    }
}
