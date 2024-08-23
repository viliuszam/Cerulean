package me.vilius.cerulean.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MyBidsResponse {
    private Long auctionId;
    private String itemName;
    private String description;
    private LocalDateTime endDate;
    private Double highestBid;
    private Double userBidAmount;
    private String userBidStatus; // possible values: "TOP_BIDDER", "OUTBID", "WON", "LOST"
    private List<String> imageUrls;
    private Boolean auctionEnded;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
    }

    public Double getUserBidAmount() {
        return userBidAmount;
    }

    public void setUserBidAmount(Double userBidAmount) {
        this.userBidAmount = userBidAmount;
    }

    public String getUserBidStatus() {
        return userBidStatus;
    }

    public void setUserBidStatus(String userBidStatus) {
        this.userBidStatus = userBidStatus;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Boolean getAuctionEnded() {
        return auctionEnded;
    }

    public void setAuctionEnded(Boolean auctionEnded) {
        this.auctionEnded = auctionEnded;
    }
}
