package ru.vladimir.dto;

import java.util.List;

public record ReportSnapshot(long usersCount, List<ProjectSnapshot> projects,
        long usersCountTimeMs, long projectsFetchTimeMs, long totalTimeMs) {
}
