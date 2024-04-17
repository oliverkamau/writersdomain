package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionsRepo extends JpaRepository<RolePermissions,Long> {
}
