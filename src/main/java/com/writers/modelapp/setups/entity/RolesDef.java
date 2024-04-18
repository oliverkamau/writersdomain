package com.writers.modelapp.setups.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Entity
@Table(name ="writers_app_roles")
@Data
public class RolesDef implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="role_code")
    private Long roleCode;


    @Column(name="role_name")
    private String roleName;

    @Column(name="role_desc")
    private String roleDesc;

    @Column(name="role_alias")
    private String roleAlias;


}
