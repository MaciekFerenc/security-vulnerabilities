package com.mferenc.springboottemplate.tickets;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final AuthenticationFacade auth;
    private final TicketRepository ticketRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketController(AuthenticationFacade auth, TicketRepository ticketRepository) {
        this.auth = auth;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getTickets(
            @RequestParam(required = false) String phrase
    ) {
        long userId = auth.getCurrentUser().getId();
        List<Ticket> tickets;

        if (phrase != null && !phrase.isEmpty()) {
            tickets = ticketRepository.findByUserIdAndDescriptionContaining(userId, phrase);
        } else {
            tickets = ticketRepository.findByUserId(userId);
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/2")
    public ResponseEntity<List<Ticket>> getTickets2(
            @RequestParam(required = false) String phrase
    ) {
        long userId = auth.getCurrentUser().getId();

        String sql = "SELECT * FROM tickets WHERE user_id = :userId";

        if (phrase != null && !phrase.isEmpty()) {
            sql += " AND description LIKE :phrase";
        }

        Query query = entityManager.createNativeQuery(sql, Ticket.class);
        query.setParameter("userId", userId);

        if (phrase != null && !phrase.isEmpty()) {
            query.setParameter("phrase", "%" + phrase + "%");
        }

        List<Ticket> tickets = query.getResultList();
        return ResponseEntity.ok(tickets);
    }
}