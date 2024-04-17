package com.writers.modelapp.setups.service;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;

public interface SetupsService {
    JSONObject createModule(JSONObject moduleRequest) throws BadRequestException;

    DataTableResponse getModules(DataTableRequest dataTableRequest);
}
