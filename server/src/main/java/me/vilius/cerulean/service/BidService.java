package me.vilius.cerulean.service;

import me.vilius.cerulean.controller.dto.BidRequest;
import me.vilius.cerulean.controller.dto.BidResponse;
import me.vilius.cerulean.controller.dto.NotificationMessage;
import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.Bid;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.repository.AuctionRepository;
import me.vilius.cerulean.repository.BidRepository;
import me.vilius.cerulean.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    public BidResponse placeBid(Long auctionId, User user, BidRequest bidRequest) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElse(null);

        if (auction == null) {
            return new BidResponse(false, "Auction not found with id: " + auctionId);
        }

        if (auction.getSeller().getId().equals(user.getId())) {
            return new BidResponse(false, "You cannot bid on your own auction.");
        }

        if (auction.getStatus() != AuctionStatus.IN_PROGRESS) {
            return new BidResponse(false, "Bidding is only allowed on auctions that are in progress.");
        }

        if(bidRequest.getAmount() < auction.getStartingBid()){
            return new BidResponse(false, "Your bid is smaller than the starting bid.");
        }

        Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
        if (highestBid.isPresent() && bidRequest.getAmount() <= highestBid.get().getAmount()) {
            return new BidResponse(false, "Your bid must be higher than the current highest bid of $" + highestBid.get().getAmount());
        }

        if (auction.getBuyItNowPrice() != null && bidRequest.getAmount() > auction.getBuyItNowPrice()) {
            return new BidResponse(false, "Your bid exceeds the Buy It Now price of $" + auction.getBuyItNowPrice());
        }

        Optional<Bid> lastBid = bidRepository.findTopByAuctionOrderByTimestampDesc(auction);
        if (lastBid.isPresent() && lastBid.get().getBidder().getId().equals(user.getId())) {
            return new BidResponse(false, "You cannot place two consecutive bids.");
        }

        // i think this .doubleValue conversion isn't very good to do
        if (user.getBalance().doubleValue() < bidRequest.getAmount()) {
            return new BidResponse(false, "Insufficient balance.");
        }


        Bid bid = new Bid();
        bid.setAmount(bidRequest.getAmount());
        bid.setBidder(user);
        bid.setAuction(auction);
        bid.setTimestamp(LocalDateTime.now());
        userService.updateBalance(user, user.getBalance()
                .subtract(new BigDecimal(bidRequest.getAmount())));
        userRepository.save(user);

        Bid savedBid = bidRepository.save(bid);

        if(lastBid.isPresent()){
            Bid lastBidObj = lastBid.get();
            User bidder = lastBidObj.getBidder();
            // return last bidders balance
            userService.updateBalance(bidder, bidder.getBalance()
                    .add(new BigDecimal(lastBidObj.getAmount())));
            userRepository.save(bidder);
            // notify last bidder
            notificationService.sendOutbidNotification(bidder.getId(), auctionId,
                    auction.getItemName(), bid.getAmount());
        }

        return new BidResponse(true, "Bid placed successfully.", savedBid);
    }
}
