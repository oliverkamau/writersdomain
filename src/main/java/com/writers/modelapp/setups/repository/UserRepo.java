package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.Users;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Users findByUsernameIgnoreCase(String username);

    Users findByUsernameIgnoreCaseAndEnabled(String username, String enabled);

    Users findByUniqueRef(String userId);

    @Query("SELECT s FROM Users s where s.enabled = :status")
    List<Users> getActiveUsers(PageRequest of, String status);
}
