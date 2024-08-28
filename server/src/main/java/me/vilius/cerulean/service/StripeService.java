package me.vilius.cerulean.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TransferCreateParams;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.WithdrawalRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    public PaymentIntent createPaymentIntent(BigDecimal amount, User user, String currency) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency(currency)
                .putMetadata("username", user.getUsername())
                .build();

        return PaymentIntent.create(params);
    }

    public String processPayout(WithdrawalRequest request) throws Exception {
        User user = request.getUser();
        String stripeAccountId = user.getStripeAccountId();

        if (stripeAccountId == null) {
            throw new Exception("User does not have a connected Stripe account.");
        }

        String transferGroup = "WITHDRAW_REQUEST_" + request.getId();
        Long amount = request.getAmount().multiply(new BigDecimal(100)).longValue();
        TransferCreateParams params =
                TransferCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency("eur")
                        .setDestination(stripeAccountId)
                        .setTransferGroup(transferGroup)
                        .build();

        try {
            Transfer transfer = Transfer.create(params);
            PayoutCreateParams pr =
                    PayoutCreateParams.builder().setAmount
                                    (request.getAmount().multiply(new BigDecimal(100)).longValue())
                            .setCurrency("eur")
                            .setMethod(PayoutCreateParams.Method.STANDARD)
                            .setSourceType(PayoutCreateParams.SourceType.CARD) // might not be necessary
                            .build();

            RequestOptions ro =
                    RequestOptions.builder().setStripeAccount(stripeAccountId
                    ).build();

            Payout payout = Payout.create(pr, ro);

            return payout.getId();
        } catch (StripeException e) {
            throw new Exception("Stripe payout failed: " + e.getMessage());
        }
    }
}
