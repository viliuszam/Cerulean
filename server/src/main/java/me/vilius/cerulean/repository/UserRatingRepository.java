package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Auction;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    Optional<UserRating> findByTransactionAndBuyer(Auction transaction, User buyer);
}
