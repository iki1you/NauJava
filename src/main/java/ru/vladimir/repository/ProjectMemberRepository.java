package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.ProjectMember;

@RepositoryRestResource
public interface ProjectMemberRepository extends CrudRepository<ProjectMember, Long> {
}
