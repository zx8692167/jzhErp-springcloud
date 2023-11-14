package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.MsgEx;
import com.jzh.erp.service.msg.MsgService;
import com.jzh.erp.utils.BaseResponseInfo;
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
 * @author ji sheng hua jzhERP
 */
@RestController
@RequestMapping(value = "/msg")
@Tag(name = "消息管理")
public class MsgController {
    private Logger logger = LoggerFactory.getLogger(MsgController.class);

    @Resource
    private MsgService msgService;

    /**
     * 根据状态查询消息
     * @param status
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getMsgByStatus")
    @Operation(summary = "根据状态查询消息")
    public BaseResponseInfo getMsgByStatus(@RequestParam("status") String status,
                                           HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MsgEx> list = msgService.getMsgByStatus(status);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 批量更新状态
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/batchUpdateStatus")
    @Operation(summary = "批量更新状态")
    public BaseResponseInfo batchUpdateStatus(@RequestBody JSONObject jsonObject,
                                              HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String ids = jsonObject.getString("ids");
            String status = jsonObject.getString("status");
            msgService.batchUpdateStatus(ids, status);
            res.code = 200;
            res.data = "更新成功";
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据状态查询数量
     * @param status
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getMsgCountByStatus")
    @Operation(summary = "根据状态查询数量")
    public BaseResponseInfo getMsgCountByStatus(@RequestParam("status") String status,
                                                HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Long> map = new HashMap<String, Long>();
            Long count = msgService.getMsgCountByStatus(status);
            map.put("count", count);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据类型查询数量
     * @param type
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getMsgCountByType")
    @Operation(summary = "根据类型查询数量")
    public BaseResponseInfo getMsgCountByType(@RequestParam("type") String type,
                                                HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Map<String, Integer> map = new HashMap<>();
            Integer count = msgService.getMsgCountByType(type);
            map.put("count", count);
            res.code = 200;
            res.data = map;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 全部设置未已读
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/readAllMsg")
    @Operation(summary = "全部设置未已读")
    public BaseResponseInfo readAllMsg(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            msgService.readAllMsg();
            res.code = 200;
            res.data = "操作成功!";
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
