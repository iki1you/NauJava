package ru.vladimir.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.vladimir.dto.ReportSnapshot;
import ru.vladimir.model.Report;
import ru.vladimir.model.ReportStatus;
import ru.vladimir.service.ReportService;
import tools.jackson.databind.json.JsonMapper;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final JsonMapper jsonMapper;
    private final TemplateEngine templateEngine;

    public ReportController(ReportService reportService, JsonMapper jsonMapper, TemplateEngine templateEngine) {
        this.reportService = reportService;
        this.jsonMapper = jsonMapper;
        this.templateEngine = templateEngine;
    }

    @PostMapping
    public ResponseEntity<Long> create() {
        Long id = reportService.createReport();
        reportService.generateReportAsync(id);
        return ResponseEntity.accepted().body(id);
    }

    @GetMapping(value = "/{id}", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> getContent(@PathVariable("id") Long id) {
        Report report = reportService.findById(id);
        if (report.getStatus() == ReportStatus.CREATED) {
            return ResponseEntity.accepted().body("Отчёт ещё формируется.");
        }
        if (report.getStatus() == ReportStatus.ERROR) {
            return ResponseEntity.internalServerError().body("Не удалось сформировать отчёт.");
        }

        ReportSnapshot snapshot = jsonMapper.readValue(report.getContent(), ReportSnapshot.class);
        Context context = new Context();
        context.setVariable("snapshot", snapshot);
        String html = templateEngine.process("report", context);
        return ResponseEntity.ok(html);
    }
}
