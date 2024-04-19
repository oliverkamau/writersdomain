package com.writers.modelapp.setups.repository;

import com.writers.modelapp.setups.entity.RolesDef;
import com.writers.modelapp.setups.entity.UserRole;
import com.writers.modelapp.setups.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UseRoleRepo extends JpaRepository<UserRole, Long> {
    UserRole findByUsersAndRolesDef(Users users, RolesDef rolesDef);

    List<UserRole> findByUsers(Users users);


}
