package me.vilius.cerulean.controller.dto;

// abridged version of bid to avoid recursion in the response
public class BidDto {
    private double amount;
    private String timestamp;
    private String bidderUsername;

    public BidDto(double amount, String timestamp, String bidderUsername) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.bidderUsername = bidderUsername;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBidderUsername() {
        return bidderUsername;
    }

    public void setBidderUsername(String bidderUsername) {
        this.bidderUsername = bidderUsername;
    }


}