package me.vilius.cerulean.service;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.Bid;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.repository.AuctionRepository;
import me.vilius.cerulean.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatusUpdateService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DeliveryService deliveryService;

    // instead of doing it like this, maybe start using events, just something to look into
    @Scheduled(fixedRate = 15000)
    @Transactional
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findByEndDateBeforeAndStatus(now, AuctionStatus.IN_PROGRESS);
        List<Auction> buyItNowAuctions = auctionRepository.findAuctionsWithHighestBidMatchingBINPrice();

        List<Auction> auctionsToFinish = new java.util.ArrayList<>(expiredAuctions);
        auctionsToFinish.addAll(buyItNowAuctions);

        for (Auction auction : auctionsToFinish) {
            finishAuction(auction);
        }
    }

    private void finishAuction(Auction auction) {
        auction.setEndDate(LocalDateTime.now()); // set end date (auction can also end by BIN)
        auction.setStatus(AuctionStatus.FINISHED);

        // notify bidders
        List<User> uniqueBidders = auction.getBids().stream()
                .map(Bid::getBidder)
                .distinct()
                .collect(Collectors.toList());

        for (User bidder : uniqueBidders) {
            notificationService.sendAuctionEndedNotification(bidder.getId(), auction.getId());
        }

        Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);

        if (highestBid.isPresent()) {
            Bid highestBidObj = highestBid.get();
            auction.setBuyer(highestBidObj.getBidder());
            // pay out to the seller
            userService.updateBalance(auction.getSeller(), auction.getSeller()
                    .getBalance().add(new BigDecimal(highestBidObj.getAmount())));
            auctionRepository.save(auction);

            // create a delivery conversation between buyer and seller
            deliveryService.createDelivery(auction);
        }

        auctionRepository.save(auction);
    }
}