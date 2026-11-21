package com.abes.defenceprocurement.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "APP_ROLES", uniqueConstraints = @UniqueConstraint(name = "UK_APP_ROLES_CODE", columnNames = "ROLE_CODE"))
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Long id;

    @Column(name = "ROLE_CODE", nullable = false, length = 40)
    private String code;

    @Column(name = "ROLE_NAME", nullable = false, length = 80)
    private String name;

    protected AppRole() { }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
}
