package me.vilius.cerulean.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
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

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    // TODO: perhaps store these in the database?
    public PaymentIntent createPaymentIntent(BigDecimal amount, User user, String currency) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency(currency)
                .putMetadata("username", user.getUsername())
                .build();

        return PaymentIntent.create(params);
    }

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
        withdrawalRequestRepository.save(request);

        return request;
    }
}