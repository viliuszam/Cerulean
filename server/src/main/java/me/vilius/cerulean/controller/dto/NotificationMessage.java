package me.vilius.cerulean.controller.dto;

public class NotificationMessage {
    private String type; // OUTBID, AUCTION_ENDED
    private Long userId;
    private Long auctionId;
    private String message;

    public NotificationMessage(String type, Long userId, Long auctionId, String message) {
        this.type = type;
        this.userId = userId;
        this.auctionId = auctionId;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}