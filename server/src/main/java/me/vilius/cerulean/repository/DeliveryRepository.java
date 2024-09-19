package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.Delivery;
import me.vilius.cerulean.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByAuction(Auction auction);
    List<Delivery> findByAuctionBuyer(User buyer);
    List<Delivery> findByAuctionSeller(User seller);
}
