package me.vilius.cerulean.controller.dto;

import me.vilius.cerulean.model.Delivery;
import me.vilius.cerulean.model.UserRating;

import java.time.LocalDateTime;
import java.util.List;

public class FullDeliveryResponse {
    private Long id;
    private Long auctionId;
    private String itemName;
    private Delivery.DeliveryStatus status;
    private String trackingInfo;
    private LocalDateTime deliveryConfirmedAt;
    private String sellerUsername;
    private String buyerUsername;
    private List<String> auctionImageUrls;
    private List<MessageResponse> messages;
    private boolean isSeller;
    private UserRating userRating;

    public FullDeliveryResponse(Long id, Long auctionId, String itemName, Delivery.DeliveryStatus status,
                                String trackingInfo, LocalDateTime deliveryConfirmedAt, String sellerUsername, String buyerUsername,
                                List<String> auctionImageUrls, List<MessageResponse> messages, boolean seller,
                                UserRating userRating) {
        this.id = id;
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.status = status;
        this.trackingInfo = trackingInfo;
        this.deliveryConfirmedAt = deliveryConfirmedAt;
        this.sellerUsername = sellerUsername;
        this.buyerUsername = buyerUsername;
        this.auctionImageUrls = auctionImageUrls;
        this.messages = messages;
        this.isSeller = seller;
        this.userRating = userRating;
    }

    public boolean isSeller() {
        return isSeller;
    }

    public void setSeller(boolean seller) {
        isSeller = seller;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Delivery.DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(Delivery.DeliveryStatus status) {
        this.status = status;
    }

    public String getTrackingInfo() {
        return trackingInfo;
    }

    public void setTrackingInfo(String trackingInfo) {
        this.trackingInfo = trackingInfo;
    }

    public LocalDateTime getDeliveryConfirmedAt() {
        return deliveryConfirmedAt;
    }

    public void setDeliveryConfirmedAt(LocalDateTime deliveryConfirmedAt) {
        this.deliveryConfirmedAt = deliveryConfirmedAt;
    }

    public UserRating getUserRating() {
        return userRating;
    }

    public void setUserRating(UserRating userRating) {
        this.userRating = userRating;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public List<String> getAuctionImageUrls() {
        return auctionImageUrls;
    }

    public void setAuctionImageUrls(List<String> auctionImageUrls) {
        this.auctionImageUrls = auctionImageUrls;
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponse> messages) {
        this.messages = messages;
    }
}
