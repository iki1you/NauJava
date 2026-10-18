package ru.vladimir.dao;

import java.util.List;

import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.model.Task;

public interface SearchRepository {
    List<Project> findAllByStatusAndDeletedAtIsNull(ProjectStatus status);

    List<Task> findByProjectName(String projectName);
}
