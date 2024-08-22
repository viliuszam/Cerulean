package me.vilius.cerulean.util;

import me.vilius.cerulean.controller.dto.AuctionResponse;
import me.vilius.cerulean.controller.dto.BidDto;
import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.Bid;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.UserRating;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class ConversionUtil {

    public static AuctionResponse convertAuctionToDto(Auction auction) {
        AuctionResponse dto = new AuctionResponse();
        dto.setId(auction.getId());
        dto.setItemName(auction.getItemName());
        dto.setDescription(auction.getDescription());
        dto.setStartingPrice(auction.getStartingBid());
        dto.setStartDate(auction.getStartDate());
        dto.setEndDate(auction.getEndDate());
        dto.setImageUrls(auction.getImageUrls());
        dto.setBuyItNowPrice(auction.getBuyItNowPrice());
        dto.setReservePrice(auction.getReservePrice());
        dto.setStatus(auction.getStatus());

        User seller = auction.getSeller();
        dto.setSellerName(seller.getUsername());
        dto.setSellerSignupDate(seller.getSignupDate());

        List<BidDto> bidDtos = auction.getBids().stream()
                .sorted(Comparator.comparingDouble(Bid::getAmount).reversed())
                .map(bid -> new BidDto(
                        bid.getAmount(),
                        bid.getTimestamp().toString(),
                        StringUtil.censorUsername(bid.getBidder().getUsername())
                ))
                .collect(Collectors.toList());
        dto.setBids(bidDtos);

        List<UserRating> ratings = seller.getReceivedRatings();
        OptionalDouble averageRating = ratings.stream().mapToInt(UserRating::getRating).average();
        dto.setSellerAverageRating(averageRating.isPresent() ? averageRating.getAsDouble() : 0.0);

        return dto;
    }
}
