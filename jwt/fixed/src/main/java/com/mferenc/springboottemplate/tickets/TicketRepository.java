package com.mferenc.springboottemplate.tickets;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserIdAndDescriptionContaining(Long userId, String description);
    List<Ticket> findByUserId(Long userId);
}