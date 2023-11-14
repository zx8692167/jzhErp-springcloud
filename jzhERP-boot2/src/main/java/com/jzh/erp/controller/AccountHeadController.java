package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.ExceptionConstants;
import com.jzh.erp.datasource.entities.AccountHead;
import com.jzh.erp.datasource.entities.AccountHeadVo4Body;
import com.jzh.erp.datasource.entities.AccountHeadVo4ListEx;
import com.jzh.erp.service.accountHead.AccountHeadService;
import com.jzh.erp.utils.BaseResponseInfo;
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
 * @author jishenghua 752*718*920
 */
@RestController
@RequestMapping(value = "/accountHead")
@Tag(name = "财务管理")
public class AccountHeadController {
    private Logger logger = LoggerFactory.getLogger(AccountHeadController.class);

    @Resource
    private AccountHeadService accountHeadService;

    /**
     * 批量设置状态-审核或者反审核
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @Operation(summary = "批量设置状态-审核或者反审核")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception{
        Map<String, Object> objectMap = new HashMap<>();
        String status = jsonObject.getString("status");
        String ids = jsonObject.getString("ids");
        int res = accountHeadService.batchSetStatus(status, ids);
        if(res > 0) {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return ResponseJsonUtil.returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 新增财务主表及财务子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addAccountHeadAndDetail")
    @Operation(summary = "新增财务主表及财务子表信息")
    public Object addAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws  Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        accountHeadService.addAccountHeadAndDetail(beanJson,rows, request);
        return result;
    }

    /**
     * 更新财务主表及财务子表信息
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateAccountHeadAndDetail")
    @Operation(summary = "更新财务主表及财务子表信息")
    public Object updateAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws Exception{
        JSONObject result = ExceptionConstants.standardSuccess();
        String beanJson = body.getInfo();
        String rows = body.getRows();
        accountHeadService.updateAccountHeadAndDetail(beanJson,rows,request);
        return result;
    }

    /**
     * 根据编号查询单据信息
     * @param billNo
     * @param request
     * @return
     */
    @GetMapping(value = "/getDetailByNumber")
    @Operation(summary = "根据编号查询单据信息")
    public BaseResponseInfo getDetailByNumber(@RequestParam("billNo") String billNo,
                                              HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        AccountHeadVo4ListEx ahl = new AccountHeadVo4ListEx();
        try {
            List<AccountHeadVo4ListEx> list = accountHeadService.getDetailByNumber(billNo);
            if(list.size()>0) {
                ahl = list.get(0);
            }
            res.code = 200;
            res.data = ahl;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 根据出入库单据id查询收付款单号
     * @param billId
     * @param request
     * @return
     */
    @GetMapping(value = "/getFinancialBillNoByBillId")
    @Operation(summary = "根据编号查询单据信息")
    public BaseResponseInfo getFinancialBillNoByBillId(@RequestParam("billId") Long billId,
                                              HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<AccountHead> list = accountHeadService.getFinancialBillNoByBillId(billId);
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
