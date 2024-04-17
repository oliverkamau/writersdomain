package com.writers.modelapp.setups.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name ="writers_app_permissions")
@Data
public class PermissionsDef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="permission_code")
    private Long permissionCode;

    @Column(name="permission_name")
    private String permissionName;

    @Column(name="permission_desc")
    private String permissionDesc;

    @ManyToOne
    @JoinColumn(name="permission_module_id")
    private ModulesDef module;

    @Column(name="permission_alias")
    private String permissionAlias;

}
