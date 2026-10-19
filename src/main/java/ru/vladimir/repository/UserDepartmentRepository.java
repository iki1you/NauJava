package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.UserDepartment;

@RepositoryRestResource
public interface UserDepartmentRepository extends CrudRepository<UserDepartment, Long> {
}
