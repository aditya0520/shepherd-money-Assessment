package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "MyUser")
// Define a User class that will be mapped to a database table.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; // Unique identifier for the user.

    private String name; // Store the user's name.

    private String email; // Store the user's email.

    // Establish a one-to-many relationship between User and CreditCard entities.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CreditCard> creditCards = new HashSet<>();
}
