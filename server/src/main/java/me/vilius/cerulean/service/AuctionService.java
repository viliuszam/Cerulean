package me.vilius.cerulean.service;

import me.vilius.cerulean.controller.dto.AuctionResponse;
import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.UserRating;
import me.vilius.cerulean.repository.AuctionRepository;
import me.vilius.cerulean.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

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

    public AuctionResponse convertToDto(Auction auction) {
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

        User seller = auction.getSeller();
        dto.setSellerName(seller.getUsername());
        dto.setSellerSignupDate(seller.getSignupDate());

        List<UserRating> ratings = seller.getReceivedRatings();
        OptionalDouble averageRating = ratings.stream().mapToInt(UserRating::getRating).average();
        dto.setSellerAverageRating(averageRating.isPresent() ? averageRating.getAsDouble() : 0.0);

        return dto;
    }
}
