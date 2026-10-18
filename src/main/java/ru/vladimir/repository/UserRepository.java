package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
