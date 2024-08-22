package me.vilius.cerulean.controller.dto;

import me.vilius.cerulean.model.Bid;

public class BidResponse {
    private boolean success;
    private String message;
    private Bid bid;

    public BidResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BidResponse(boolean success, String message, Bid data) {
        this.success = success;
        this.message = message;
        this.bid = bid;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bid getData() {
        return bid;
    }

    public void setData(Bid data) {
        this.bid = data;
    }
}