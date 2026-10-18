package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long> {
}
