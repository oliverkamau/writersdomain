package com.writers.modelapp.setups.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Table(name="writers_app_users")
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="user_username")
    private String username;

    @Column(name="user_email")
    private String email;

    @Column(name="user_phone")
    private String phoneNumber;

    @Column(name="user_name")
    private String name;

    @Column(name="user_ref")
    private String uniqueRef;

    @Column(name="user_status")
    private String enabled;

    @Column(name="user_password")
    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "user_reset_pwd")
    private String resetPass;

    @Column(name = "user_jwt_token")
    private String token;

    @Column(name = "user_otp")
    private String otp;

    @Column(name = "user_otp_expiry")
    private Date otpExpiry;

    @Column(name = "is_logged_in")
    private String isLoggedIn;

    @Column(name="user_last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Column(name="user_last_ip")
    private String lastIP;



}
