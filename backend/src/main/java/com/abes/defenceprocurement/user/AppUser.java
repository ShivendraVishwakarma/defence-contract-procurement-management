package com.abes.defenceprocurement.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "APP_USERS", uniqueConstraints = {
        @UniqueConstraint(name = "UK_APP_USERS_USERNAME", columnNames = "USERNAME"),
        @UniqueConstraint(name = "UK_APP_USERS_EMAIL", columnNames = "EMAIL")
})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 50)
    private String username;

    @Column(name = "DISPLAY_NAME", nullable = false, length = 120)
    private String displayName;

    @Column(name = "EMAIL", nullable = false, length = 160)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "ACTIVE_FLAG", nullable = false, length = 1)
    private String activeFlag;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "APP_USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Set<AppRole> roles = new HashSet<>();

    protected AppUser() { }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return "Y".equals(activeFlag); }
    public Set<AppRole> getRoles() { return Set.copyOf(roles); }
}
