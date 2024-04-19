package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.RolesDef;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolesDefRepo extends JpaRepository<RolesDef, Long> {
    RolesDef findByRoleNameIgnoreCase(String roleName);

    RolesDef findByRoleAlias(String id);

    @Query("SELECT r FROM RolesDef r")
    List<RolesDef> getRoles(PageRequest of);

    @Query("SELECT r FROM RolesDef r where r.roleAlias = :id")
    List<RolesDef> getRole(PageRequest of, String id);

    @Query("select r from RolesDef r where  r.roleCode NOT IN(select s.rolesDef.roleCode from UserRole s where s.users.uniqueRef = :userId)")
    List<RolesDef> getUnassogenedRoles(String userId);
}
