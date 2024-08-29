package me.vilius.cerulean.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import me.vilius.cerulean.model.Payment;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.WithdrawalRequest;
import me.vilius.cerulean.service.PaymentService;
import me.vilius.cerulean.service.StripeService;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private UserService userService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(Principal principal
            , @RequestParam BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ONE) <= 0) {
                return ResponseEntity.badRequest().body("Deposit must be one euro or larger.");
            }
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }
            User user = userService.findByUsername(principal.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, user, "eur");
            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", paymentIntent.getClientSecret()); // might need source later
            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error creating payment intent: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(Principal principal
            , @RequestParam BigDecimal amount) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Withdrawal amount must be greater than zero.");
        }
        if (user.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance.");
        }
        try {
            WithdrawalRequest withdrawalRequest = paymentService.createWithdrawalRequest(user, amount);
            return ResponseEntity.ok("Withdrawal request created successfully. Awaiting approval.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating withdrawal request: " + e.getMessage());
        }
    }

    @GetMapping("/deposits")
    public ResponseEntity<?> getDepositHistory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            List<Payment> deposits = paymentService.getDepositsByUser(user);
            if (deposits.isEmpty()) {
                return ResponseEntity.ok("No deposits found.");
            }
            return ResponseEntity.ok(deposits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving deposit history: " + e.getMessage());
        }
    }

    @GetMapping("/withdrawal-requests")
    public ResponseEntity<?> getWithdrawalRequestHistory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            List<WithdrawalRequest> deposits = paymentService.getWithdrawalRequests(user);
            if (deposits.isEmpty()) {
                return ResponseEntity.ok("No withdrawal requests found.");
            }
            return ResponseEntity.ok(deposits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving withdrawal request history: " + e.getMessage());
        }
    }

}
