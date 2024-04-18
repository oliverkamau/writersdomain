package com.writers.modelapp.setups.service;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;

public interface SetupsService {
    JSONObject createModule(JSONObject moduleRequest) throws BadRequestException;

    DataTableResponse getModules(DataTableRequest dataTableRequest);

    DataTableResponse getModule(DataTableRequest dataTableRequest, String id);

    JSONObject createPermission(JSONObject permissionRequest) throws BadRequestException;

    DataTableResponse getPermissions(DataTableRequest dataTableRequest);

    DataTableResponse getPermission(DataTableRequest dataTableRequest, String id);

    JSONObject createRoles(JSONObject roleRequest) throws BadRequestException;

    DataTableResponse getRoles(DataTableRequest dataTableRequest);

    DataTableResponse getRole(DataTableRequest dataTableRequest, String id);

    JSONObject createRolePermissions(JSONObject rolePermissionRequest) throws BadRequestException;
}
