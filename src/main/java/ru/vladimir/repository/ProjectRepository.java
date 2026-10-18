package ru.vladimir.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findByIdAndDeletedAtIsNull(Long id);

    List<Project> findAllByDeletedAtIsNull();

    List<Project> findAllByStatusAndDeletedAtIsNull(ProjectStatus status);
}
