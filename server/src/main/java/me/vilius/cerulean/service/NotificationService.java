package me.vilius.cerulean.service;

import me.vilius.cerulean.controller.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendOutbidNotification(Long userId, Long auctionId,
                                       String auctionName, Double newBidAmount) {
        NotificationMessage message = new NotificationMessage("OUTBID", userId, auctionId,
                "You have been outbid for \"" + auctionName + "\"! New bid: €" + newBidAmount);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }

    public void sendAuctionEndedNotification(Long userId, Long auctionId) {
        NotificationMessage message = new NotificationMessage("AUCTION_ENDED", userId, auctionId, "The auction you participated in has ended.");
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }

    public void sendBalanceUpdateNotification(Long userId, BigDecimal oldBalance, BigDecimal newBalance) {
        BigDecimal balanceDifference = newBalance.subtract(oldBalance);
        boolean increment = balanceDifference.compareTo(BigDecimal.ZERO) > 0;
        String message = balanceDifference.abs() + "€ " +
                (increment ? "has been added to" : "has been taken from")
                + " your account.";
        String type = increment ? "BALANCE_INCREMENT" : "BALANCE_DECREMENT";
        NotificationMessage notificationMessage = new NotificationMessage(type, userId, null, message);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", notificationMessage);
    }

    public void sendDeliveryMessageNotification(Long userId, Long deliveryId, String senderUsername) {
        NotificationMessage message = new NotificationMessage("DELIVERY_MESSAGE", userId, deliveryId,
                "New message from " + senderUsername + " in your delivery chat.");
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }

    public void sendDeliveryRatingNotification(Long userId, Long deliveryId, int rating) {
        NotificationMessage message = new NotificationMessage("DELIVERY_RATING", userId, deliveryId,
                "You received a " + rating + "-star rating for your delivery.");
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }

    public void sendDeliveryStatusChangeNotification(Long userId, Long deliveryId, String newStatus) {
        NotificationMessage message = new NotificationMessage("DELIVERY_STATUS_CHANGE", userId, deliveryId,
                "The status of your delivery has changed to: " + newStatus);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }
}