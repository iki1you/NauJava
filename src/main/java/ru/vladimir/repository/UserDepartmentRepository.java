package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.UserDepartment;

public interface UserDepartmentRepository extends CrudRepository<UserDepartment, Long> {
}
