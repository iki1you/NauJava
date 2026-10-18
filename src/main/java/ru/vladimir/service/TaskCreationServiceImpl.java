package ru.vladimir.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladimir.model.Comment;
import ru.vladimir.model.Project;
import ru.vladimir.model.Task;
import ru.vladimir.model.User;
import ru.vladimir.repository.CommentRepository;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.TaskRepository;
import ru.vladimir.repository.UserRepository;

@Service
public class TaskCreationServiceImpl implements TaskCreationService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    public TaskCreationServiceImpl(ProjectRepository projectRepository, UserRepository userRepository,
            TaskRepository taskRepository, CommentRepository commentRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public Task createTaskWithComment(Long projectId, String title, Long authorId, String commentText) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + authorId));

        Task task = new Task();
        task.setProject(project);
        task.setTitle(title);
        taskRepository.save(task);

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setText(commentText);
        commentRepository.save(comment);

        return task;
    }
}
