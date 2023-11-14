package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.MaterialAttribute;
import com.jzh.erp.service.materialAttribute.MaterialAttributeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ji sheng hua jzhERP
 */
@RestController
@RequestMapping(value = "/materialAttribute")
@Tag(name = "商品属性")
public class MaterialAttributeController {
    private Logger logger = LoggerFactory.getLogger(MaterialAttributeController.class);

    @Resource
    private MaterialAttributeService materialAttributeService;

    /**
     * 获取商品属性的名称列表
     * @param request
     * @return
     */
    @GetMapping(value = "/getNameList")
    @Operation(summary = "获取商品属性的名称列表")
    public JSONArray getNameList(HttpServletRequest request)throws Exception {
        JSONArray dataArray = new JSONArray();
        try {
            List<MaterialAttribute> materialAttributeList = materialAttributeService.getMaterialAttribute();
            if (null != materialAttributeList) {
                for (MaterialAttribute materialAttribute : materialAttributeList) {
                    JSONObject item = new JSONObject();
                    item.put("value", materialAttribute.getId().toString());
                    item.put("name", materialAttribute.getAttributeName());
                    dataArray.add(item);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return dataArray;
    }

    /**
     * 获取id查询属性的值列表
     * @param request
     * @return
     */
    @GetMapping(value = "/getValueListById")
    @Operation(summary = "获取id查询属性的值列表")
    public JSONArray getValueListById(@RequestParam("id") Long id,
                                     HttpServletRequest request)throws Exception {
        JSONArray dataArray = new JSONArray();
        try {
            dataArray = materialAttributeService.getValueArrById(id);
        } catch(Exception e){
            e.printStackTrace();
        }
        return dataArray;
    }
}
