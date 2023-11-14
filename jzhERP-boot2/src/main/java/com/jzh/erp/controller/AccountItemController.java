package com.jzh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.datasource.vo.AccountItemVo4List;
import com.jzh.erp.service.accountHead.AccountHeadService;
import com.jzh.erp.service.accountItem.AccountItemService;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.erp.utils.StringUtil;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ji sheng hua 752*718*920
 */
@RestController
@RequestMapping(value = "/accountItem")
@Tag(name = "财务明细")
public class AccountItemController {
    private Logger logger = LoggerFactory.getLogger(AccountItemController.class);

    @Resource
    private AccountItemService accountItemService;

    @Resource
    private AccountHeadService accountHeadService;

    @GetMapping(value = "/getDetailList")
    @Operation(summary = "明细列表")
    public BaseResponseInfo getDetailList(@RequestParam("headerId") Long headerId,
                                          HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            String type = null;
            List<AccountItemVo4List> dataList = new ArrayList<>();
            if(headerId != 0) {
                dataList = accountItemService.getDetailList(headerId);
                type = accountHeadService.getAccountHead(headerId).getType();
            }
            JSONObject outer = new JSONObject();
            outer.put("total", dataList.size());
            //存放数据json数组
            JSONArray dataArray = new JSONArray();
            if (null != dataList) {
                for (AccountItemVo4List ai : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("accountId", ai.getAccountId());
                    item.put("accountName", ai.getAccountName());
                    item.put("inOutItemId", ai.getInOutItemId());
                    item.put("inOutItemName", ai.getInOutItemName());
                    if(StringUtil.isNotEmpty(ai.getBillNumber())) {
                        item.put("billNumber", ai.getBillNumber());
                    } else {
                        item.put("billNumber", "QiChu");
                    }
                    item.put("needDebt", ai.getNeedDebt());
                    item.put("finishDebt", ai.getFinishDebt());
                    BigDecimal eachAmount = ai.getEachAmount();
                    if(BusinessConstants.TYPE_MONEY_IN.equals(type)) {
                        item.put("eachAmount", eachAmount);
                    } else if(BusinessConstants.TYPE_MONEY_OUT.equals(type)) {
                        item.put("eachAmount", BigDecimal.ZERO.subtract(eachAmount));
                    } else {
                        item.put("eachAmount", (eachAmount.compareTo(BigDecimal.ZERO))==-1 ? BigDecimal.ZERO.subtract(eachAmount): eachAmount);
                    }
                    item.put("remark", ai.getRemark());
                    dataArray.add(item);
                }
            }
            outer.put("rows", dataArray);
            res.code = 200;
            res.data = outer;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
