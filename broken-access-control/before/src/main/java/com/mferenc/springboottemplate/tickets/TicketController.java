package com.mferenc.springboottemplate.tickets;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import com.mferenc.springboottemplate.auth.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}