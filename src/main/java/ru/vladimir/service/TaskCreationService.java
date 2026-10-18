package ru.vladimir.service;

import ru.vladimir.model.Task;

public interface TaskCreationService {
    Task createTaskWithComment(Long projectId, String title, Long authorId, String commentText);
}
