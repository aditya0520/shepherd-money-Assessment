package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.CreditCardService;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CreditCardService creditCardService;


    @PostMapping("/credit-card")
    public ResponseEntity<String> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        // Find the user by ID provided in the payload. If not found, return 'null'.
        User user = userRepository.findById(payload.getUserId()).orElse(null);
        if (user == null) {
            // If the user doesn't exist, return a 400 Bad Request with an error message.
            return ResponseEntity.badRequest().body("User not found");
        }
        // Create a new CreditCard object and set its properties from the payload.
        CreditCard newCreditCard = new CreditCard();
        newCreditCard.setIssuanceBank(payload.getCardIssuanceBank()); // Set the bank that issued the card.
        newCreditCard.setNumber(payload.getCardNumber()); // Set the credit card number.
        newCreditCard.setUser(user); // Associate the credit card with the found user.

        // Add the new credit card to the user's set of credit cards.
        user.getCreditCards().add(newCreditCard);
        // Persist the user entity, which will cascade and save the new credit card as well.
        userRepository.save(user);
        // Return a 200 OK response indicating the credit card was added successfully.
        return ResponseEntity.ok("Credit card added successfully with ID: " + payload.getUserId());
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        // Fetch all credit cards from the repository by the provided user ID.
        List<CreditCard> cards = creditCardRepository.findByUserId(userId);
        // Check if the user has no credit cards and return an empty list as the response if true.
        if (cards.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        // If credit cards are found, prepare a list of CreditCardView objects to send as the response.
        List<CreditCardView> cardViews = new ArrayList<>();
        for (CreditCard card : cards) {
            // Create a view model for each credit card containing its bank and number.
            CreditCardView cardView = new CreditCardView(card.getIssuanceBank(), card.getNumber());
            // Add the view model to the list.
            cardViews.add(cardView);
        }
        // Return the list of credit card view models in the response body.
        return ResponseEntity.ok(cardViews);
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        // Attempt to find a CreditCard entity based on the credit card number provided.
        Optional<CreditCard> creditCard = creditCardRepository.findByNumber(creditCardNumber);
        // Check if the Optional<CreditCard> is empty, which would mean no matching credit card was found.
        if (creditCard.isEmpty()) {
            // Respond with a 400 Bad Request status and no body if the credit card is not found.
            return ResponseEntity.badRequest().body(null);
        }
        // If a credit card is found, extract the user ID associated with this credit card and return it.
        return ResponseEntity.ok(creditCard.get().getUser().getId());
    }

    @PostMapping("/credit-card:update-balance")
    public ResponseEntity<String> postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        try {
            // Calls the service method to update the card balances with the provided payload.
            creditCardService.updateCardBalances(payload);
            // If the update is successful, return an HTTP 200 OK response with a success message.
            return ResponseEntity.ok("All balances updated successfully.");
        } catch (Exception e) {
            // If there is any exception during the update process, catch the exception.
            // Return an HTTP 400 Bad Request response with the exception message.
            return ResponseEntity.badRequest().body("Failed to update: " + e.getMessage());
        }
    }
}
