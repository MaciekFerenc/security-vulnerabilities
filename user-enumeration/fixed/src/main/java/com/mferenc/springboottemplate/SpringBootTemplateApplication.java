package com.mferenc.springboottemplate;

import com.mferenc.springboottemplate.auth.User;
import com.mferenc.springboottemplate.auth.UserRepository;
import com.mferenc.springboottemplate.tickets.Ticket;
import com.mferenc.springboottemplate.tickets.TicketRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Configuration
public class SpringBootTemplateApplication {

    private final UserRepository userRepository;

    private final TicketRepository ticketRepository;

    private final PasswordEncoder passwordEncoder;

    public SpringBootTemplateApplication(UserRepository userRepository, TicketRepository ticketRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTemplateApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertData() {
        if (userRepository.count() == 0 && ticketRepository.count() == 0) {
            createDefaultUsers();
            createSampleTicketData();
        }
        addTestUsers();
    }

    private void createDefaultUsers() {
        User user1 = new User("user1", passwordEncoder.encode("haslo"), "test@test.com");
        User user2 = new User("user2", passwordEncoder.encode("haslo"), "test2@test.com");
        User admin = new User("admin", passwordEncoder.encode("admin"), "test3@test.com");
        admin.setRole(User.Role.ADMIN);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(admin);
    }

    private void addTestUsers() {
        try {
            ClassPathResource resource = new ClassPathResource("usernames.txt");
            List<String> usernames = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    usernames.add(line);
                }
            }
            System.out.println(usernames.size());

            for (String username : usernames) {
                username = username.trim();

                if (username.isEmpty()) {
                    continue;
                }

                if (username.startsWith("ex_")) {
                    if (userRepository.findByUsername(username).isEmpty()) {
                        User user = new User(username, passwordEncoder.encode("haslo"), username + "@test.com");
                        userRepository.save(user);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createSampleTicketData() {
        if (userRepository.existsById(1L) && userRepository.existsById(2L)) {
            // 10 sample tickets
            var tickets = List.of(
                    new Ticket("Nie działa mi drukarka w pokoju 302. Po włączeniu miga tylko czerwona dioda.", 1L),
                    new Ticket("Zgubiłem hasło do panelu logowania. Proszę o reset i przesłanie nowego na maila.", 1L),
                    new Ticket("System CRM działa bardzo wolno od rana. Sprawdzałem z różnych komputerów, bez zmian.", 1L),
                    new Ticket("Na koncie brakuje mi uprawnień do edycji zamówień. Potrzebuję pilnie na dzisiaj.", 1L),
                    new Ticket("W aplikacji mobilnej pojawia się błąd 403 przy próbie synchronizacji.", 1L),
                    new Ticket("Nie mogę połączyć się z VPN-em z domu. Login i hasło są poprawne.", 2L),
                    new Ticket("Potrzebuję nowej licencji do AutoCAD-a. Obecna wygasła w zeszłym tygodniu.", 2L),
                    new Ticket("Outlook nie odbiera nowych maili. Pojawia się komunikat o błędzie połączenia z serwerem.", 2L),
                    new Ticket("Ktoś nieautoryzowany korzystał z mojego konta. W historii logowania widzę IP z Niemiec.", 2L),
                    new Ticket("Proszę o utworzenie nowego konta dla praktykanta. Start pracy: poniedziałek 8:00.", 2L)
            );

            ticketRepository.saveAll(tickets);
        }
    }
}
