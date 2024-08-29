package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Payment;
import me.vilius.cerulean.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserAndType(User user, Payment.PaymentType type);
}
