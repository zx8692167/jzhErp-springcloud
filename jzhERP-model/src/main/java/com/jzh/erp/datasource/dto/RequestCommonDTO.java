package com.jzh.erp.datasource.dto;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class RequestCommonDTO {
    JSONObject jsonObject;
    HttpServletRequest request;
    HttpServletResponse response;
}
