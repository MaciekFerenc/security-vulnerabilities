package com.mferenc.springboottemplate;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import com.mferenc.springboottemplate.auth.User;
import com.mferenc.springboottemplate.auth.UserRepository;
import com.mferenc.springboottemplate.users.UpdateAccountDetailsRequest;
import com.mferenc.springboottemplate.users.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class UserSensitiveDataTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private UserController userController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void sensitiveUserDetailsAreNotPlainText() {
        var user = createTestUser();
        when(authenticationFacade.getCurrentUser()).thenReturn(user);

        var details = new UpdateAccountDetailsRequest(
                "Jan", "Kowalski", "05212647544"
        );
        userController.updateUserDetails(details);

        Map<String, Object> dbUser = jdbcTemplate.queryForMap(
                "SELECT last_name, pesel FROM users WHERE id = ?",
                user.getId()
        );
        String storedPesel = (String) dbUser.get("pesel");
        String storedLastName = (String) dbUser.get("last_name");

        assertThat(storedPesel).isNotEqualTo(details.pesel());
        assertThat(storedLastName).isNotEqualTo(details.lastName());
    }

    public User createTestUser() {
        User user = new User();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("haslo"));
        userRepository.save(user);
        return user;
    }

}
