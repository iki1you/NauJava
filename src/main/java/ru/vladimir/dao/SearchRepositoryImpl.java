package ru.vladimir.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import ru.vladimir.model.Project;
import ru.vladimir.model.ProjectStatus;
import ru.vladimir.model.Task;

@Repository
public class SearchRepositoryImpl implements SearchRepository {
    private final EntityManager entityManager;

    public SearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Project> findAllByStatusAndDeletedAtIsNull(ProjectStatus status) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Project> query = builder.createQuery(Project.class);
        Root<Project> project = query.from(Project.class);

        Predicate statusCondition = builder.equal(project.get("status"), status);
        Predicate notDeleted = builder.isNull(project.get("deletedAt"));
        query.select(project).where(builder.and(statusCondition, notDeleted));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Task> findByProjectName(String projectName) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        Root<Task> task = query.from(Task.class);
        Join<Task, Project> project = task.join("project");

        query.select(task).where(builder.equal(project.get("name"), projectName));

        return entityManager.createQuery(query).getResultList();
    }
}
