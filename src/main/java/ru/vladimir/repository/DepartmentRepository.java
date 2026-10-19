package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.Department;

@RepositoryRestResource
public interface DepartmentRepository extends CrudRepository<Department, Long> {
}
