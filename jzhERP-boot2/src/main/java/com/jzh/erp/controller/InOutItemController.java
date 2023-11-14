package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.InOutItem;
import com.jzh.erp.service.inOutItem.InOutItemService;
import com.jzh.erp.utils.ErpInfo;
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
 * @author jishenghua  jzhERP 2018年12月25日14:38:08
 */
@RestController
@RequestMapping(value = "/inOutItem")
@Tag(name = "收支项目")
public class InOutItemController {
    private Logger logger = LoggerFactory.getLogger(InOutItemController.class);

    @Resource
    private InOutItemService inOutItemService;

    /**
     * 查找收支项目信息-下拉框
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @Operation(summary = "查找收支项目信息")
    public String findBySelect(@RequestParam("type") String type, HttpServletRequest request) throws Exception{
        String res = null;
        try {
            List<InOutItem> dataList = inOutItemService.findBySelect(type);
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (InOutItem inOutItem : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", inOutItem.getId());
                    //收支项目名称
                    item.put("name", inOutItem.getName());
                    dataArray.add(item);
                }
            }
            res = dataArray.toJSONString();
        } catch(Exception e){
            e.printStackTrace();
            res = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        Boolean status = jsonObject.getBoolean("status");
        String ids = jsonObject.getString("ids");
        Map<String, Object> objectMap = new HashMap<>();
        int res = inOutItemService.batchSetStatus(status, ids);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
