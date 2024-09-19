package me.vilius.cerulean.service;

import me.vilius.cerulean.model.*;
import me.vilius.cerulean.repository.DeliveryRepository;
import me.vilius.cerulean.repository.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private UserRatingRepository userRatingRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public UserRating rateSeller(Auction auction, User buyer, int rating, String review) {
        if (auction.getStatus() != AuctionStatus.FINISHED) {
            throw new IllegalStateException("Cannot rate the seller. Auction is still in progress.");
        }

        Optional<UserRating> existingRating = userRatingRepository.findByTransactionAndBuyer(auction, buyer);
        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("You have already rated this auction.");
        }

        if (!auction.getBuyer().equals(buyer)) {
            throw new IllegalArgumentException("Only the buyer can rate the seller.");
        }

        Delivery deliveryConversation = deliveryRepository.findByAuction(auction)
                .orElseThrow(() -> new IllegalArgumentException("No delivery conversation exists for this auction."));

        if (!deliveryConversation.getStatus().equals(Delivery.DeliveryStatus.DELIVERED)) {
            throw new IllegalStateException("Cannot rate the seller. Delivery is not yet completed.");
        }

        UserRating userRating = new UserRating();
        userRating.setTransaction(auction);
        userRating.setBuyer(buyer);
        userRating.setSeller(auction.getSeller());
        userRating.setRating(rating);
        userRating.setReview(review);

        userRating = userRatingRepository.save(userRating);

        notificationService.sendDeliveryRatingNotification(auction.getSeller().getId(),
                deliveryConversation.getId(), rating);

        deliveryService.linkRating(auction, userRating);

        return userRating;
    }
}

