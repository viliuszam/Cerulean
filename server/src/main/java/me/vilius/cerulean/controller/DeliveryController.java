package me.vilius.cerulean.controller;

import com.fasterxml.jackson.annotation.JsonView;
import me.vilius.cerulean.controller.dto.FullDeliveryResponse;
import me.vilius.cerulean.controller.dto.MessageResponse;
import me.vilius.cerulean.controller.dto.RatingRequest;
import me.vilius.cerulean.controller.dto.ShortDeliveryResponse;
import me.vilius.cerulean.model.*;
import me.vilius.cerulean.service.DeliveryService;
import me.vilius.cerulean.service.RatingService;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<List<ShortDeliveryResponse>> getUserDeliveries(
            Principal principal,
            @RequestParam(name = "role", defaultValue = "buyer") String role) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!role.equalsIgnoreCase("buyer") && !role.equalsIgnoreCase("seller")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = userService.findByUsername(principal.getName());
        List<ShortDeliveryResponse> deliveries = deliveryService.getAllDeliveries(user, role.toLowerCase());

        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<FullDeliveryResponse> getDeliveryById(@PathVariable Long deliveryId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findByUsername(principal.getName());
        FullDeliveryResponse delivery = deliveryService.getDeliveryById(user, deliveryId);

        if (!delivery.getBuyerUsername().equals(user.getUsername())
                && !delivery.getSellerUsername().equals(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(delivery);
    }

    @PostMapping("/{deliveryId}/message")
    public ResponseEntity<?> sendMessage(@PathVariable Long deliveryId, @RequestBody String content, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        Delivery delivery = deliveryService.getDeliveryObjectById(deliveryId);
        Message message = deliveryService.sendMessage(delivery, sender, content);
        return ResponseEntity.ok(new MessageResponse(message.getId(), sender.getId(), sender.getUsername(), content, message.getSentAt()));
    }

    @PutMapping("/{deliveryId}/tracking")
    public ResponseEntity<?> updateTrackingInfo(@PathVariable Long deliveryId, @RequestBody String trackingInfo, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Delivery delivery = deliveryService.getDeliveryObjectById(deliveryId);
        if (!delivery.getAuction().getSeller().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the seller can update tracking info");
        }
        deliveryService.updateTrackingInfo(deliveryId, trackingInfo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long deliveryId, @RequestBody Delivery.DeliveryStatus status, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Delivery delivery = deliveryService.getDeliveryObjectById(deliveryId);
        if (!delivery.getAuction().getSeller().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the seller can update the status");
        }
        deliveryService.updateStatus(deliveryId, status);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{deliveryId}/confirm")
    public ResponseEntity<?> confirmDelivery(@PathVariable Long deliveryId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        deliveryService.markAsDelivered(deliveryId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rate/{deliveryId}")
    public ResponseEntity<?> rateSeller(@PathVariable Long deliveryId, @RequestBody RatingRequest ratingRequest, Principal principal) {
        User buyer = userService.findByUsername(principal.getName());
        Delivery delivery = deliveryService.getDeliveryObjectById(deliveryId);
        UserRating rating = ratingService.rateSeller(delivery.getAuction(), buyer, ratingRequest.getRating(), ratingRequest.getReview());
        return ResponseEntity.ok(rating);
    }

}
