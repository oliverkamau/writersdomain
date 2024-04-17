package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UseRoleRepo extends JpaRepository<UserRole, Long> {
}
