package com.writers.modelapp.setups.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.setups.entity.ModulesDef;
import com.writers.modelapp.setups.entity.PermissionsDef;
import com.writers.modelapp.setups.entity.RolePermissions;
import com.writers.modelapp.setups.entity.RolesDef;
import com.writers.modelapp.setups.repository.ModulesDefRepo;
import com.writers.modelapp.setups.repository.PermissionsDefRepo;
import com.writers.modelapp.setups.repository.RolePermissionsRepo;
import com.writers.modelapp.setups.repository.RolesDefRepo;
import com.writers.modelapp.setups.service.SetupsService;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;
import com.writers.modelapp.utils.RandomUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupsServiceImpl implements SetupsService {

    private final ModulesDefRepo modulesDefRepo;

    private final RandomUtils randomUtils;

    private final PermissionsDefRepo permissionsDefRepo;

    private final RolesDefRepo rolesDefRepo;

    private final RolePermissionsRepo rolePermissionsRepo;

    public SetupsServiceImpl(ModulesDefRepo modulesDefRepo, RandomUtils randomUtils, PermissionsDefRepo permissionsDefRepo, RolesDefRepo rolesDefRepo, RolePermissionsRepo rolePermissionsRepo) {
        this.modulesDefRepo = modulesDefRepo;
        this.randomUtils = randomUtils;
        this.permissionsDefRepo = permissionsDefRepo;
        this.rolesDefRepo = rolesDefRepo;
        this.rolePermissionsRepo = rolePermissionsRepo;
    }

    @Override
    public JSONObject createRolePermissions(JSONObject rolePermissionRequest) throws BadRequestException {

        RolesDef rolesDef = rolesDefRepo.findByRoleAlias(rolePermissionRequest.getString("role"));
        if(rolesDef==null)
            throw new BadRequestException("Invalid Role");
        PermissionsDef permissionsDef = permissionsDefRepo.findByPermissionAlias(rolePermissionRequest.getString("permission"));
        if(permissionsDef==null)
            throw new BadRequestException("Invalid Permission");

        RolePermissions rolePermissions = rolePermissionsRepo.findByRolesDefAndPermissionsDef(rolesDef,permissionsDef);
        if(rolePermissions == null){
            rolePermissions = new RolePermissions();
            rolePermissions.setRolePermissionAlias(randomUtils.generateID(18));
            rolePermissions.setPermissionsDef(permissionsDef);
            rolePermissions.setRolesDef(rolesDef);
            rolePermissionsRepo.save(rolePermissions);
            }
            else{
                throw new BadRequestException("The Role Permission already Exists");
            }

        JSONObject response = new JSONObject();


        response.put("status","200");
        response.put("message","Role Permission Created/Updated Successfully");
        response.put("id",rolePermissions.getRolePermissionAlias());
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
        String id = permissionRequest.getString("permissionCode");

        if(StringUtils.isEmpty(id)){
            PermissionsDef def = permissionsDefRepo.findByPermissionNameIgnoreCase(permissionRequest.getString("permissionName"));
            if(def==null) {
                PermissionsDef permissionsDef = new PermissionsDef();

                permissionsDef.setPermissionAlias(randomUtils.generateID(18));
                permissionsDef.setPermissionDesc(permissionRequest.getString("permissionDesc"));
                permissionsDef.setPermissionName(permissionRequest.getString("permissionName"));
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
}
