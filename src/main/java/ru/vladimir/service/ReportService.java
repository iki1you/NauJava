package ru.vladimir.service;

import ru.vladimir.model.Report;

public interface ReportService {
    Long createReport();

    Report findById(Long id);

    void generateReportAsync(Long id);
}
