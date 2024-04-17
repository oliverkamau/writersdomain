package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.PermissionsDef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionsDefRepo extends JpaRepository<PermissionsDef, Long> {
}
