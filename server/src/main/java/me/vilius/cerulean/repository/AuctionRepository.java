package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.AuctionStatus;
import me.vilius.cerulean.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Page<Auction> findAllBySellerOrStatusOrItemNameContaining(User seller, AuctionStatus status, String itemName, Pageable pageable);
    List<Auction> findByEndDateBeforeAndStatus(LocalDateTime endDate, AuctionStatus status);
}
