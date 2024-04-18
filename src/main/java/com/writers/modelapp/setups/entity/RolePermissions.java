package com.writers.modelapp.setups.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name ="writers_app_role_permissions")
@Data
public class RolePermissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_permission_code")
    private Long rolePermissionCode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="role_permission_role")
    private RolesDef rolesDef;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="role_permission_permissions")
    private PermissionsDef permissionsDef;


    @Column(name="role_permission_alias")
    private String rolePermissionAlias;
}
