package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Page<Auction> findAllBySellerOrStatusOrItemNameContaining(User seller, AuctionStatus status, String itemName, Pageable pageable);
    List<Auction> findByEndDateBeforeAndStatus(LocalDateTime endDate, AuctionStatus status);

    @Query("SELECT a FROM Auction a WHERE a.status = 'IN_PROGRESS' AND a.buyItNowPrice IS NOT NULL AND a.buyItNowPrice <= (SELECT MAX(b.amount) FROM Bid b WHERE b.auction = a)")
    List<Auction> findAuctionsWithHighestBidMatchingBINPrice();
}
