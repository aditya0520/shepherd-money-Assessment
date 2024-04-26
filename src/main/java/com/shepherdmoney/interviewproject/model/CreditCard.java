package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id; // Unique identifier for a credit card.

    private String issuanceBank; // Store the name of the bank that issued the credit card.

    private String number; // Store the credit card number.

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // This establishes a one-to-many relationship between CreditCard and BalanceHistory entities:
    private SortedSet<BalanceHistory> balanceHistories = new TreeSet<>(new Comparator<BalanceHistory>() {
        @Override
        public int compare(BalanceHistory o1, BalanceHistory o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }); // Holds a sorted set of balance history objects, sorted by date in descending order.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user; // The user that owns this credit card.
}
