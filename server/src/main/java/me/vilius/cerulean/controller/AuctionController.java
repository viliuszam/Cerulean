package me.vilius.cerulean.controller;

import me.vilius.cerulean.controller.dto.AuctionResponse;
import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.repository.UserRepository;
import me.vilius.cerulean.service.AuctionService;
import me.vilius.cerulean.service.UserService;
import me.vilius.cerulean.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserService userService;

    // TODO: add categories to auction items
    @GetMapping("")
    public ResponseEntity<?> getAuctions(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) AuctionStatus status,
            @RequestParam(required = false) String itemName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            , Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(auctionService.getAuctions(username, status, itemName, pageRequest, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuctionById(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        AuctionResponse auctionResponse = auctionService.getAuctionById(id, user.getId());
        return auctionResponse != null ? ResponseEntity.ok(auctionResponse) : ResponseEntity.notFound().build();
    }

    // TODO: some kind of nesting problem? test later
    @PostMapping("/create")
    public ResponseEntity<?> createAuction(
            Principal principal,
            @RequestParam String itemName,
            @RequestParam String description,
            @RequestParam double startingPrice,
            @RequestParam String endDate,
            @RequestParam(name = "images", required = false) List<MultipartFile> images,
            @RequestParam(required = false) Double buyItNowPrice,
            @RequestParam(required = false) Double reservePrice) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        try {
            Auction auction = auctionService.createAuction(user.getId(), itemName, description, startingPrice, end, images, buyItNowPrice, reservePrice);
            AuctionResponse auctionResponseDTO = ConversionUtil.convertAuctionToDto(auction, user.getId());
            return ResponseEntity.ok(auctionResponseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
