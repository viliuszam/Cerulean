package me.vilius.cerulean.service;

import me.vilius.cerulean.model.Role;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository,  @Lazy PasswordEncoder passwordEncoder,
                       @Lazy NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public void updateBalance(User user, BigDecimal newBalance) {
        BigDecimal oldBalance = user.getBalance();
        user.setBalance(newBalance);
        userRepository.save(user);
        notificationService.sendBalanceUpdateNotification(user.getId(), oldBalance, newBalance);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setSignupDate(LocalDateTime.now());
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
