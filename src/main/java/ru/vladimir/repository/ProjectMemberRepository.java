package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import ru.vladimir.model.ProjectMember;

public interface ProjectMemberRepository extends CrudRepository<ProjectMember, Long> {
}
