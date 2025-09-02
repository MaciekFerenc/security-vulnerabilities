package com.mferenc.springboottemplate.users;

import com.mferenc.springboottemplate.auth.AuthenticationFacade;
import com.mferenc.springboottemplate.auth.User;
import com.mferenc.springboottemplate.auth.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class UserController {

    private final AuthenticationFacade auth;
    private final UserRepository userRepository;

    public UserController(AuthenticationFacade auth, UserRepository userRepository) {
        this.auth = auth;
        this.userRepository = userRepository;
    }

    @PostMapping("/details")
    @Transactional
    public ResponseEntity<String> updateUserDetails(
            @RequestBody UpdateAccountDetailsRequest request) {
        User user = auth.getCurrentUser();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPesel(request.pesel());
        userRepository.save(user);
        return ResponseEntity.ok("User details updated successfully.");
    }

    @GetMapping("/details")
    public ResponseEntity<AccountDetails> getUserDetails() {
        User user = auth.getCurrentUser();

        AccountDetails details = new AccountDetails(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPesel()
        );
        return ResponseEntity.ok(details);
    }
}
