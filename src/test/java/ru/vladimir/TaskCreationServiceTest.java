package ru.vladimir;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.vladimir.model.Comment;
import ru.vladimir.model.Project;
import ru.vladimir.model.Task;
import ru.vladimir.model.User;
import ru.vladimir.repository.CommentRepository;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.TaskRepository;
import ru.vladimir.repository.UserRepository;
import ru.vladimir.service.TaskCreationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TaskCreationServiceTest {
    @Autowired
    private TaskCreationService taskCreationService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Project project;
    private User author;

    @BeforeEach
    void setUp() {
        String name = UUID.randomUUID().toString();

        project = new Project();
        project.setName(name);
        projectRepository.save(project);

        author = new User();
        author.setLogin(name);
        author.setPasswordHash("test-hash");
        author.setFirstName("Test");
        author.setLastName("User");
        userRepository.save(author);
    }

    @AfterEach
    void cleanUp() {
        for (Comment comment : commentRepository.findAll()) {
            if (comment.getTask().getProject().getId().equals(project.getId())) {
                commentRepository.deleteById(comment.getId());
            }
        }
        for (Task task : taskRepository.findByProjectName(project.getName())) {
            taskRepository.deleteById(task.getId());
        }
        projectRepository.deleteById(project.getId());
        userRepository.deleteById(author.getId());
    }

    @Test
    void createTaskWithCommentSavesBothEntities() {
        Task createdTask = taskCreationService.createTaskWithComment(
                project.getId(), "New task", author.getId(), "First comment");

        Task savedTask = taskRepository.findById(createdTask.getId()).orElseThrow();
        assertEquals("New task", savedTask.getTitle());
        assertEquals(project.getId(), savedTask.getProject().getId());

        List<Comment> savedComments = new ArrayList<>();
        for (Comment comment : commentRepository.findAll()) {
            if (comment.getTask().getId().equals(createdTask.getId())) {
                savedComments.add(comment);
            }
        }

        assertEquals(1, savedComments.size());
        assertEquals("First comment", savedComments.getFirst().getText());
        assertEquals(author.getId(), savedComments.getFirst().getAuthor().getId());
    }

    @Test
    void createTaskWithCommentRollsBackWhenCommentCannotBeSaved() {
        long commentsBefore = commentRepository.count();

        // Внешней тестовой транзакции нет: проверяем откат внутри сервиса.
        assertThrows(DataIntegrityViolationException.class,
                () -> taskCreationService.createTaskWithComment(
                        project.getId(), "Failed task", author.getId(), null));

        assertTrue(taskRepository.findByProjectName(project.getName()).isEmpty());
        assertEquals(commentsBefore, commentRepository.count());
    }
}
