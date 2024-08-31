package me.vilius.cerulean.service;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.Bid;
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

    @Scheduled(fixedRate = 15000)
    @Transactional
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findByEndDateBeforeAndStatus(now, AuctionStatus.IN_PROGRESS);

        for (Auction auction : expiredAuctions) {
            auction.setStatus(AuctionStatus.FINISHED);
            // notify users the auction has ended
            auction.getBids().forEach(bid ->
                    notificationService.sendAuctionEndedNotification(bid.getBidder().getId(), auction.getId()));
            // pay out the seller
            Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
            if (highestBid.isPresent()) {
                Bid highestBidObj = highestBid.get();
                userService.updateBalance(auction.getSeller(), auction.getSeller()
                        .getBalance().add(new BigDecimal(highestBidObj.getAmount())));
            }
            auctionRepository.save(auction);
        }
    }

}
