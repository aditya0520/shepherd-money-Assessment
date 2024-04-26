package com.shepherdmoney.interviewproject.model;

import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;
    @Transactional
    public void updateCardBalances(UpdateBalancePayload[] payloads) {
        for (UpdateBalancePayload load : payloads) {
            // Attempt to find a credit card by its number using the repository.
            Optional<CreditCard> cardOptional = creditCardRepository.findByNumber(load.getCreditCardNumber());
            if (cardOptional.isEmpty()) {
                // If no credit card is found, throw an exception with a message.
                throw new RuntimeException("Credit card not found for number: " + load.getCreditCardNumber());
            }
            // Get the actual CreditCard object if present.
            CreditCard card = cardOptional.get();
            // Update the balance histories for the card with the provided date and amount.
            updateBalanceHistories(card, load.getBalanceDate(), load.getBalanceAmount());
            // Save the updated card back to the repository to persist changes.
            creditCardRepository.save(card);
        }
    }

    // Method to update balance histories for a given credit card.
    public void updateBalanceHistories(CreditCard card, LocalDate date, double amount) {
        // Create a new balance history entry.
        BalanceHistory newEntry = new BalanceHistory();
        newEntry.setDate(date); // Set the date for the entry.
        newEntry.setBalance(amount); // Set the balance amount for the entry.

        // Find an existing balance history entry for the same date.
        BalanceHistory existingEntry = findBalanceHistoryByDate(card, date);

        if (existingEntry == null) {
            // If no entry exists for this date, add the new entry to the card's balance history.
            newEntry.setCreditCard(card);
            card.getBalanceHistories().add(newEntry);
        } else {
            // If an entry exists, update it and adjust subsequent balance entries.
            double difference = amount - existingEntry.getBalance();
            existingEntry.setBalance(amount);
            adjustSubsequentBalances(card, date, difference);
        }
        // Ensure that there are no gaps in the balance history.
        fillGaps(card);
    }

    // Method to find a balance history entry for a specific date.
    private BalanceHistory findBalanceHistoryByDate(CreditCard card, LocalDate date) {
        // Iterate over the balance histories of the card to find a match for the date.
        Iterator<BalanceHistory> iterator = card.getBalanceHistories().iterator();
        while (iterator.hasNext()) {
            BalanceHistory history = iterator.next();
            if (history.getDate().equals(date)) {
                // Return the matching history entry.
                return history;
            }
        }
        return null; // Return null if no match is found.
    }

    // Method to adjust all subsequent balance histories after a certain date.
    private void adjustSubsequentBalances(CreditCard card, LocalDate date, double difference) {
        boolean update = false;
        for (BalanceHistory history : card.getBalanceHistories()) {
            if (update) {
                // Update the balance by adding the difference.
                history.setBalance(history.getBalance() + difference);
            }
            if (history.getDate().equals(date)) {
                // Start updates from the next entry after the date match.
                update = true;
            }
        }
    }

    // Method to fill any missing balance history entries between dates.
    private void fillGaps(CreditCard card) {
        // A new TreeSet to store the new entries that fill the gaps.
        Set<BalanceHistory> newEntries = new TreeSet<>(new Comparator<BalanceHistory>() {
            @Override
            public int compare(BalanceHistory o1, BalanceHistory o2) {
                // Comparator to sort the histories in descending order by date.
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        BalanceHistory previous = null;
        for (BalanceHistory current : card.getBalanceHistories()) {
            // Check if there's a gap between the current entry and the previous one.
            if (previous != null && !current.getDate().minusDays(1).equals(previous.getDate())) {
                LocalDate date = previous.getDate().plusDays(1);
                // Fill the gap by creating new balance history entries for each missing day.
                while (!date.equals(current.getDate())) {
                    BalanceHistory fill = new BalanceHistory();
                    fill.setDate(date); // Set the date for the new entry.
                    fill.setBalance(current.getBalance()); // Set the balance amount to match the current entry.
                    fill.setCreditCard(card); // Associate the new entry with the credit card.
                    newEntries.add(fill); // Add the new entry to the set.
                    date = date.plusDays(1); // Increment the date by one day.
                }
            }
            previous = current; // Update the previous entry reference to the current entry.
        }
        // Add all the new entries to the card's balance history in one go.
        card.getBalanceHistories().addAll(newEntries);
    }
}
