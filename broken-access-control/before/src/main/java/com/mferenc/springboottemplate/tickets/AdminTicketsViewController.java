package com.mferenc.springboottemplate.tickets;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import com.mferenc.springboottemplate.auth.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminTicketsViewController {

    private final TicketRepository ticketRepository;
    private final AuthenticationFacade auth;

    public AdminTicketsViewController(TicketRepository ticketRepository, AuthenticationFacade auth) {
        this.ticketRepository = ticketRepository;
        this.auth = auth;
    }

    @GetMapping("/tickets")
    public String showAllTickets(Model model) {
        User user = auth.getCurrentUser();
        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/tickets";
        }
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        model.addAttribute("isAdmin", true);
        return "admin-tickets";
    }

    @PostMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}