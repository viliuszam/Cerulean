package me.vilius.cerulean.repository;

import me.vilius.cerulean.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}