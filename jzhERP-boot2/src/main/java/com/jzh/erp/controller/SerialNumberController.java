package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.DepotItem;
import com.jzh.erp.datasource.entities.SerialNumberEx;
import com.jzh.erp.service.depotHead.DepotHeadService;
import com.jzh.erp.service.depotItem.DepotItemService;
import com.jzh.erp.service.serialNumber.SerialNumberService;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.erp.utils.ErpInfo;
import com.jzh.erp.utils.Tools;
import com.jzh.erp.utils.ResponseJsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @Author: cjl
 * @Date: 2019/1/22 10:29
 */
@RestController
@RequestMapping(value = "/serialNumber")
@Tag(name = "序列号管理")
public class SerialNumberController {
    private Logger logger = LoggerFactory.getLogger(SerialNumberController.class);

    @Resource
    private SerialNumberService serialNumberService;
    @Resource
    private DepotHeadService depotHeadService;
    @Resource
    private DepotItemService depotItemService;

    /**
     * create by: jwh
     * description:
     *批量添加序列号
     * create time: 2019/1/29 15:11
     * @Param: materialName
     * @Param: serialNumberPrefix
     * @Param: batAddTotal
     * @Param: remark
     * @return java.lang.Object
     */
    @PostMapping("/batAddSerialNumber")
    @Operation(summary = "批量添加序列号")
    public String batAddSerialNumber(@RequestBody JSONObject jsonObject, HttpServletRequest request)throws Exception{
        Map<String, Object> objectMap = new HashMap<>();
        String materialCode = jsonObject.getString("materialCode");
        String serialNumberPrefix = jsonObject.getString("serialNumberPrefix");
        Integer batAddTotal = jsonObject.getInteger("batAddTotal");
        String remark = jsonObject.getString("remark");
        int insert = serialNumberService.batAddSerialNumber(materialCode,serialNumberPrefix,batAddTotal,remark);
        if(insert > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else if(insert == -1) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.TEST_USER.name, ErpInfo.TEST_USER.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取序列号商品
     * @param name
     * @param depotId
     * @param barCode
     * @param currentPage
     * @param pageSize
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getEnableSerialNumberList")
    @Operation(summary = "获取序列号商品")
    public BaseResponseInfo getEnableSerialNumberList(@RequestParam("name") String name,
                                                      @RequestParam("depotItemId") Long depotItemId,
                                                      @RequestParam("depotId") Long depotId,
                                                      @RequestParam("barCode") String barCode,
                                                      @RequestParam("page") Integer currentPage,
                                                      @RequestParam("rows") Integer pageSize,
                                                      HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            String number = "";
            if(depotItemId != null) {
                DepotItem depotItem = depotItemService.getDepotItem(depotItemId);
                number = depotHeadService.getDepotHead(depotItem.getHeaderId()).getNumber();
            }
            List<SerialNumberEx> list = serialNumberService.getEnableSerialNumberList(number, name, depotId, barCode, (currentPage-1)*pageSize, pageSize);
            for(SerialNumberEx serialNumberEx: list) {
                serialNumberEx.setCreateTimeStr(Tools.getCenternTime(serialNumberEx.getCreateTime()));
            }
            Long total = serialNumberService.getEnableSerialNumberCount(number, name, depotId, barCode);
            map.put("rows", list);
            map.put("total", total);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
