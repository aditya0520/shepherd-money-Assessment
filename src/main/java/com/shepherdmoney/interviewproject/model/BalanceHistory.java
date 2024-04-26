package com.shepherdmoney.interviewproject.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BalanceHistory implements Comparable<BalanceHistory> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; // Unique identifier for the balance history record.

    private LocalDate date; // Stores the date of the balance history entry.

    private double balance; // Stores the balance amount for the given date.

    @ManyToOne
    @JoinColumn(name = "creditCardId")
    private CreditCard creditCard; // The associated credit card for this balance history.

    @Override
    public int compareTo(BalanceHistory other) {
        return this.date.compareTo(other.date); // For sorting, it compares balance history entries by date.
    }
}