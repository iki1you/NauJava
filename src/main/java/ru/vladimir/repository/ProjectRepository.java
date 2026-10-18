package ru.vladimir.repository;

import java.util.List;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> readAllByStatus(ProjectStatus status);
}
