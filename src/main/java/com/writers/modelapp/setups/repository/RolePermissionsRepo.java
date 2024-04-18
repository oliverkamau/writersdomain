package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.PermissionsDef;
import com.writers.modelapp.setups.entity.RolePermissions;
import com.writers.modelapp.setups.entity.RolesDef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionsRepo extends JpaRepository<RolePermissions,Long> {
    RolePermissions findByRolesDefAndPermissionsDef(RolesDef rolesDef, PermissionsDef permissionsDef);
}
