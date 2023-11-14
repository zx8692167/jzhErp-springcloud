package com.jzh.hystrix.consumer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.*;
import com.jzh.erp.utils.*;
import com.jzh.hystrix.consumer.service.AccountCenterService;
import com.jzh.hystrix.consumer.service.SystemCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/account_center")
@Tag(name = "结算中心模块 ")  //仓库跟财务
@Slf4j
public class AccountCenterController {
    @Resource
    private AccountCenterService accountCenterService;

    @Resource
    private SystemCenterService systemCenterService;

    /**
     * 批量设置状态-审核或者反审核
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/batchSetStatus")
    @Operation(summary = "批量设置状态-审核或者反审核")
    public String batchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception {

        return accountCenterService.batchSetStatus(jsonObject);

    }

    /**
     * 入库出库明细接口
     *
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param number
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInOutDetail")
    @Operation(summary = "入库出库明细接口")
    public BaseResponseInfo findInOutDetail(@RequestParam("currentPage") Integer currentPage,
                                            @RequestParam("pageSize") Integer pageSize,
                                            @RequestParam(value = "organId", required = false) Integer oId,
                                            @RequestParam("number") String number,
                                            @RequestParam("materialParam") String materialParam,
                                            @RequestParam(value = "depotId", required = false) Long depotId,
                                            @RequestParam("beginTime") String beginTime,
                                            @RequestParam("endTime") String endTime,
                                            @RequestParam(value = "roleType", required = false) String roleType,
                                            @RequestParam("type") String type,
                                            @RequestParam("remark") String remark,
                                            HttpServletRequest request) throws Exception {

        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.findInOutDetail(forceFlag,currentPage, pageSize, oId, number, materialParam, depotId, beginTime, endTime, roleType, type, remark);
    }

    /**
     * 入库出库统计接口
     *
     * @param currentPage
     * @param pageSize
     * @param oId
     * @param materialParam
     * @param depotId
     * @param beginTime
     * @param endTime
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/findInOutMaterialCount")
    @Operation(summary = "入库出库统计接口")
    public BaseResponseInfo findInOutMaterialCount(@RequestParam("currentPage") Integer currentPage,
                                                   @RequestParam("pageSize") Integer pageSize,
                                                   @RequestParam(value = "organId", required = false) Integer oId,
                                                   @RequestParam("materialParam") String materialParam,
                                                   @RequestParam(value = "depotId", required = false) Long depotId,
                                                   @RequestParam("beginTime") String beginTime,
                                                   @RequestParam("endTime") String endTime,
                                                   @RequestParam("type") String type,
                                                   @RequestParam(value = "roleType", required = false) String roleType,
                                                   HttpServletRequest request) throws Exception {

        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.findInOutMaterialCount(forceFlag,currentPage, pageSize, oId, materialParam, depotId, beginTime, endTime, type, roleType);
    }

    /**
     * 调拨明细统计
     *
     * @param currentPage
     * @param pageSize
     * @param number
     * @param materialParam
     * @param depotIdF      调出仓库
     * @param depotId       调入仓库
     * @param beginTime
     * @param endTime
     * @param subType
     * @param request
     * @return
     */
    @GetMapping(value = "/findAllocationDetail")
    @Operation(summary = "调拨明细统计")
    public BaseResponseInfo findallocationDetail(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("number") String number,
                                                 @RequestParam("materialParam") String materialParam,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam(value = "depotIdF", required = false) Long depotIdF,
                                                 @RequestParam("beginTime") String beginTime,
                                                 @RequestParam("endTime") String endTime,
                                                 @RequestParam("subType") String subType,
                                                 @RequestParam(value = "roleType", required = false) String roleType,
                                                 @RequestParam("remark") String remark,
                                                 HttpServletRequest request) throws Exception {

        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.findallocationDetail(forceFlag,currentPage, pageSize, number, materialParam, depotId, depotIdF, beginTime, endTime, subType, roleType, remark);
    }

    /**
     * 对账单接口
     *
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param organId
     * @param supplierType
     * @param request
     * @return
     */
    @GetMapping(value = "/getStatementAccount")
    @Operation(summary = "对账单接口")
    public BaseResponseInfo getStatementAccount(@RequestParam("currentPage") Integer currentPage,
                                                @RequestParam("pageSize") Integer pageSize,
                                                @RequestParam("beginTime") String beginTime,
                                                @RequestParam("endTime") String endTime,
                                                @RequestParam(value = "organId", required = false) Integer organId,
                                                @RequestParam("supplierType") String supplierType,
                                                HttpServletRequest request) throws Exception {

        return accountCenterService.getStatementAccount(currentPage, pageSize, beginTime, endTime, organId, supplierType);
    }

    /**
     * 根据编号查询单据信息
     *
     * @param number
     * @param request
     * @return
     */
    @GetMapping(value = "/getDetailByNumber")
    @Operation(summary = "根据编号查询单据信息")
    public BaseResponseInfo getDetailByNumber(@RequestParam("number") String number,
                                              HttpServletRequest request) throws Exception {

        return accountCenterService.getDetailByNumber(number);
    }

    /**
     * 根据原单号查询关联的单据列表
     *
     * @param number
     * @param request
     * @return
     */
    @GetMapping(value = "/getBillListByLinkNumber")
    @Operation(summary = "根据原单号查询关联的单据列表")
    public BaseResponseInfo getBillListByLinkNumber(@RequestParam("number") String number,
                                                    HttpServletRequest request) throws Exception {

        return accountCenterService.getBillListByLinkNumber(number);
    }

    /**
     * 新增单据主表及单据子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addDepotHeadAndDetail")
    @Operation(summary = "新增单据主表及单据子表信息")
    public Object addDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws Exception {
        return accountCenterService.addDepotHeadAndDetail(body);
    }

    /**
     * 更新单据主表及单据子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateDepotHeadAndDetail")
    @Operation(summary = "更新单据主表及单据子表信息")
    public Object updateDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body, HttpServletRequest request) throws Exception {
        return accountCenterService.updateDepotHeadAndDetail(body);
    }

    /**
     * 统计今日采购额、昨日采购额、本月采购额、今年采购额|销售额|零售额
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getBuyAndSaleStatistics")
    @Operation(summary = "统计今日采购额、昨日采购额、本月采购额、今年采购额|销售额|零售额")
    public BaseResponseInfo getBuyAndSaleStatistics(@RequestParam(value = "roleType", required = false) String roleType,
                                                    HttpServletRequest request) {

        return accountCenterService.getBuyAndSaleStatistics(roleType);
    }

    /**
     * 根据当前用户获取操作员数组，用于控制当前用户的数据权限，限制可以看到的单据范围
     * 注意：该接口提供给部分插件使用，勿删
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getCreatorByCurrentUser")
    @Operation(summary = "根据当前用户获取操作员数组")
    public BaseResponseInfo getCreatorByRoleType(HttpServletRequest request) {

        return accountCenterService.getCreatorByRoleType();
    }

    /**
     * 查询存在欠款的单据
     *
     * @param search
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/debtList")
    @Operation(summary = "查询存在欠款的单据")
    public String debtList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                           @RequestParam("currentPage") Integer currentPage,
                           @RequestParam("pageSize") Integer pageSize,
                           HttpServletRequest request) throws Exception {
        return accountCenterService.debtList(search, currentPage, pageSize);
    }


    /**
     * 批量设置状态-审核或者反审核
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/accountHead/batchSetStatus")
    @Operation(summary = "批量设置状态-审核或者反审核")
    public String accountHeadBatchSetStatus(@RequestBody JSONObject jsonObject,
                                            HttpServletRequest request) throws Exception {
        return accountCenterService.accountHeadBatchSetStatus(jsonObject);
    }

    /**
     * 新增财务主表及财务子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addAccountHeadAndDetail")
    @Operation(summary = "新增财务主表及财务子表信息")
    public Object addAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws Exception {
        return accountCenterService.addAccountHeadAndDetail(body);
    }

    /**
     * 更新财务主表及财务子表信息
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/updateAccountHeadAndDetail")
    @Operation(summary = "更新财务主表及财务子表信息")
    public Object updateAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body, HttpServletRequest request) throws Exception {
        return accountCenterService.updateAccountHeadAndDetail(body);
    }

    /**
     * 根据编号查询单据信息
     *
     * @param billNo
     * @param request
     * @return
     */
    @GetMapping(value = "/accountHead/getDetailByNumber")
    @Operation(summary = "根据编号查询单据信息")
    public BaseResponseInfo accountHeadGetDetailByNumber(@RequestParam("billNo") String billNo,
                                                         HttpServletRequest request) throws Exception {

        return accountCenterService.accountHeadGetDetailByNumber(billNo);
    }

    /**
     * 根据出入库单据id查询收付款单号
     *
     * @param billId
     * @param request
     * @return
     */
    @GetMapping(value = "/getFinancialBillNoByBillId")
    @Operation(summary = "根据编号查询单据信息")
    public BaseResponseInfo getFinancialBillNoByBillId(@RequestParam("billId") Long billId,
                                                       HttpServletRequest request) throws Exception {

        return accountCenterService.getFinancialBillNoByBillId(billId);
    }


    /**
     * create by: jwh
     * description:
     * 批量添加序列号
     * create time: 2019/1/29 15:11
     *
     * @return java.lang.Object
     * @Param: materialName
     * @Param: serialNumberPrefix
     * @Param: batAddTotal
     * @Param: remark
     */
    @PostMapping("/batAddSerialNumber")
    @Operation(summary = "批量添加序列号")
    public String batAddSerialNumber(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        return accountCenterService.batAddSerialNumber(jsonObject);
    }

    /**
     * 获取序列号商品
     *
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
                                                      HttpServletRequest request) throws Exception {

        return accountCenterService.getEnableSerialNumberList(name, depotItemId, depotId, barCode, currentPage, pageSize);
    }


    /**
     * 经手人全部数据列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/person/getAllList")
    @Operation(summary = "经手人全部数据列表")
    public BaseResponseInfo personGetAllList(HttpServletRequest request) throws Exception {
        return accountCenterService.personGetAllList();
    }

    /**
     * 根据Id获取经手人信息
     *
     * @param personIds
     * @param request
     * @return
     */
    @GetMapping(value = "/getPersonByIds")
    @Operation(summary = "根据Id获取经手人信息")
    public BaseResponseInfo getPersonByIds(@RequestParam("personIds") String personIds,
                                           HttpServletRequest request) throws Exception {
        return accountCenterService.getPersonByIds(personIds);
    }

    /**
     * 根据类型获取经手人信息
     *
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "/getPersonByType")
    @Operation(summary = "根据类型获取经手人信息")
    public BaseResponseInfo getPersonByType(@RequestParam("type") String type,
                                            HttpServletRequest request) throws Exception {
        return accountCenterService.getPersonByType(type);
    }

    /**
     * 根据类型获取经手人信息 1-业务员，2-仓管员，3-财务员
     *
     * @param typeNum
     * @param request
     * @return
     */
    @GetMapping(value = "/getPersonByNumType")
    @Operation(summary = "根据类型获取经手人信息1-业务员，2-仓管员，3-财务员")
    public JSONArray getPersonByNumType(@RequestParam("type") String typeNum,
                                        HttpServletRequest request) throws Exception {
        return accountCenterService.getPersonByNumType(typeNum);
    }

    /**
     * 批量设置状态-启用或者禁用
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/person/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String personBatchSetStatus(@RequestBody JSONObject jsonObject,
                                       HttpServletRequest request) throws Exception {
        return accountCenterService.personBatchSetStatus(jsonObject);
    }

    /**
     * 查找收支项目信息-下拉框
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/inOutItem/findBySelect")
    @Operation(summary = "查找收支项目信息")
    public String inOutItemFindBySelect(@RequestParam("type") String type, HttpServletRequest request) throws Exception {
        return accountCenterService.inOutItemFindBySelect(type);
    }

    /**
     * 批量设置状态-启用或者禁用
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/inOutItem/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String inOutItemBatchSetStatus(@RequestBody JSONObject jsonObject,
                                          HttpServletRequest request) throws Exception {
        return accountCenterService.inOutItemBatchSetStatus(jsonObject);
    }


    /**
     * 根据仓库和商品查询单据列表
     *
     * @param mId
     * @param request
     * @return
     */
    @GetMapping(value = "/findDetailByDepotIdsAndMaterialId")
    @Operation(summary = "根据仓库和商品查询单据列表")
    public String findDetailByDepotIdsAndMaterialId(
            @RequestParam(value = Constants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = Constants.CURRENT_PAGE, required = false) Integer currentPage,
            @RequestParam(value = "depotIds", required = false) String depotIds,
            @RequestParam(value = "sku", required = false) String sku,
            @RequestParam(value = "batchNumber", required = false) String batchNumber,
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam("materialId") Long mId,
            HttpServletRequest request) throws Exception {
        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.findDetailByDepotIdsAndMaterialId(forceFlag,pageSize, currentPage, depotIds, sku, batchNumber, number, beginTime, endTime, mId);
    }

    /**
     * 根据商品条码和仓库id查询库存数量
     *
     * @param depotId
     * @param barCode
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findStockByDepotAndBarCode")
    @Operation(summary = "根据商品条码和仓库id查询库存数量")
    public BaseResponseInfo findStockByDepotAndBarCode(
            @RequestParam(value = "depotId", required = false) Long depotId,
            @RequestParam("barCode") String barCode,
            HttpServletRequest request) throws Exception {

        return accountCenterService.findStockByDepotAndBarCode(depotId, barCode);

    }

    /**
     * 单据明细列表
     *
     * @param headerId
     * @param mpList
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getDetailList")
    @Operation(summary = "单据明细列表")
    public BaseResponseInfo getDetailList(@RequestParam("headerId") Long headerId,
                                          @RequestParam("mpList") String mpList,
                                          @RequestParam(value = "linkType", required = false) String linkType,
                                          @RequestParam(value = "isReadOnly", required = false) String isReadOnly,
                                          HttpServletRequest request) throws Exception {

        return accountCenterService.getDetailList(headerId, mpList, linkType, isReadOnly);
    }


    /**
     * 进销存统计
     *
     * @param currentPage
     * @param pageSize
     * @param depotIds
     * @param monthTime
     * @param materialParam
     * @param mpList
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findByAll")
    @Operation(summary = "查找所有的明细")
    public BaseResponseInfo findByAll(@RequestParam("currentPage") Integer currentPage,
                                      @RequestParam("pageSize") Integer pageSize,
                                      @RequestParam(value = "depotIds", required = false) String depotIds,
                                      @RequestParam(value = "categoryId", required = false) Long categoryId,
                                      @RequestParam("monthTime") String monthTime,
                                      @RequestParam("materialParam") String materialParam,
                                      @RequestParam("mpList") String mpList,
                                      HttpServletRequest request) throws Exception {

        return accountCenterService.findByAll(currentPage, pageSize, depotIds, categoryId, monthTime, materialParam, mpList);
    }

    /**
     * 进销存统计总计金额
     *
     * @param depotIds
     * @param monthTime
     * @param materialParam
     * @param request
     * @return
     */
    @GetMapping(value = "/totalCountMoney")
    @Operation(summary = "统计总计金额")
    public BaseResponseInfo totalCountMoney(@RequestParam(value = "depotIds", required = false) String depotIds,
                                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                                            @RequestParam("monthTime") String monthTime,
                                            @RequestParam("materialParam") String materialParam,
                                            HttpServletRequest request) throws Exception {

        return accountCenterService.totalCountMoney(depotIds, categoryId, monthTime, materialParam);

    }

    /**
     * 采购统计
     *
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param materialParam
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/buyIn")
    @Operation(summary = "采购统计")
    public BaseResponseInfo buyIn(@RequestParam("currentPage") Integer currentPage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("beginTime") String beginTime,
                                  @RequestParam("endTime") String endTime,
                                  @RequestParam(value = "organId", required = false) Long organId,
                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                  @RequestParam("materialParam") String materialParam,
                                  @RequestParam("mpList") String mpList,
                                  @RequestParam(value = "roleType", required = false) String roleType,
                                  HttpServletRequest request) throws Exception {
        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.buyIn(forceFlag,currentPage, pageSize, beginTime, endTime, organId, depotId, materialParam, mpList, roleType);

    }

    /**
     * 零售统计
     *
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param materialParam
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/retailOut")
    @Operation(summary = "零售统计")
    public BaseResponseInfo retailOut(@RequestParam("currentPage") Integer currentPage,
                                      @RequestParam("pageSize") Integer pageSize,
                                      @RequestParam("beginTime") String beginTime,
                                      @RequestParam("endTime") String endTime,
                                      @RequestParam(value = "organId", required = false) Long organId,
                                      @RequestParam(value = "depotId", required = false) Long depotId,
                                      @RequestParam("materialParam") String materialParam,
                                      @RequestParam("mpList") String mpList,
                                      @RequestParam(value = "roleType", required = false) String roleType,
                                      HttpServletRequest request) throws Exception {
        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.retailOut(forceFlag,currentPage, pageSize, beginTime, endTime, organId, depotId, materialParam, mpList, roleType);

    }


    /**
     * 销售统计
     *
     * @param currentPage
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @param materialParam
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/saleOut")
    @Operation(summary = "销售统计")
    public BaseResponseInfo saleOut(@RequestParam("currentPage") Integer currentPage,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("beginTime") String beginTime,
                                    @RequestParam("endTime") String endTime,
                                    @RequestParam(value = "organId", required = false) Long organId,
                                    @RequestParam(value = "depotId", required = false) Long depotId,
                                    @RequestParam("materialParam") String materialParam,
                                    @RequestParam("mpList") String mpList,
                                    @RequestParam(value = "roleType", required = false) String roleType,
                                    HttpServletRequest request) throws Exception {
        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.saleOut(forceFlag,currentPage, pageSize, beginTime, endTime, organId, depotId, materialParam, mpList, roleType);

    }


    /**
     * 库存预警报表
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/findStockWarningCount")
    @Operation(summary = "库存预警报表")
    public BaseResponseInfo findStockWarningCount(@RequestParam("currentPage") Integer currentPage,
                                                  @RequestParam("pageSize") Integer pageSize,
                                                  @RequestParam("materialParam") String materialParam,
                                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                                  @RequestParam("mpList") String mpList) throws Exception {

        return accountCenterService.findStockWarningCount(currentPage, pageSize, materialParam, depotId, mpList);

    }

    /**
     * 统计采购、销售、零售的总金额
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/buyOrSalePrice")
    @Operation(summary = "统计采购、销售、零售的总金额")
    public BaseResponseInfo buyOrSalePrice(@RequestParam(value = "roleType", required = false) String roleType,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {

        return accountCenterService.buyOrSalePrice(roleType);
    }

    /**
     * 获取批次商品列表信息
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getBatchNumberList")
    @Operation(summary = "获取批次商品列表信息")
    public BaseResponseInfo getBatchNumberList(@RequestParam("name") String name,
                                               @RequestParam("depotItemId") Long depotItemId,
                                               @RequestParam("depotId") Long depotId,
                                               @RequestParam("barCode") String barCode,
                                               @RequestParam(value = "batchNumber", required = false) String batchNumber,
                                               HttpServletRequest request) throws Exception {

        return accountCenterService.getBatchNumberList(name, depotItemId, depotId, barCode, batchNumber);
    }

    /**
     * Excel导入明细
     *
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importItemExcel")
    public BaseResponseInfo importItemExcel(MultipartFile file,
                                            @RequestParam(required = false, value = "prefixNo") String prefixNo,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return accountCenterService.importItemExcel(file, prefixNo);
    }

    /**
     * 仓库列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/depot/getAllList")
    @Operation(summary = "仓库列表")
    public BaseResponseInfo depotGetAllList(HttpServletRequest request) throws Exception {
        return accountCenterService.depotGetAllList();
    }

    /**
     * 用户对应仓库显示
     *
     * @param type
     * @param keyId
     * @param request
     * @return
     */
    @GetMapping(value = "/findUserDepot")
    @Operation(summary = "用户对应仓库显示")
    public JSONArray findUserDepot(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                   HttpServletRequest request) throws Exception {
        return accountCenterService.findUserDepot(type, keyId);
    }

    /**
     * 获取当前用户拥有权限的仓库列表
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/findDepotByCurrentUser")
    @Operation(summary = "获取当前用户拥有权限的仓库列表")
    public BaseResponseInfo findDepotByCurrentUser(HttpServletRequest request) throws Exception {
        return accountCenterService.findDepotByCurrentUser();
    }

    /**
     * 更新默认仓库
     *
     * @param object
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/updateIsDefault")
    @Operation(summary = "更新默认仓库")
    public String updateIsDefault(@RequestBody JSONObject object,
                                  HttpServletRequest request) throws Exception {
        return accountCenterService.updateIsDefault(object);
    }

    /**
     * 仓库列表-带库存
     *
     * @param mId
     * @param request
     * @return
     */
    @GetMapping(value = "/getAllListWithStock")
    @Operation(summary = "仓库列表-带库存")
    public BaseResponseInfo getAllList(@RequestParam("mId") Long mId,
                                       HttpServletRequest request) {
        return accountCenterService.getAllList(mId);
    }

    /**
     * 批量设置状态-启用或者禁用
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/depot/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String depotBatchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request) throws Exception {
        return accountCenterService.depotBatchSetStatus(jsonObject);
    }


    /**
     * 查找结算账户信息-下拉框
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @Operation(summary = "查找结算账户信息-下拉框")
    public String findBySelect(HttpServletRequest request) throws Exception {
        return accountCenterService.findBySelect();
    }

    /**
     * 获取所有结算账户
     * @param request
     * @return
     */
    @GetMapping(value = "/getAccount")
    @Operation(summary = "获取所有结算账户")
    public BaseResponseInfo getAccount(HttpServletRequest request) throws Exception {
        return accountCenterService.getAccount();
    }

    /**
     * 账户流水信息
     * @param currentPage
     * @param pageSize
     * @param accountId
     * @param initialAmount
     * @param request
     * @return
     */
    @GetMapping(value = "/findAccountInOutList")
    @Operation(summary = "账户流水信息")
    public BaseResponseInfo findAccountInOutList(@RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("accountId") Long accountId,
                                                 @RequestParam("initialAmount") BigDecimal initialAmount,
                                                 HttpServletRequest request) throws Exception{
        boolean forceFlag=false;
        BaseResponseInfo baseResponseInfo=systemCenterService.getCurrentInfo();
        if(baseResponseInfo.code == 200 && baseResponseInfo.data!=null) {
            String flag = ((Map)baseResponseInfo.data).get("forceApprovalFlag").toString();
            //SystemConfig systemConfig = JSONObject.parseObject(baseResponseInfo.data.toString(), SystemConfig.class);
            if(("1").equals(flag)) {
                forceFlag = true;
            }
        }
        return accountCenterService.findAccountInOutList(forceFlag,currentPage,pageSize,accountId,initialAmount);
    }

    /**
     * 更新默认账户
     * @param object
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/account/updateIsDefault")
    @Operation(summary = "更新默认账户")
    public String accountUpdateIsDefault(@RequestBody JSONObject object,
                                  HttpServletRequest request) throws Exception{
        return accountCenterService.accountUpdateIsDefault(object);
    }

    /**
     * 结算账户的统计
     * @param request
     * @return
     */
    @GetMapping(value = "/getStatistics")
    @Operation(summary = "结算账户的统计")
    public BaseResponseInfo getStatistics(@RequestParam("name") String name,
                                          @RequestParam("serialNo") String serialNo,
                                          HttpServletRequest request) throws Exception {
        return accountCenterService.getStatistics(name,serialNo);
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/account/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String accountBatchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        return accountCenterService.accountBatchSetStatus(jsonObject);
    }

    /**
     * 单据编号生成接口
     * @param request
     * @return
     */
    @GetMapping(value = "/buildNumber")
    @Operation(summary = "单据编号生成接口")
    public BaseResponseInfo buildNumber(HttpServletRequest request)throws Exception {
        return accountCenterService.buildNumber();

    }

    @GetMapping(value = "/accountItem/getDetailList")
    @Operation(summary = "财务明细列表")
    public BaseResponseInfo accountItemGetDetailList(@RequestParam("headerId") Long headerId,
                                          HttpServletRequest request)throws Exception {

        return accountCenterService.accountItemGetDetailList(headerId);
    }




































}
