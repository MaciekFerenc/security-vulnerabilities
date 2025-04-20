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
@RequestMapping("/tickets")
public class TicketController {
    private final AuthenticationFacade auth;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketController(AuthenticationFacade auth) {
        this.auth = auth;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getTickets(
            @RequestParam(required = false) String phrase
    ) {
        long userId = auth.getCurrentUser().getId();

        String sql = "SELECT * FROM tickets WHERE user_id = " + userId;

        if (phrase != null && !phrase.isEmpty()) {
            sql += " AND description LIKE '%" + phrase + "%'";
        }

        Query query = entityManager.createNativeQuery(sql, Ticket.class);
        List<Ticket> tickets = query.getResultList();

        return ResponseEntity.ok(tickets);
    }
}