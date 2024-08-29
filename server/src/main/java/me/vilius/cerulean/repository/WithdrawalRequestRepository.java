package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.User;
import me.vilius.cerulean.model.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    List<WithdrawalRequest> findAllByStatus(WithdrawalRequest.Status status);
    List<WithdrawalRequest> findByUser(User user);
}