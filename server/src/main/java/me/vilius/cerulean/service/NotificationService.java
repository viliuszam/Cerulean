package me.vilius.cerulean.service;

import me.vilius.cerulean.controller.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
                "You have been outbid for \"" + auctionName + "\"! New bid: $" + newBidAmount);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }

    public void sendAuctionEndedNotification(Long userId, Long auctionId) {
        NotificationMessage message = new NotificationMessage("AUCTION_ENDED", userId, auctionId, "The auction you participated in has ended.");
        messagingTemplate.convertAndSendToUser(userId.toString(), "/topic/notifications", message);
    }
}