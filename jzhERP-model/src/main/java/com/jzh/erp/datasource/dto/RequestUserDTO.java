package com.jzh.erp.datasource.dto;

import com.jzh.erp.datasource.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

@Data
public class RequestUserDTO {
    User userParam;
    HttpServletRequest request;
    HttpServletResponse response;
}
