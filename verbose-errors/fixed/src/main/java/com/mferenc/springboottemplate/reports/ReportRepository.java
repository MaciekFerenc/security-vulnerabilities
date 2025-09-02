package com.mferenc.springboottemplate.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findTopByOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM reports WHERE author = :authorId", nativeQuery = true)
    List<Report> findByAuthorId(@Param("authorId") long authorId);
}
