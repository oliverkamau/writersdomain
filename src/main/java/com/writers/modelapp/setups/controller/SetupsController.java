package com.writers.modelapp.setups.controller;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.setups.entity.ModulesDef;
import com.writers.modelapp.setups.entity.Users;
import com.writers.modelapp.setups.repository.ModulesDefRepo;
import com.writers.modelapp.setups.service.SetupsService;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;
import com.writers.modelapp.utils.RandomUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setups")
@Slf4j
public class SetupsController {

    private final SetupsService setupsService;


    public SetupsController(SetupsService setupsService) {
        this.setupsService = setupsService;

    }

    @PostMapping("/createModule")
    public JSONObject createModule(@RequestBody JSONObject moduleRequest) throws Exception {
      log.info("Incoming Create Module Request {}", moduleRequest);
      return setupsService.createModule(moduleRequest);

    }

    @PostMapping("getModules")
    public DataTableResponse getModules(@RequestBody DataTableRequest dataTableRequest){

        return setupsService.getModules(dataTableRequest);
    }
    @PostMapping("getModule")
    public DataTableResponse getModule(@RequestParam DataTableRequest dataTableRequest, @RequestParam String id){

        return setupsService.getModule(dataTableRequest,id);
    }

    @PostMapping("/createPermission")
    public JSONObject createPermission(@RequestBody JSONObject permissionRequest) throws Exception {
        log.info("Incoming Create Permission Request {}", permissionRequest);
        return setupsService.createPermission(permissionRequest);

    }

    @PostMapping("getPermissions")
    public DataTableResponse getPermissions(@RequestBody DataTableRequest dataTableRequest){

        return setupsService.getPermissions(dataTableRequest);
    }
    @PostMapping("getModule")
    public DataTableResponse getPermission(@RequestParam DataTableRequest dataTableRequest, @RequestParam String id){

        return setupsService.getPermission(dataTableRequest,id);
    }

    @PostMapping("/createRoles")
    public JSONObject createRoles(@RequestBody JSONObject roleRequest) throws Exception {
        log.info("Incoming Create Role Request {}", roleRequest);
        return setupsService.createRoles(roleRequest);

    }

    @PostMapping("getRoles")
    public DataTableResponse getRoles(@RequestBody DataTableRequest dataTableRequest){

        return setupsService.getRoles(dataTableRequest);
    }
    @PostMapping("getRole")
    public DataTableResponse getRole(@RequestParam DataTableRequest dataTableRequest, @RequestParam String id){

        return setupsService.getRole(dataTableRequest,id);
    }

    @PostMapping("/createRolePermissions")
    public JSONObject createRolePermissions(@RequestBody JSONObject rolePermissionRequest) throws Exception {
        log.info("Incoming Create Role Permission Request {}", rolePermissionRequest);
        return setupsService.createRolePermissions(rolePermissionRequest);

    }


}
