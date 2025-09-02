package com.mferenc.springboottemplate.tickets;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import com.mferenc.springboottemplate.files.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class TicketsViewController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final TicketRepository ticketRepository;
    private final AuthenticationFacade auth;
    private final FileService fileService;

    public TicketsViewController(TicketRepository ticketRepository, AuthenticationFacade auth, FileService fileService) {
        this.ticketRepository = ticketRepository;
        this.auth = auth;
        this.fileService = fileService;
    }

    @PostMapping(value = "/tickets")
    public String createTicket(
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        long currentUserId = auth.getCurrentUser().getId();
        Ticket ticket = new Ticket(description, currentUserId);
        ticketRepository.save(ticket);

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            ticket.setAttachmentName(originalFilename);
            fileService.saveFile(file, ticket.getId().toString());
            ticketRepository.save(ticket);
        }

        return "redirect:/tickets";
    }

    @GetMapping("/tickets-html")
    public void showTickets_PlainHtml(HttpServletResponse response) throws IOException {
        long currentUserId = auth.getCurrentUser().getId();
        List<Ticket> tickets = ticketRepository.findByUserId(currentUserId);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h1>Lista zgłoszeń</h1>");
        out.println("<ul>");
        for (Ticket ticket : tickets) {
            String safeDescription = StringEscapeUtils.escapeHtml4(ticket.getDescription());
            log.info(safeDescription);
            out.println("<li>" + safeDescription + "</li>");
        }
        out.println("</ul>");
        out.println("<a href=\"/form.html\">Utwórz nowe zgłoszenie</a>");
        out.println("</body></html>");
    }

    @GetMapping("/tickets")
    public String showTickets(Model model) {
        long currentUserId = auth.getCurrentUser().getId();
        List<Ticket> tickets = ticketRepository.findByUserId(currentUserId);
        model.addAttribute("tickets", tickets);
        return "tickets";
    }
}
