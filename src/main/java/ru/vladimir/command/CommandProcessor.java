package ru.vladimir.command;

import java.util.List;
import java.util.NoSuchElementException;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandProcessor {
    private final ProjectService projectService;

    @Autowired
    public CommandProcessor(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void processCommand(String input) {
        String[] commandParts = input.trim().split(" ");

        try {
            switch (commandParts[0]) {
                case "create":
                    createProject(commandParts);
                    break;
                case "read":
                    printProject(projectService.findById(Long.valueOf(commandParts[1])));
                    break;
                case "list":
                    printProjects(projectService.findAll());
                    break;
                case "list-status":
                    ProjectStatus status = ProjectStatus.valueOf(commandParts[1].toUpperCase());
                    printProjects(projectService.findAllByStatus(status));
                    break;
                case "update":
                    updateProject(commandParts);
                    break;
                case "delete":
                    deleteProject(commandParts);
                    break;
                default:
                    System.out.println("Введена неизвестная команда...");
                    break;
            }
        } catch (NoSuchElementException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        }
    }

    private void createProject(String[] commandParts) {
        if (commandParts.length < 3) {
            throw new IllegalArgumentException("Используйте: create <name> <description>");
        }

        String description = combineParts(commandParts, 2);
        projectService.createProject(commandParts[1], description);
        System.out.println("Проект успешно добавлен.");
    }

    private void updateProject(String[] commandParts) {
        if (commandParts.length < 5) {
            throw new IllegalArgumentException("Используйте: update <id> <status> <name> <description>");
        }

        Long id = Long.valueOf(commandParts[1]);
        ProjectStatus status = ProjectStatus.valueOf(commandParts[2].toUpperCase());
        String description = combineParts(commandParts, 4);
        projectService.updateProject(id, commandParts[3], description, status);
        System.out.println("Проект успешно изменён.");
    }

    private void deleteProject(String[] commandParts) {
        projectService.deleteById(Long.valueOf(commandParts[1]));
        System.out.println("Проект успешно удалён.");
    }

    private String combineParts(String[] commandParts, int startIndex) {
        StringBuilder result = new StringBuilder();

        for (int index = startIndex; index < commandParts.length; index++) {
            if (index > startIndex) {
                result.append(" ");
            }

            result.append(commandParts[index]);
        }

        return result.toString();
    }

    private void printProjects(List<Project> projects) {
        if (projects.isEmpty()) {
            System.out.println("Проекты не найдены.");
            return;
        }

        for (Project project : projects) {
            printProject(project);
        }
    }

    private void printProject(Project project) {
        System.out.printf(
                "id=%d, name=%s, description=%s, status=%s%n",
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus()
        );
    }
}
