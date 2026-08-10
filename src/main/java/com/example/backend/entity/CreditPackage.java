package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "packages")
@Getter
@Setter
public class CreditPackage extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "credits", nullable = false)
    private int credits;

    @Column(name = "active", nullable = false)
    private boolean active = true;

}
