package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
