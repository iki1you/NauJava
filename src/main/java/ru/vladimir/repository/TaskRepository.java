package ru.vladimir.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.Task;

@RepositoryRestResource
public interface TaskRepository extends CrudRepository<Task, Long> {
    @Query("SELECT task FROM Task task WHERE task.project.name = :projectName")
    List<Task> findByProjectName(@Param("projectName") String projectName);
}
