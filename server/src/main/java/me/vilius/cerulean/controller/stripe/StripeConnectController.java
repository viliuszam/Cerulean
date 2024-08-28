package me.vilius.cerulean.controller.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.repository.UserRepository;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class StripeConnectController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStripeConnectionStatus(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        boolean isConnected = user != null && user.getStripeAccountId() != null;
        return ResponseEntity.ok(Collections.singletonMap("connected", isConnected));
    }

    @GetMapping("/connect")
    public ResponseEntity<String> createAccountLink(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String accountId;
        if (user.getStripeAccountId() == null) {
            Map<String, Object> accountParams = new HashMap<>();
            accountParams.put("type", "express");

            try {
                Account account = Account.create(accountParams);
                accountId = account.getId();
                user.setStripeAccountId(accountId);
                userRepository.save(user);
            } catch (StripeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Stripe account: " + e.getMessage());
            }
        } else {
            accountId = user.getStripeAccountId();
        }

        Map<String, Object> accountLinkParams = new HashMap<>();
        accountLinkParams.put("account", accountId);
        // TODO: CHANGE THIS LATER
        accountLinkParams.put("refresh_url", "http://localhost:3000/withdraw");
        accountLinkParams.put("return_url", "http://localhost:3000/withdraw");
        accountLinkParams.put("type", "account_onboarding");

        try {
            AccountLink accountLink = AccountLink.create(accountLinkParams);
            return ResponseEntity.ok(accountLink.getUrl());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Stripe account link: " + e.getMessage());
        }
    }
}