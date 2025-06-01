package com.mferenc.springboottemplate.reports;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportRepository reportRepository;

    public ReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/latest-description")
    public String getLatestReportDescription() {
        return reportRepository.
                findTopByOrderByCreatedAtDesc()
                .getDescription()
                .toUpperCase();
    }

    @GetMapping("/authors/{authorId}")
    public List<Report> getReportsByAuthor(@PathVariable long authorId) {
        return reportRepository.findByAuthorId(authorId);
    }
}
