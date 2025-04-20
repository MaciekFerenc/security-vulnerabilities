package com.mferenc.springboottemplate.tickets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public ResponseEntity<List<Ticket>> getTickets(
            @RequestParam(required = false) String phrase
    ) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        String userIdSql = "SELECT id FROM users WHERE username ='" + username + "'";
        Query userQuery = entityManager.createNativeQuery(userIdSql);
        Long userId = ((Number) userQuery.getSingleResult()).longValue();

        String sql = "SELECT * FROM tickets WHERE user_id = " + userId;

        if (phrase != null && !phrase.isEmpty()) {
            sql += " AND description LIKE '%" + phrase + "%'";
        }

        Query query = entityManager.createNativeQuery(sql, Ticket.class);
        List<Ticket> tickets = query.getResultList();

        return ResponseEntity.ok(tickets);
    }
}