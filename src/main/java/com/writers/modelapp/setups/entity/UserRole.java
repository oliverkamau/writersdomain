package com.writers.modelapp.setups.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Entity
@Table(name ="writers_app_user_roles")
@Data
public class UserRole implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_role_code")
	private Long userRoleCode;


	@ManyToOne
	@JoinColumn(name="user_role_role")
	private RolesDef roles;

	@ManyToOne
	@JoinColumn(name="user_role_user")
	private Users users;

	@Column(name="user_role_alias")
	private String userRoleAlias;

	

}
