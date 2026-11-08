package ru.vladimir.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.vladimir.model.Report;

@RepositoryRestResource(exported = false)
public interface ReportRepository extends CrudRepository<Report, Long> {
}
