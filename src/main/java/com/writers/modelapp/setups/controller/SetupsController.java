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

    @GetMapping("getModules")
    public DataTableResponse getModules(@RequestBody DataTableRequest dataTableRequest){

        return setupsService.getModules(dataTableRequest);
    }
}
