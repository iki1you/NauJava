package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.User;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, Long> {
}
