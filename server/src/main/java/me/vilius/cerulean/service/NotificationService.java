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
}