package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final UserRepository userRepo;

    public DataLoader(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostConstruct
    public void loadData() {
        if (userRepo.count() == 0) {
            userRepo.save(new User("Rana", "Rana@example.com"));
            userRepo.save(new User("ahmed", "ahmed@example.com"));
        }
    }
}
