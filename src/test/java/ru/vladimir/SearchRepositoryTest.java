package ru.vladimir;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.vladimir.dao.SearchRepository;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.model.Task;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class SearchRepositoryTest {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SearchRepository searchRepository;

    private Project project;
    private Project otherProject;
    private Project deletedProject;
    private Task task;

    @BeforeEach
    void setUp() {
        String name = UUID.randomUUID().toString();

        project = new Project();
        project.setName(name);
        projectRepository.save(project);

        otherProject = new Project();
        otherProject.setName("Other " + name);
        otherProject.setStatus(ProjectStatus.IN_PROGRESS);
        projectRepository.save(otherProject);

        deletedProject = new Project();
        deletedProject.setName("Deleted " + name);
        deletedProject.setDeletedAt(Instant.now());
        projectRepository.save(deletedProject);

        task = new Task();
        task.setProject(project);
        task.setTitle("Project task");
        taskRepository.save(task);

        Task otherTask = new Task();
        otherTask.setProject(otherProject);
        otherTask.setTitle("Other task");
        taskRepository.save(otherTask);
    }

    @Test
    void findProjectsByStatusUsingSpringData() {
        List<Project> result = projectRepository.findAllByStatusAndDeletedAtIsNull(ProjectStatus.NEW);

        assertActiveNewProject(result);
    }

    @Test
    void findTasksByProjectNameUsingJpql() {
        List<Task> result = taskRepository.findByProjectName(project.getName());

        assertEquals(1, result.size());
        assertEquals(task.getId(), result.getFirst().getId());
    }

    @Test
    void findProjectsByStatusUsingCriteria() {
        List<Project> result = searchRepository.findAllByStatusAndDeletedAtIsNull(ProjectStatus.NEW);

        assertActiveNewProject(result);
    }

    @Test
    void findTasksByProjectNameUsingCriteria() {
        List<Task> result = searchRepository.findByProjectName(project.getName());

        assertEquals(1, result.size());
        assertEquals(task.getId(), result.getFirst().getId());
    }

    private void assertActiveNewProject(List<Project> result) {
        List<Long> ids = new ArrayList<>();
        for (Project foundProject : result) {
            ids.add(foundProject.getId());
        }

        assertTrue(ids.contains(project.getId()));
        assertFalse(ids.contains(otherProject.getId()));
        assertFalse(ids.contains(deletedProject.getId()));
    }
}
