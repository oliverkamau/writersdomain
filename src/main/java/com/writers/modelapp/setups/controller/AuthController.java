package com.writers.modelapp.setups.controller;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.config.JwtUtil;
import com.writers.modelapp.config.SystemUserDetails;
import com.writers.modelapp.setups.entity.Users;
import com.writers.modelapp.setups.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;


@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationProvider authenticationProvider;
    private final SystemUserDetails mySystemUserDetails;
    private final UserRepo userRepo;

    public AuthController(JwtUtil jwtUtil, AuthenticationProvider authenticationProvider, SystemUserDetails mySystemUserDetails, UserRepo userRepo) {
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
        this.mySystemUserDetails = mySystemUserDetails;
        this.userRepo = userRepo;
    }

    @PostMapping("/authenticate")
    public JSONObject createAuthenticationToken(@RequestBody JSONObject authenticationRequest) throws Exception {

        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getString("username"),authenticationRequest.getString("password")));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect phone or password", e);
        }
        Users user=userRepo.findByUsernameEqualsIgnoreCaseAndEnabled(authenticationRequest.getString("username"),"1");
        String otp = mySystemUserDetails.promptOTP(user);
        JSONObject response = new JSONObject();
        response.put("status","pending");
        response.put("alias",user.getUniqueRef());
        response.put("email",user.getEmail());
//        response.put("otp",otp);
        return response;





    }
    @GetMapping("/logout")
    public void logout(HttpServletRequest servletRequest, @RequestParam String userId) throws Exception {
        log.info("Logging out with userId " + userId);
        Users user = userRepo.findByUniqueRef(userId);
        user.setIsLoggedIn(null);
        userRepo.save(user);
    }

    @PostMapping("/validateotp")
    public JSONObject validateAuthenticationToken(@RequestBody JSONObject jsonObject) throws Exception {
        Users user=userRepo.findByUsernameEqualsIgnoreCaseAndEnabled(jsonObject.getString("username"),"1");
        if(user==null)
            throw new BadCredentialsException("User Being validated doen't Exist");
        mySystemUserDetails.verifyOTP(user,jsonObject.getString("otp"));
        UserDetails userDetails= mySystemUserDetails.loadUserByUsername(user.getPhoneNumber());
        user.setOtp("logged");
        user.setIsLoggedIn("Y");
        userRepo.save(user);
        String realName=user.getName().toUpperCase();
        final String jwt=jwtUtil.generateToken(userDetails);
        JSONObject response = new JSONObject();
        response.put("status","verified");

        response.put("name",realName);
        response.put("userId",user.getUniqueRef());
        response.put("email",user.getEmail());
        response.put("token",jwt);
        response.put("phoneNumber",user.getPhoneNumber());

        System.out.println("token "+jwt);
        return response;
    }
}
