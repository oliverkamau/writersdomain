package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.PermissionsDef;
import com.writers.modelapp.setups.entity.RolePermissions;
import com.writers.modelapp.setups.entity.RolesDef;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolePermissionsRepo extends JpaRepository<RolePermissions,Long> {
    RolePermissions findByRolesDefAndPermissionsDef(RolesDef rolesDef, PermissionsDef permissionsDef);

    RolePermissions findByRolePermissionAlias(String id);

    @Query("SELECT r FROM RolePermissions r")
    List<RolePermissions> getRolePermissions(PageRequest of);
    @Query("SELECT r FROM RolePermissions r where r.rolePermissionAlias = :id")
    List<RolePermissions> getRolePermission(String id);
    List<RolePermissions> findByRolesDef(RolesDef rolesDef);
}
