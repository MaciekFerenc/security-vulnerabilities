package com.mferenc.springboottemplate.reports;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }
}
