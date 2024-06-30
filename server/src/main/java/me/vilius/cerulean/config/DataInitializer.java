package me.vilius.cerulean.config;

import jakarta.annotation.PostConstruct;
import me.vilius.cerulean.model.Role;
import me.vilius.cerulean.model.User;
import me.vilius.cerulean.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    private final UserService userService;

    @Autowired
    public DataInitializer( @Lazy UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        if (userService.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setRealName("Administrator");
            admin.setEmail("admin@example.com");
            admin.setPassword("admin");
            admin.setRole(Role.ADMIN);
            userService.registerUser(admin);
        }

        List<User> testUsers = Arrays.asList(
                createUser("user1", "User One", "user1@example.com", "password1", Role.USER),
                createUser("user2", "User Two", "user2@example.com", "password2", Role.USER),
                createUser("user3", "User Three", "user3@example.com", "password3", Role.USER)
        );

        for (User user : testUsers) {
            if (userService.findByUsername(user.getUsername()) == null) {
                userService.registerUser(user);
            }
        }
    }

    private User createUser(String username, String realName, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
