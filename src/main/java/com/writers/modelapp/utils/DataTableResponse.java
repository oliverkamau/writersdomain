package com.writers.modelapp.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataTableResponse {

    private int size;
    private int pageNumber;
    private long totalRecords;
    private List<JSONObject> data;

}
