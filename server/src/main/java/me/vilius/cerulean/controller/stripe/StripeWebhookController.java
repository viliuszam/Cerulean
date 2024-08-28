package me.vilius.cerulean.controller.stripe;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import me.vilius.cerulean.model.Payment;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.service.PaymentService;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/webhook")
@CrossOrigin("*")
public class StripeWebhookController {

    @Value("${stripe.endpoint}")
    private String stripeEndpointSecret;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeEndpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook Error: " + e.getMessage());
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
                String paymentIntentId = paymentIntent.getId();
                BigDecimal sum = new BigDecimal(paymentIntent.getAmount() / 100);
                User user = userService.findByUsername(paymentIntent.getMetadata().get("username"));
                paymentService.createPayment(user, sum, paymentIntentId, Payment.PaymentType.DEPOSIT);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok().build();
    }
}