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
    public ResponseEntity<String> getLatestReportDescription() {
        var reportOpt = reportRepository.findTopByOrderByCreatedAtDesc();
        return reportOpt.map(report -> new ResponseEntity<>(
                report.getDescription().toUpperCase(),
                HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
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
