package me.vilius.cerulean.service;

import com.stripe.model.tax.Registration;
import me.vilius.cerulean.controller.dto.FullDeliveryResponse;
import me.vilius.cerulean.controller.dto.MessageResponse;
import me.vilius.cerulean.controller.dto.ShortDeliveryResponse;
import me.vilius.cerulean.model.*;
import me.vilius.cerulean.repository.DeliveryRepository;
import me.vilius.cerulean.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationService notificationService;

    public Delivery getDeliveryObjectById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
    }

    @Transactional
    public void updateStatus(Long deliveryId, Delivery.DeliveryStatus status) {
        Delivery delivery = getDeliveryObjectById(deliveryId);
        delivery.setStatus(status);
        deliveryRepository.save(delivery);
    }

    public FullDeliveryResponse getDeliveryById(User requester, Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        List<MessageResponse> messageDTOs = delivery.getMessages().stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getSender().getId(),
                        message.getSender().getUsername(),
                        message.getContent(),
                        message.getSentAt()
                ))
                .collect(Collectors.toList());

        return new FullDeliveryResponse(
                delivery.getId(),
                delivery.getAuction().getId(),
                delivery.getAuction().getItemName(),
                delivery.getStatus(),
                delivery.getTrackingInfo(),
                delivery.getDeliveryConfirmedAt(),
                delivery.getAuction().getSeller().getUsername(),
                delivery.getAuction().getBuyer() != null ? delivery.getAuction().getBuyer().getUsername() : null,
                delivery.getAuction().getImageUrls(),
                messageDTOs,
                delivery.getAuction().getSeller().getId().equals(requester.getId()),
                delivery.getUserRating() != null ? delivery.getUserRating() : null
        );
    }

    public List<ShortDeliveryResponse> getAllDeliveries(User user, String role) {
        List<Delivery> deliveries = role.equals("buyer") ?
                deliveryRepository.findByAuctionBuyer(user) :
                deliveryRepository.findByAuctionSeller(user);

        return deliveries.stream()
                .map(delivery -> new ShortDeliveryResponse(
                        delivery.getId(),
                        delivery.getAuction().getId(),
                        delivery.getAuction().getItemName(),
                        delivery.getStatus(),
                        delivery.getTrackingInfo(),
                        delivery.getAuction().getImageUrls(),
                        delivery.getAuction().getSeller().getUsername(),
                        delivery.getAuction().getBuyer() != null ? delivery.getAuction().getBuyer().getUsername() : null
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Delivery createDelivery(Auction auction) {
        Delivery delivery = new Delivery();
        delivery.setAuction(auction);
        delivery.setStatus(Delivery.DeliveryStatus.PENDING);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Message sendMessage(Delivery conversation, User sender, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        User recipient = sender.getId().equals(conversation.getAuction().getSeller().getId()) ?
                conversation.getAuction().getBuyer() : conversation.getAuction().getSeller();
        notificationService.sendDeliveryMessageNotification(recipient.getId(), conversation.getId(), sender.getUsername());
        return messageRepository.save(message);
    }

    @Transactional
    public void updateTrackingInfo(Long conversationId, String trackingInfo) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(conversationId);
        if (deliveryOptional.isPresent()) {
            Delivery delivery = deliveryOptional.get();
            delivery.setTrackingInfo(trackingInfo);
            if(!delivery.getStatus().equals(Delivery.DeliveryStatus.IN_TRANSIT)){
                User buyer = delivery.getAuction().getBuyer();
                delivery.setStatus(Delivery.DeliveryStatus.IN_TRANSIT);
                notificationService.sendDeliveryStatusChangeNotification(buyer.getId(), delivery.getId(), "IN TRANSIT");
            }
            deliveryRepository.save(delivery);
        }
    }

    @Transactional
    public void markAsDelivered(Long conversationId, User currentUser) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(conversationId);
        if (deliveryOptional.isPresent()) {
            Delivery delivery = deliveryOptional.get();
            if (!delivery.getAuction()
                    .getBuyer().equals(currentUser)) {
                throw new IllegalArgumentException("Only the buyer can mark the delivery as completed.");
            }
            delivery.setStatus(Delivery.DeliveryStatus.DELIVERED);
            User seller = delivery.getAuction().getSeller();
            notificationService.sendDeliveryStatusChangeNotification(seller.getId(), delivery.getId(), "DELIVERED");
            delivery.setDeliveryConfirmedAt(LocalDateTime.now());
            deliveryRepository.save(delivery);
        }
    }

    @Transactional
    public Delivery linkRating(Auction auction, UserRating userRating) {
        Delivery delivery = deliveryRepository.findByAuction(auction)
                .orElseThrow(() -> new IllegalArgumentException("Delivery conversation not found."));

        delivery.setUserRating(userRating);
        return deliveryRepository.save(delivery);
    }
}
