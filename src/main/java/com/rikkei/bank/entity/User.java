package com.rikkei.bank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private Boolean isActive;

    private Boolean isKyc;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private KycProfile kycProfile;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    private List<TokenBlacklist> tokenBlacklists;

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (isActive == null) {
            isActive = true;
        }

        if (isKyc == null) {
            isKyc = false;
        }
    }
}