package ru.vladimir.dto;

import ru.vladimir.model.ProjectStatus;

public record ProjectSnapshot(Long id, String name, String description, ProjectStatus status) {
}
