package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.Comment;

@RepositoryRestResource
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
