package me.vilius.cerulean.controller;

import me.vilius.cerulean.controller.dto.BidRequest;
import me.vilius.cerulean.controller.dto.BidResponse;
import me.vilius.cerulean.controller.dto.MyBidsResponse;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.service.AuctionService;
import me.vilius.cerulean.service.BidService;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private UserService userService;

    @Autowired
    private BidService bidService;

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/me")
    public ResponseEntity<List<MyBidsResponse>> getUserBids(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        User user = userService.findByUsername(principal.getName());
        List<MyBidsResponse> myBids = auctionService.getUserBids(user);
        return ResponseEntity.ok(myBids);
    }

    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<BidResponse> placeBid(
            Principal principal,
            @PathVariable Long auctionId,
            @RequestBody BidRequest bidRequest) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BidResponse(false, "User is not authenticated."));
        }
        User user = userService.findByUsername(principal.getName());
        BidResponse response = bidService.placeBid(auctionId, user, bidRequest);

        return ResponseEntity.ok(response);
    }
}