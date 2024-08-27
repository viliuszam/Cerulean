package me.vilius.cerulean.controller;

import me.vilius.cerulean.model.Payment;
import me.vilius.cerulean.model.Role;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.WithdrawalRequest;
import me.vilius.cerulean.repository.UserRepository;
import me.vilius.cerulean.repository.WithdrawalRequestRepository;
import me.vilius.cerulean.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/withdrawal-requests")
    public ResponseEntity<?> getAllWithdrawalRequests() {
        return ResponseEntity.ok(withdrawalRequestRepository.findAll());
    }

    @PostMapping("/approve-withdrawal/{id}")
    public ResponseEntity<?> approveWithdrawal(Principal principal,
                                               @PathVariable Long id, @RequestParam(required = false) String comment) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        Optional<User> requestUser = userRepository.findByUsername(principal.getName());
        if (!requestUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if(requestUser.get().getRole() != Role.ADMIN){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrator permissions required.");
        }

        Optional<WithdrawalRequest> optionalRequest = withdrawalRequestRepository.findById(id);
        if (optionalRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WithdrawalRequest request = optionalRequest.get();
        if (request.getStatus() != WithdrawalRequest.Status.PENDING) {
            return ResponseEntity.badRequest().body("This withdrawal request has already been processed.");
        }

        User user = request.getUser();
        BigDecimal amount = request.getAmount();
        paymentService.createPayment(user, amount, null, Payment.PaymentType.REFUND);
        request.setStatus(WithdrawalRequest.Status.APPROVED);
        request.setAdminComment(comment);
        withdrawalRequestRepository.save(request);

        return ResponseEntity.ok("Withdrawal approved and processed successfully.");
    }

    @PostMapping("/deny-withdrawal/{id}")
    public ResponseEntity<?> denyWithdrawal(Principal principal,
                                            @PathVariable Long id, @RequestParam String comment) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        Optional<User> requestUser = userRepository.findByUsername(principal.getName());
        if (!requestUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if(requestUser.get().getRole() != Role.ADMIN){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrator permissions required.");
        }

        Optional<WithdrawalRequest> optionalRequest = withdrawalRequestRepository.findById(id);
        if (optionalRequest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WithdrawalRequest request = optionalRequest.get();
        if (request.getStatus() != WithdrawalRequest.Status.PENDING) {
            return ResponseEntity.badRequest().body("This withdrawal request has already been processed.");
        }

        request.setStatus(WithdrawalRequest.Status.DENIED);
        request.setAdminComment(comment);
        withdrawalRequestRepository.save(request);

        return ResponseEntity.ok("Withdrawal denied successfully.");
    }
}
