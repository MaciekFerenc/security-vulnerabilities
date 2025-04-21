package com.mferenc.springboottemplate.tickets;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class TicketsViewController {
    private final TicketRepository ticketRepository;
    private final AuthenticationFacade auth;

    public TicketsViewController(TicketRepository ticketRepository, AuthenticationFacade auth) {
        this.ticketRepository = ticketRepository;
        this.auth = auth;
    }

    @PostMapping("/tickets")
    public String createTicket(@RequestParam("description") String description) {
        long currentUserId = auth.getCurrentUser().getId();
        Ticket ticket = new Ticket(description, currentUserId);
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @GetMapping("/tickets")
    public void showTickets_PlainHtml(HttpServletResponse response) throws IOException {
        long currentUserId = auth.getCurrentUser().getId();
        List<Ticket> tickets = ticketRepository.findByUserId(currentUserId);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Lista zgłoszeń</h1>");
        out.println("<ul>");
        for (Ticket ticket : tickets) {
            out.println("<li>" + ticket.getDescription() + "</li>");
        }
        out.println("</ul>");
        out.println("<a href=\"/form.html\">Utwórz nowe zgłoszenie</a>");
        out.println("</body></html>");
    }

    @GetMapping("/tickets-th")
    public String showTickets(Model model) {
        long currentUserId = auth.getCurrentUser().getId();
        List<Ticket> tickets = ticketRepository.findByUserId(currentUserId);
        model.addAttribute("tickets", tickets);
        return "tickets";
    }
}
