package com.writers.modelapp.setups.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.writers.modelapp.exception.BadRequestException;
import com.writers.modelapp.setups.entity.ModulesDef;
import com.writers.modelapp.setups.repository.ModulesDefRepo;
import com.writers.modelapp.setups.service.SetupsService;
import com.writers.modelapp.utils.DataTableRequest;
import com.writers.modelapp.utils.DataTableResponse;
import com.writers.modelapp.utils.RandomUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupsServiceImpl implements SetupsService {

    private final ModulesDefRepo modulesDefRepo;

    private final RandomUtils randomUtils;

    public SetupsServiceImpl(ModulesDefRepo modulesDefRepo, RandomUtils randomUtils) {
        this.modulesDefRepo = modulesDefRepo;
        this.randomUtils = randomUtils;
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


}
