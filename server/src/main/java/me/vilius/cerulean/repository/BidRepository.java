package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.Bid;
import me.vilius.cerulean.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionOrderByAmountDesc(Auction auction);
    Optional<Bid> findTopByAuctionOrderByTimestampDesc(Auction auction);

    @Query("SELECT b FROM Bid b WHERE b.bidder = :bidder")
    List<Bid> findAllByBidder(User bidder);
}