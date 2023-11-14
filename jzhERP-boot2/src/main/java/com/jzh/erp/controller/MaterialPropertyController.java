package com.jzh.erp.controller;

import com.jzh.erp.datasource.entities.MaterialProperty;
import com.jzh.erp.service.materialProperty.MaterialPropertyService;
import com.jzh.erp.utils.BaseResponseInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description
 *
 * @Author: qiankunpingtai
 * @Date: 2019/3/29 15:24
 */
@RestController
@RequestMapping(value = "/materialProperty")
@Tag(name = "商品扩展字段")
public class MaterialPropertyController {

    @Resource
    private MaterialPropertyService materialPropertyService;

    @GetMapping(value = "/getAllList")
    @Operation(summary = "查询全部商品扩展字段信息")
    public BaseResponseInfo getAllList(HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialProperty> list = materialPropertyService.getMaterialProperty();
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

}
