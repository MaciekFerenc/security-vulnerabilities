package com.mferenc.springboottemplate.auth;

import com.mferenc.springboottemplate.encryption.EncryptionService;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String pesel;

    public User() {
    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Transient
    private static EncryptionService encryptionService;

    public static void setEncryptionService(EncryptionService service) {
        encryptionService = service;
    }

    @PostLoad
    private void decryptFields() {
        try {
            if (lastName != null) lastName = encryptionService.decrypt(lastName);
            if (pesel != null) pesel = encryptionService.decrypt(pesel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt user data", e);
        }
    }

    @PrePersist
    @PreUpdate
    private void encryptFields() {
        try {
            if (lastName != null) lastName = encryptionService.encrypt(lastName);
            if (pesel != null) pesel = encryptionService.encrypt(pesel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt user data", e);
        }
    }
}

