package me.vilius.cerulean.service;

import me.vilius.cerulean.model.Payment;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.WithdrawalRequest;
import me.vilius.cerulean.repository.PaymentRepository;
import me.vilius.cerulean.repository.UserRepository;
import me.vilius.cerulean.repository.WithdrawalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Autowired
    private StripeService stripeService;

    public Payment createPayment(User user, BigDecimal amount, String stripePaymentIntentId, Payment.PaymentType type) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setTimestamp(LocalDateTime.now());
        payment.setStripePaymentIntentId(stripePaymentIntentId);
        payment.setType(type);
        paymentRepository.save(payment);

        if (type == Payment.PaymentType.DEPOSIT) {
            user.setBalance(user.getBalance().add(amount));
        } else if (type == Payment.PaymentType.REFUND) {
            user.setBalance(user.getBalance().subtract(amount));
        }
        userRepository.save(user);

        return payment;
    }

    public WithdrawalRequest createWithdrawalRequest(User user, BigDecimal amount) {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setUser(user);
        request.setAmount(amount);
        request.setRequestTime(LocalDateTime.now());
        request.setStatus(WithdrawalRequest.Status.PENDING);
        user.setBalance(user.getBalance().subtract(request.getAmount()));
        userRepository.save(user);
        withdrawalRequestRepository.save(request);

        return request;
    }

    public List<WithdrawalRequest> getAllPendingRequests() {
        return withdrawalRequestRepository.findAllByStatus(WithdrawalRequest.Status.PENDING);
    }

    public void approveWithdrawalRequest(Long requestId, String comment) throws Exception {
        WithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal request not found"));

        if (request.getStatus() != WithdrawalRequest.Status.PENDING) {
            throw new IllegalStateException("Request is not in pending status");
        }

        String payoutId = stripeService.processPayout(request);

        request.setStatus(WithdrawalRequest.Status.APPROVED);
        request.setPayoutId(payoutId);
        request.setAdminComment(comment);
        withdrawalRequestRepository.save(request);
    }

    public void denyWithdrawalRequest(Long requestId, String comment) {
        WithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal request not found"));

        if (request.getStatus() != WithdrawalRequest.Status.PENDING) {
            throw new IllegalStateException("Request is not in pending status");
        }

        User user = request.getUser();
        // refund the user to account balance if we deny withdrawal
        user.setBalance(user.getBalance().add(request.getAmount()));
        userRepository.save(user);
        request.setStatus(WithdrawalRequest.Status.DENIED);
        request.setAdminComment(comment);
        withdrawalRequestRepository.save(request);
    }

}