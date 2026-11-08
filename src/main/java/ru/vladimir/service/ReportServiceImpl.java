package ru.vladimir.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.vladimir.dto.ProjectSnapshot;
import ru.vladimir.dto.ReportSnapshot;
import ru.vladimir.model.Report;
import ru.vladimir.model.ReportStatus;
import ru.vladimir.repository.ProjectRepository;
import ru.vladimir.repository.ReportRepository;
import ru.vladimir.repository.UserRepository;
import tools.jackson.databind.json.JsonMapper;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final JsonMapper jsonMapper;

    public ReportServiceImpl(ReportRepository reportRepository, UserRepository userRepository,
            ProjectRepository projectRepository, JsonMapper jsonMapper) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Long createReport() {
        Report report = new Report();
        Report savedReport = reportRepository.save(report);
        return savedReport.getId();
    }

    @Override
    public Report findById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Report not found: " + id));
    }

    @Override
    public void generateReportAsync(Long id) {
        CompletableFuture.runAsync(() -> {
            Report report = findById(id);
            try {
                ReportSnapshot snapshot = collectSnapshot();
                report.setContent(jsonMapper.writeValueAsString(snapshot));
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);
            } catch (InterruptedException exception) {
                try {
                    markAsFailed(report, exception);
                } finally {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception exception) {
                markAsFailed(report, exception);
            }
        });
    }

    private void markAsFailed(Report report, Exception exception) {
        LOG.error("Report generation failed: {}", report.getId(), exception);
        report.setContent(null);
        report.setStatus(ReportStatus.ERROR);
        reportRepository.save(report);
    }

    private ReportSnapshot collectSnapshot() throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        FutureTask<TimedResult<Long>> usersTask = new FutureTask<>(() -> {
            long queryStartTime = System.currentTimeMillis();
            long count = userRepository.count();
            return new TimedResult<>(count, System.currentTimeMillis() - queryStartTime);
        });
        FutureTask<TimedResult<List<ProjectSnapshot>>> projectsTask = new FutureTask<>(() -> {
            long queryStartTime = System.currentTimeMillis();
            List<ProjectSnapshot> projects = projectRepository.findAllByDeletedAtIsNull().stream()
                    .map(project -> new ProjectSnapshot(project.getId(), project.getName(),
                            project.getDescription(), project.getStatus()))
                    .toList();
            return new TimedResult<>(projects, System.currentTimeMillis() - queryStartTime);
        });

        Thread usersThread = new Thread(usersTask, "report-users");
        Thread projectsThread = new Thread(projectsTask, "report-projects");

        try {
            usersThread.start();
            projectsThread.start();
            usersThread.join();
            projectsThread.join();

            TimedResult<Long> users = usersTask.get();
            TimedResult<List<ProjectSnapshot>> projects = projectsTask.get();
            long totalTimeMs = System.currentTimeMillis() - startTime;

            return new ReportSnapshot(users.value(), projects.value(),
                    users.elapsedMs(), projects.elapsedMs(), totalTimeMs);
        } finally {
            usersTask.cancel(true);
            projectsTask.cancel(true);
        }
    }

    private record TimedResult<T>(T value, long elapsedMs) {
    }
}
