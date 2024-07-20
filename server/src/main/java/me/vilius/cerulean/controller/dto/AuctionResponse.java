package me.vilius.cerulean.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionResponse {
    private Long id;
    private String itemName;
    private String description;
    private Double startingPrice;
    private Double buyItNowPrice;
    private Double reservePrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> imageUrls;
    private String sellerName;
    private LocalDateTime sellerSignupDate;
    private double sellerAverageRating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public LocalDateTime getSellerSignupDate() {
        return sellerSignupDate;
    }

    public void setSellerSignupDate(LocalDateTime sellerSignupDate) {
        this.sellerSignupDate = sellerSignupDate;
    }

    public double getSellerAverageRating() {
        return sellerAverageRating;
    }

    public void setSellerAverageRating(double sellerAverageRating) {
        this.sellerAverageRating = sellerAverageRating;
    }

    public Double getBuyItNowPrice() {
        return buyItNowPrice;
    }

    public void setBuyItNowPrice(Double buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }

    public Double getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(Double reservePrice) {
        this.reservePrice = reservePrice;
    }
}
