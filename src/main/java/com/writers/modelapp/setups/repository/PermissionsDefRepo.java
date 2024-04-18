package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.PermissionsDef;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionsDefRepo extends JpaRepository<PermissionsDef, Long> {
    PermissionsDef findByPermissionNameIgnoreCase(String permissionName);

    PermissionsDef findByPermissionAlias(String id);

    List<PermissionsDef> getPermissions(PageRequest of);

    @Query("SELECT p FROM PermissionsDef p where p.permissionAlias = :id")
    List<PermissionsDef> getPermission(String id);
}
