package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    Users findByUsernameEqualsIgnoreCaseAndEnabled(String username, String enabled);

    Users findByUniqueRef(String userId);
}
