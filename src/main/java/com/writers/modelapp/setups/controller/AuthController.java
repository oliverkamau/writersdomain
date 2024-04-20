package com.writers.modelapp.setups.controller;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.config.JwtUtil;
import com.writers.modelapp.config.SystemUserDetails;
import com.writers.modelapp.setups.entity.RolePermissions;
import com.writers.modelapp.setups.entity.UserRole;
import com.writers.modelapp.setups.entity.Users;
import com.writers.modelapp.setups.repository.RolePermissionsRepo;
import com.writers.modelapp.setups.repository.UseRoleRepo;
import com.writers.modelapp.setups.repository.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationProvider authenticationProvider;
    private final SystemUserDetails mySystemUserDetails;
    private final UserRepo userRepo;

    private final UseRoleRepo useRoleRepo;

    private final RolePermissionsRepo rolePermissionsRepo;

    public AuthController(JwtUtil jwtUtil, AuthenticationProvider authenticationProvider, SystemUserDetails mySystemUserDetails, UserRepo userRepo, UseRoleRepo useRoleRepo, RolePermissionsRepo rolePermissionsRepo) {
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
        this.mySystemUserDetails = mySystemUserDetails;
        this.userRepo = userRepo;
        this.useRoleRepo = useRoleRepo;
        this.rolePermissionsRepo = rolePermissionsRepo;
    }

    @PostMapping("/authenticate")
    public JSONObject createAuthenticationToken(@RequestBody JSONObject authenticationRequest) throws Exception {

        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getString("username"),authenticationRequest.getString("password")));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect phone or password", e);
        }
        Users user=userRepo.findByUsernameIgnoreCaseAndEnabled(authenticationRequest.getString("username"),"1");
        String otp = mySystemUserDetails.promptOTP(user);
        JSONObject response = new JSONObject();
        response.put("status","pending");
        response.put("alias",user.getUniqueRef());
        response.put("email",user.getEmail());
        response.put("username",user.getUsername());

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
        Users user=userRepo.findByUsernameIgnoreCaseAndEnabled(jsonObject.getString("username"),"1");
        if(user==null)
            throw new BadCredentialsException("User Being validated doen't Exist");
        mySystemUserDetails.verifyOTP(user,jsonObject.getString("otp"));
        UserDetails userDetails= mySystemUserDetails.loadUserByUsername(user.getUsername());
        Set<JSONObject> accessRights = getPermissions(user);
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
        response.put("accessRights",accessRights);

        System.out.println("token "+jwt);
        return response;
    }

    private Set<JSONObject> getPermissions(Users user) {
        Set<JSONObject> jsonObjects = new HashSet<>();
        List<UserRole> userRoleList = useRoleRepo.findByUsers(user);
        for (UserRole u: userRoleList){

            List<RolePermissions> rolePermissions = rolePermissionsRepo.findByRolesDef(u.getRolesDef());
            for(RolePermissions rp: rolePermissions){

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("moduleName",rp.getPermissionsDef().getModule().getModuleName());
                jsonObject.put("moduleId",rp.getPermissionsDef().getModule().getModuleAlias());
                jsonObject.put("permissionId",rp.getPermissionsDef().getPermissionAlias());
                jsonObject.put("permissionName",rp.getPermissionsDef().getPermissionName());
                jsonObjects.add(jsonObject);

            }
        }
        return jsonObjects;
    }
}
