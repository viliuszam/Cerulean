package me.vilius.cerulean.controller.dto;

import me.vilius.cerulean.model.Delivery;

import java.util.List;

public class ShortDeliveryResponse {
    private Long id;
    private Long auctionId;
    private String itemName;
    private Delivery.DeliveryStatus status;
    private String trackingInfo;
    private List<String> auctionImageUrls;
    private String sellerUsername;
    private String buyerUsername;

    public ShortDeliveryResponse(Long id, Long auctionId, String itemName, Delivery.DeliveryStatus status,
                                 String trackingInfo, List<String> auctionImageUrls,
                                 String sellerUsername, String buyerUsername) {
        this.id = id;
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.status = status;
        this.trackingInfo = trackingInfo;
        this.auctionImageUrls = auctionImageUrls;
        this.sellerUsername = sellerUsername;
        this.buyerUsername = buyerUsername;
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

    public List<String> getAuctionImageUrls() {
        return auctionImageUrls;
    }

    public void setAuctionImageUrls(List<String> auctionImageUrls) {
        this.auctionImageUrls = auctionImageUrls;
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
}
