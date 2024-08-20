package me.vilius.cerulean.service;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusUpdateService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findByEndDateBeforeAndStatus(now, AuctionStatus.IN_PROGRESS);

        for (Auction auction : expiredAuctions) {
            auction.setStatus(AuctionStatus.FINISHED);
            auctionRepository.save(auction);
        }
    }

}
