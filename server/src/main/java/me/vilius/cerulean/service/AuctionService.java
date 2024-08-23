package me.vilius.cerulean.service;

import me.vilius.cerulean.controller.dto.AuctionResponse;
import me.vilius.cerulean.controller.dto.MyBidsResponse;
import me.vilius.cerulean.model.*;
import me.vilius.cerulean.repository.AuctionRepository;
import me.vilius.cerulean.repository.BidRepository;
import me.vilius.cerulean.repository.UserRepository;
import me.vilius.cerulean.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    public Auction createAuction(Long userId, String itemName, String description, double startingPrice,
                                 LocalDateTime endDate, List<MultipartFile> images,
                                 Double buyItNowPrice, Double reservePrice) throws IOException {
        User seller = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (reservePrice != null && reservePrice < startingPrice) {
            throw new IllegalArgumentException("Reserve price must be greater than or equal to starting price");
        }
        if (buyItNowPrice != null && (buyItNowPrice <= startingPrice || (reservePrice != null && buyItNowPrice <= reservePrice))) {
            throw new IllegalArgumentException("Buy it now price must be greater than starting price and reserve price");
        }

        Auction auction = new Auction();
        auction.setStatus(AuctionStatus.IN_PROGRESS);
        auction.setSeller(seller);
        auction.setItemName(itemName);
        auction.setDescription(description);
        auction.setStartingBid(startingPrice);
        auction.setStartDate(LocalDateTime.now());
        auction.setEndDate(endDate);
        auction.setCurrentBid(0D);

        if (buyItNowPrice != null) {
            auction.setBuyItNowPrice(buyItNowPrice);
        }
        if (reservePrice != null) {
            auction.setReservePrice(reservePrice);
        }

        if(images != null){
            for (MultipartFile image : images) {
                String imageUrl = uploadImage(image);
                auction.getImageUrls().add(imageUrl);
            }
        }

        return auctionRepository.save(auction);
    }

    public List<MyBidsResponse> getUserBids(User user) {
        List<Bid> userBids = bidRepository.findAllByBidder(user);

        return userBids.stream()
                .collect(Collectors.groupingBy(Bid::getAuction))
                .entrySet()
                .stream()
                .map(entry -> {
                    Auction auction = entry.getKey();
                    List<Bid> bids = entry.getValue();
                    Double userBidAmount = bids.stream().mapToDouble(Bid::getAmount).max().orElse(0);

                    Double highestBid = auction.getBids().stream().mapToDouble(Bid::getAmount).max().orElse(0);

                    String userBidStatus = determineBidStatus(userBidAmount, highestBid, auction);

                    MyBidsResponse response = new MyBidsResponse();
                    response.setAuctionId(auction.getId());
                    response.setItemName(auction.getItemName());
                    response.setDescription(auction.getDescription());
                    response.setEndDate(auction.getEndDate());
                    response.setHighestBid(highestBid);
                    response.setUserBidAmount(userBidAmount);
                    response.setUserBidStatus(userBidStatus);
                    response.setImageUrls(auction.getImageUrls());
                    response.setAuctionEnded(auction.getEndDate().isBefore(LocalDateTime.now()));

                    return response;
                })
                .collect(Collectors.toList());
    }

    private String determineBidStatus(Double userBidAmount, Double highestBid, Auction auction) {
        // this could also be inferred from the status? (status only updates once a minute however)
        if (auction.getEndDate().isBefore(LocalDateTime.now())) {
            if (userBidAmount.equals(highestBid)) {
                return "WON";
            } else {
                return "LOST";
            }
        } else {
            return userBidAmount.equals(highestBid) ? "TOP_BIDDER" : "OUTBID";
        }
    }

    public AuctionResponse getAuctionById(Long id) {
        Auction auction = auctionRepository.findById(id).orElse(null);
        return auction != null ? ConversionUtil.convertAuctionToDto(auction) : null;
    }

    // old and bad
    public Page<AuctionResponse> getAuctions(String username, AuctionStatus status, String itemName, Pageable pageable) {
        User seller = username != null ? userRepository.findByUsername(username).orElse(null) : null;
        Page<Auction> auctions = auctionRepository.findAllBySellerOrStatusOrItemNameContaining(seller, status, itemName, pageable);
        return auctions.map(ConversionUtil::convertAuctionToDto);
    }

    // TODO: maybe move this out to utility class
    private String uploadImage(MultipartFile image) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        Path filePath = Paths.get(uploadDir, uniqueFilename);

        Files.write(filePath, image.getBytes());

        return filePath.toString();
    }
}
