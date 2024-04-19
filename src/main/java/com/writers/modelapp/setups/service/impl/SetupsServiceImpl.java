package com.writers.modelapp.setups.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.setups.entity.*;
import com.writers.modelapp.setups.repository.*;
import com.writers.modelapp.setups.service.SetupsService;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;
import com.writers.modelapp.utils.RandomUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SetupsServiceImpl implements SetupsService {

    private final ModulesDefRepo modulesDefRepo;

    private final RandomUtils randomUtils;

    private final PermissionsDefRepo permissionsDefRepo;

    private final RolesDefRepo rolesDefRepo;

    private final RolePermissionsRepo rolePermissionsRepo;

    private final UserRepo userRepo;

    private final UseRoleRepo useRoleRepo;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SetupsServiceImpl(ModulesDefRepo modulesDefRepo, RandomUtils randomUtils, PermissionsDefRepo permissionsDefRepo, RolesDefRepo rolesDefRepo, RolePermissionsRepo rolePermissionsRepo, UserRepo userRepo, UseRoleRepo useRoleRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.modulesDefRepo = modulesDefRepo;
        this.randomUtils = randomUtils;
        this.permissionsDefRepo = permissionsDefRepo;
        this.rolesDefRepo = rolesDefRepo;
        this.rolePermissionsRepo = rolePermissionsRepo;
        this.userRepo = userRepo;
        this.useRoleRepo = useRoleRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Set<JSONObject> getUnAssignedRoles(JSONObject roleRequest) throws BadRequestException {
        if(StringUtils.isEmpty(roleRequest.getString("userId")))
            throw new BadRequestException("User is required to remove role");
        Users users = userRepo.findByUniqueRef(roleRequest.getString("userId"));
        if(users==null)
            throw new BadRequestException("Invalid User");

        List<RolesDef> rolesDefs = rolesDefRepo.getUnassogenedRoles(users.getUniqueRef());
        Set<JSONObject> objects = new HashSet<>();
        for(RolesDef u: rolesDefs){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("roleName", u.getRoleName());
            jsonObject.put("roleDesc", u.getRoleDesc());
            jsonObject.put("roleCode", u.getRoleAlias());
            objects.add(jsonObject);
        }

        return objects;
    }

    @Override
    public Set<JSONObject> getAssignedRoles(JSONObject roleRequest) throws BadRequestException {
        if(StringUtils.isEmpty(roleRequest.getString("userId")))
            throw new BadRequestException("User is required to remove role");
        Users users = userRepo.findByUniqueRef(roleRequest.getString("userId"));
        if(users==null)
            throw new BadRequestException("Invalid User");

        List<UserRole> userRoles = useRoleRepo.findByUsers(users);
        Set<JSONObject> objects = new HashSet<>();
        for(UserRole u: userRoles){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("roleName", u.getRolesDef().getRoleName());
            jsonObject.put("roleDesc", u.getRolesDef().getRoleDesc());
            jsonObject.put("roleCode", u.getRolesDef().getRoleAlias());
            objects.add(jsonObject);
        }

        return objects;
    }

    @Override
    public JSONObject removeUserRoles(JSONObject roleRequest) throws BadRequestException {
        String id = "";
        if(StringUtils.isEmpty(roleRequest.getString("userId")))
            throw new BadRequestException("User is required to remove role");
        Users users = userRepo.findByUniqueRef(roleRequest.getString("userId"));
        if(users==null)
            throw new BadRequestException("Invalid User");
        RolesDef rolesDef = rolesDefRepo.findByRoleAlias(roleRequest.getString("role"));
        if(rolesDef==null)
            throw new BadRequestException("Invalid Role");


        UserRole userRole = useRoleRepo.findByUsersAndRolesDef(users, rolesDef);
        if (userRole == null) {
            throw new BadRequestException("The User Role doesn't Exist");

        } else {

            useRoleRepo.delete(userRole);
        }
        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","User Role Removed Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createUserRoles(JSONObject roleRequest) throws BadRequestException {

        String id = "";
        if(StringUtils.isEmpty(roleRequest.getString("userId")))
            throw new BadRequestException("User is required to create role");
        Users users = userRepo.findByUniqueRef(roleRequest.getString("userId"));
        if(users==null)
            throw new BadRequestException("Invalid User");
        RolesDef rolesDef = rolesDefRepo.findByRoleAlias(roleRequest.getString("role"));
        if(rolesDef==null)
            throw new BadRequestException("Invalid Role");


            UserRole userRole = useRoleRepo.findByUsersAndRolesDef(users, rolesDef);
            if (userRole == null) {
                userRole = new UserRole();
                userRole.setUserRoleAlias(randomUtils.generateID(18));
                userRole.setRolesDef(rolesDef);
                userRole.setUsers(users);
                id = userRole.getUserRoleAlias();
                useRoleRepo.save(userRole);
            } else {
                throw new BadRequestException("The User Role already Exists");
            }
        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","User Role Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createUsers(JSONObject userRequest) throws BadRequestException {
        String id = userRequest.getString("userId");

        if(StringUtils.isEmpty(id)){
            Users def = userRepo.findByUsernameIgnoreCase(userRequest.getString("username"));
            if(def==null) {
                Users users = new Users();
                users.setEmail(userRequest.getString("email"));
                users.setEnabled(userRequest.getString("enabled"));
                users.setName(userRequest.getString("name"));
                users.setPassword(bCryptPasswordEncoder.encode(userRequest.getString("password")));
                users.setPhoneNumber(userRequest.getString("phoneNumber"));
                users.setUsername(userRequest.getString("username"));
                users.setUniqueRef(randomUtils.generateID(18));
                userRepo.save(users);
            }
            else{
                throw new BadRequestException("The Username provided already Exists");
            }

        }
        else {
            Users users = userRepo.findByUniqueRef(id);
            users.setEmail(userRequest.getString("email"));
            users.setEnabled(userRequest.getString("enabled"));
            users.setName(userRequest.getString("name"));
            users.setPhoneNumber(userRequest.getString("phoneNumber"));
            userRepo.save(users);
        }

        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","User Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createRolePermissions(JSONObject rolePermissionRequest) throws BadRequestException {

        String id = rolePermissionRequest.getString("rolePermissionCode");

        RolesDef rolesDef = rolesDefRepo.findByRoleAlias(rolePermissionRequest.getString("role"));
        if(rolesDef==null)
            throw new BadRequestException("Invalid Role");
        PermissionsDef permissionsDef = permissionsDefRepo.findByPermissionAlias(rolePermissionRequest.getString("permission"));
        if(permissionsDef==null)
            throw new BadRequestException("Invalid Permission");

        if(StringUtils.isEmpty(id)) {

            RolePermissions rolePermissions = rolePermissionsRepo.findByRolesDefAndPermissionsDef(rolesDef, permissionsDef);
            if (rolePermissions == null) {
                rolePermissions = new RolePermissions();
                rolePermissions.setRolePermissionAlias(randomUtils.generateID(18));
                rolePermissions.setPermissionsDef(permissionsDef);
                rolePermissions.setRolesDef(rolesDef);
                id = rolePermissions.getRolePermissionAlias();
                rolePermissionsRepo.save(rolePermissions);
            } else {
                throw new BadRequestException("The Role Permission already Exists");
            }
        }else{
            RolePermissions rolePermissions = rolePermissionsRepo.findByRolePermissionAlias(id);

                rolePermissions.setRolePermissionAlias(randomUtils.generateID(18));
                rolePermissions.setPermissionsDef(permissionsDef);
                rolePermissions.setRolesDef(rolesDef);
                rolePermissionsRepo.save(rolePermissions);

        }
        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","Role Permission Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createRoles(JSONObject roleRequest) throws BadRequestException {
        String id = roleRequest.getString("roleCode");

        if(StringUtils.isEmpty(id)){
            RolesDef def = rolesDefRepo.findByRoleNameIgnoreCase(roleRequest.getString("roleName"));
            if(def==null) {
                RolesDef rolesDef = new RolesDef();

                rolesDef.setRoleAlias(randomUtils.generateID(18));
                rolesDef.setRoleName(roleRequest.getString("roleName"));
                rolesDef.setRoleDesc(roleRequest.getString("roleDesc"));
                id = rolesDef.getRoleAlias();
                rolesDefRepo.save(rolesDef);
            }
            else{
                throw new BadRequestException("The Role already Exists");
            }

        }
        else {
            RolesDef rolesDef = rolesDefRepo.findByRoleAlias(id);
            rolesDef.setRoleName(roleRequest.getString("roleName"));
            rolesDef.setRoleDesc(roleRequest.getString("roleDesc"));
            rolesDefRepo.save(rolesDef);
        }

        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","Role Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createPermission(JSONObject permissionRequest) throws BadRequestException {

        if(StringUtils.isEmpty(permissionRequest.getString("module")))
            throw new BadRequestException("Module for permission is required");
        ModulesDef modulesDef = modulesDefRepo.findByModuleAlias(permissionRequest.getString("module"));
        if(modulesDef==null)
            throw new BadRequestException("Invalid module provided");

        String id = permissionRequest.getString("permissionCode");

        if(StringUtils.isEmpty(id)){
            PermissionsDef def = permissionsDefRepo.findByPermissionNameIgnoreCase(permissionRequest.getString("permissionName"));
            if(def==null) {
                PermissionsDef permissionsDef = new PermissionsDef();

                permissionsDef.setPermissionAlias(randomUtils.generateID(18));
                permissionsDef.setPermissionDesc(permissionRequest.getString("permissionDesc"));
                permissionsDef.setPermissionName(permissionRequest.getString("permissionName"));
                permissionsDef.setModule(modulesDef);
                id = permissionsDef.getPermissionAlias();
                permissionsDefRepo.save(permissionsDef);
            }
            else{
                throw new BadRequestException("The Permission already Exists");
            }

        }
        else {
            PermissionsDef permissionsDef = permissionsDefRepo.findByPermissionAlias(id);
            permissionsDef.setPermissionDesc(permissionRequest.getString("permissionDesc"));
            permissionsDef.setModule(modulesDef);
            permissionsDef.setPermissionName(permissionRequest.getString("permissionName"));
            permissionsDefRepo.save(permissionsDef);
        }

        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","Permissions Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public JSONObject createModule(JSONObject moduleRequest) throws BadRequestException {
        String id = moduleRequest.getString("moduleId");

        if(StringUtils.isEmpty(id)){
            ModulesDef def = modulesDefRepo.findByModuleNameIgnoreCase(moduleRequest.getString("moduleName"));
            if(def==null) {
                ModulesDef modulesDef = new ModulesDef();

                modulesDef.setModuleAlias(randomUtils.generateID(18));
                modulesDef.setModuleDesc(moduleRequest.getString("moduleDesc"));
                modulesDef.setModuleName(moduleRequest.getString("moduleName"));
                id = modulesDef.getModuleAlias();
                modulesDefRepo.save(modulesDef);
            }
            else{
                throw new BadRequestException("The Module already Exists");
            }

        }
        else {
            ModulesDef modulesDef = modulesDefRepo.findByModuleAlias(id);
            modulesDef.setModuleDesc(moduleRequest.getString("moduleDesc"));
            modulesDef.setModuleName(moduleRequest.getString("moduleName"));
            modulesDefRepo.save(modulesDef);
        }

        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","Module Created/Updated Successfully");
        response.put("id",id);
        return response;
    }

    @Override
    public DataTableResponse getModule(DataTableRequest dataTableRequest, String id) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<ModulesDef> modules = modulesDefRepo.getModule(id);
        List<JSONObject> defs= new ArrayList<>();
        if(!modules.isEmpty()){
            for(ModulesDef modulesDef: modules) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("moduleName", modulesDef.getModuleName());
                jsonObject.put("moduleDesc", modulesDef.getModuleDesc());
                jsonObject.put("moduleId", modulesDef.getModuleAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(modules.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getActiveUsers(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<Users> users = userRepo.getActiveUsers(PageRequest.of(pageNumber,size),"1");
        List<JSONObject> defs= new ArrayList<>();
        if(!users.isEmpty()){
            for(Users u: users) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", u.getUniqueRef());
                jsonObject.put("username", u.getUsername());
                jsonObject.put("name", u.getName());
                jsonObject.put("email", u.getEmail());
                jsonObject.put("phoneNumber", u.getPhoneNumber());
                jsonObject.put("status", "Active");
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(defs.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getInActiveUsers(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<Users> users = userRepo.getActiveUsers(PageRequest.of(pageNumber,size),"0");
        List<JSONObject> defs= new ArrayList<>();
        if(!users.isEmpty()){
            for(Users u: users) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", u.getUniqueRef());
                jsonObject.put("username", u.getUsername());
                jsonObject.put("name", u.getName());
                jsonObject.put("email", u.getEmail());
                jsonObject.put("phoneNumber", u.getPhoneNumber());
                jsonObject.put("status", "Inactive");
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(defs.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getModules(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<ModulesDef> modules = modulesDefRepo.getModules(PageRequest.of(pageNumber,size));
        List<JSONObject> defs= new ArrayList<>();
        if(!modules.isEmpty()){
            for(ModulesDef modulesDef: modules) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("moduleName", modulesDef.getModuleName());
                jsonObject.put("moduleDesc", modulesDef.getModuleDesc());
                jsonObject.put("moduleId", modulesDef.getModuleAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(modules.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getPermissions(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<PermissionsDef> permissions = permissionsDefRepo.getPermissions(PageRequest.of(pageNumber,size));
        List<JSONObject> defs= new ArrayList<>();
        if(!permissions.isEmpty()){
            for(PermissionsDef permissionsDef: permissions) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("permissionName", permissionsDef.getPermissionName());
                jsonObject.put("permissionDesc", permissionsDef.getPermissionDesc());
                jsonObject.put("permissionCode", permissionsDef.getPermissionAlias());
                jsonObject.put("moduleName", permissionsDef.getModule().getModuleName());
                jsonObject.put("moduleId", permissionsDef.getModule().getModuleAlias());

                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(permissions.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getPermission(DataTableRequest dataTableRequest, String id) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<PermissionsDef> permissions = permissionsDefRepo.getPermission(id);
        List<JSONObject> defs= new ArrayList<>();
        if(!permissions.isEmpty()){
            for(PermissionsDef permissionsDef: permissions) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("permissionName", permissionsDef.getPermissionName());
                jsonObject.put("permissionDesc", permissionsDef.getPermissionDesc());
                jsonObject.put("permissionCode", permissionsDef.getPermissionAlias());
                jsonObject.put("moduleName", permissionsDef.getModule().getModuleName());
                jsonObject.put("moduleId", permissionsDef.getModule().getModuleAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(permissions.size());
        return dataTableResponse;
    }

    @Override
    public DataTableResponse getRoles(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<RolesDef> roles = rolesDefRepo.getRoles(PageRequest.of(pageNumber,size));
        List<JSONObject> defs= new ArrayList<>();
        if(!roles.isEmpty()){
            for(RolesDef rolesDef: roles) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("roleName", rolesDef.getRoleName());
                jsonObject.put("roleDesc", rolesDef.getRoleDesc());
                jsonObject.put("roleCode", rolesDef.getRoleAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(roles.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getRole(DataTableRequest dataTableRequest, String id) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<RolesDef> roles = rolesDefRepo.getRole(PageRequest.of(pageNumber,size),id);
        List<JSONObject> defs= new ArrayList<>();
        if(!roles.isEmpty()){
            for(RolesDef rolesDef: roles) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("roleName", rolesDef.getRoleName());
                jsonObject.put("roleDesc", rolesDef.getRoleDesc());
                jsonObject.put("roleCode", rolesDef.getRoleAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(roles.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getRolePermissions(DataTableRequest dataTableRequest) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<RolePermissions> rolePermissions = rolePermissionsRepo.getRolePermissions(PageRequest.of(pageNumber,size));
        List<JSONObject> defs= new ArrayList<>();
        if(!rolePermissions.isEmpty()){
            for(RolePermissions rp: rolePermissions) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rolePermissionCode", rp.getRolePermissionAlias());
                jsonObject.put("roleName", rp.getRolesDef().getRoleName());
                jsonObject.put("roleDesc", rp.getRolesDef().getRoleDesc());
                jsonObject.put("roleCode", rp.getRolesDef().getRoleAlias());
                jsonObject.put("permissionName", rp.getPermissionsDef().getPermissionName());
                jsonObject.put("permissionDesc", rp.getPermissionsDef().getPermissionDesc());
                jsonObject.put("permissionCode", rp.getPermissionsDef().getPermissionAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(rolePermissions.size());

        return dataTableResponse;
    }

    @Override
    public DataTableResponse getRolePermission(DataTableRequest dataTableRequest, String id) {
        int size = dataTableRequest.getPageSize();
        int pageNumber = dataTableRequest.getPageNumber();
        List<RolePermissions> rolePermissions = rolePermissionsRepo.getRolePermission(id);
        List<JSONObject> defs= new ArrayList<>();
        if(!rolePermissions.isEmpty()){
            for(RolePermissions rp: rolePermissions) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rolePermissionCode", rp.getRolePermissionAlias());
                jsonObject.put("roleName", rp.getRolesDef().getRoleName());
                jsonObject.put("roleDesc", rp.getRolesDef().getRoleDesc());
                jsonObject.put("roleCode", rp.getRolesDef().getRoleAlias());
                jsonObject.put("permissionName", rp.getPermissionsDef().getPermissionName());
                jsonObject.put("permissionDesc", rp.getPermissionsDef().getPermissionDesc());
                jsonObject.put("permissionCode", rp.getPermissionsDef().getPermissionAlias());
                defs.add(jsonObject);
            }
        }
        DataTableResponse dataTableResponse = new DataTableResponse();
        dataTableResponse.setData(defs);
        dataTableResponse.setPageNumber(pageNumber);
        dataTableResponse.setSize(rolePermissions.size());

        return dataTableResponse;
    }
}
