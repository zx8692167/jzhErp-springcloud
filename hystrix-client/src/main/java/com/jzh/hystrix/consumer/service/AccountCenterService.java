package com.jzh.hystrix.consumer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.AccountHeadVo4Body;
import com.jzh.erp.datasource.entities.DepotHeadVo4Body;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.erp.utils.Constants;
import com.jzh.hystrix.consumer.config.FeignConfigure;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Component
@FeignClient(value = "CLOUD-JZH-ERP2",configuration = FeignConfigure.class)
public interface AccountCenterService {

    @GetMapping(value = "/jzhERP-boot2/account/configInfo")
    String getConfigInfo();

    /**
     *  以下接口为結算中心模块
     */
    @GetMapping(value = "/jzhERP-boot2/{apiName}/info")
    String getList2(@PathVariable("apiName") String apiName,
                    @RequestParam("id") Long id);

    @GetMapping(value = "/jzhERP-boot2/{apiName}/list")
    String getList2(@PathVariable("apiName") String apiName,
                    @RequestParam(value = Constants.PAGE_SIZE, required = false) Integer pageSize,
                    @RequestParam(value = Constants.CURRENT_PAGE, required = false) Integer currentPage,
                    @RequestParam(value = Constants.SEARCH, required = false) String search);

    @PostMapping(value = "/jzhERP-boot2/{apiName}/add", produces = {"application/javascript", "application/json"})
    String addResource2(@PathVariable("apiName") String apiName,
                        @RequestBody JSONObject obj);

    @PutMapping(value = "/jzhERP-boot2/{apiName}/update", produces = {"application/javascript", "application/json"})
    String updateResource2(@PathVariable("apiName") String apiName,
                           @RequestBody JSONObject obj);

    @DeleteMapping(value = "/jzhERP-boot2/{apiName}/delete", produces = {"application/javascript", "application/json"})
    String deleteResource2(@PathVariable("apiName") String apiName,
                           @RequestParam("id") Long id);


    @DeleteMapping(value = "/jzhERP-boot2/{apiName}/deleteBatch", produces = {"application/javascript", "application/json"})
    String batchDeleteResource2(@PathVariable("apiName") String apiName,
                                @RequestParam("ids") String ids);

    @GetMapping(value = "/jzhERP-boot2/{apiName}/checkIsNameExist")
    String checkIsNameExist2(@PathVariable("apiName") String apiName,
                             @RequestParam Long id, @RequestParam(value ="name", required = false) String name);







    @PostMapping(value = "/jzhERP-boot2/depotHead/batchSetStatus")
     String batchSetStatus(@RequestBody JSONObject jsonObject);


    @GetMapping(value = "/jzhERP-boot2/depotHead/findInOutDetail")
    BaseResponseInfo findInOutDetail(@RequestParam("forceFlag")boolean forceFlag,
            @RequestParam("currentPage") Integer currentPage,
                                            @RequestParam("pageSize") Integer pageSize,
                                            @RequestParam(value = "organId", required = false) Integer oId,
                                            @RequestParam("number") String number,
                                            @RequestParam("materialParam") String materialParam,
                                            @RequestParam(value = "depotId", required = false) Long depotId,
                                            @RequestParam("beginTime") String beginTime,
                                            @RequestParam("endTime") String endTime,
                                            @RequestParam(value = "roleType", required = false) String roleType,
                                            @RequestParam("type") String type,
                                            @RequestParam("remark") String remark);

    @GetMapping(value = "/jzhERP-boot2/depotHead/findInOutMaterialCount")
     BaseResponseInfo findInOutMaterialCount(@RequestParam("forceFlag")boolean forceFlag,
                                                @RequestParam("currentPage") Integer currentPage,
                                                   @RequestParam("pageSize") Integer pageSize,
                                                   @RequestParam(value = "organId", required = false) Integer oId,
                                                   @RequestParam("materialParam") String materialParam,
                                                   @RequestParam(value = "depotId", required = false) Long depotId,
                                                   @RequestParam("beginTime") String beginTime,
                                                   @RequestParam("endTime") String endTime,
                                                   @RequestParam("type") String type,
                                                   @RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotHead/findAllocationDetail")
    BaseResponseInfo findallocationDetail(@RequestParam("forceFlag")boolean forceFlag,
                                                @RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("number") String number,
                                                 @RequestParam("materialParam") String materialParam,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam(value = "depotIdF", required = false) Long depotIdF,
                                                 @RequestParam("beginTime") String beginTime,
                                                 @RequestParam("endTime") String endTime,
                                                 @RequestParam("subType") String subType,
                                                 @RequestParam(value = "roleType", required = false) String roleType,
                                                 @RequestParam("remark") String remark);

    @GetMapping(value = "/jzhERP-boot2/depotHead/getStatementAccount")
     BaseResponseInfo getStatementAccount(@RequestParam("currentPage") Integer currentPage,
                                                @RequestParam("pageSize") Integer pageSize,
                                                @RequestParam("beginTime") String beginTime,
                                                @RequestParam("endTime") String endTime,
                                                @RequestParam(value = "organId", required = false) Integer organId,
                                                @RequestParam("supplierType") String supplierType);

    @GetMapping(value = "/jzhERP-boot2/depotHead/getDetailByNumber")
     BaseResponseInfo getDetailByNumber(@RequestParam("number") String number);

    @GetMapping(value = "/jzhERP-boot2/depotHead/getBillListByLinkNumber")
     BaseResponseInfo getBillListByLinkNumber(@RequestParam("number") String number);

    @PostMapping(value = "/jzhERP-boot2/depotHead/addDepotHeadAndDetail")
     Object addDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body);

    @PutMapping(value = "/jzhERP-boot2/depotHead/updateDepotHeadAndDetail")
     Object updateDepotHeadAndDetail(@RequestBody DepotHeadVo4Body body);

    @GetMapping(value = "/jzhERP-boot2/depotHead/getBuyAndSaleStatistics")
     BaseResponseInfo getBuyAndSaleStatistics(@RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotHead/getCreatorByCurrentUser")
     BaseResponseInfo getCreatorByRoleType();

    @GetMapping(value = "/jzhERP-boot2/depotHead/debtList")
     String debtList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                           @RequestParam("currentPage") Integer currentPage,
                           @RequestParam("pageSize") Integer pageSize);

    @PostMapping(value = "/jzhERP-boot2/accountHead/batchSetStatus")
     String accountHeadBatchSetStatus(@RequestBody JSONObject jsonObject);

    @PostMapping(value = "/jzhERP-boot2/accountHead/addAccountHeadAndDetail")
     Object addAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body);

    @PutMapping(value = "/jzhERP-boot2/accountHead/updateAccountHeadAndDetail")
     Object updateAccountHeadAndDetail(@RequestBody AccountHeadVo4Body body);

    @GetMapping(value = "/jzhERP-boot2/accountHead/getDetailByNumber")
     BaseResponseInfo accountHeadGetDetailByNumber(@RequestParam("billNo") String billNo);

    @GetMapping(value = "/jzhERP-boot2/accountHead/getFinancialBillNoByBillId")
     BaseResponseInfo getFinancialBillNoByBillId(@RequestParam("billId") Long billId);

    @PostMapping("/jzhERP-boot2/serialNumber/batAddSerialNumber")
     String batAddSerialNumber(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/serialNumber/getEnableSerialNumberList")
     BaseResponseInfo getEnableSerialNumberList(@RequestParam("name") String name,
                                                      @RequestParam("depotItemId") Long depotItemId,
                                                      @RequestParam("depotId") Long depotId,
                                                      @RequestParam("barCode") String barCode,
                                                      @RequestParam("page") Integer currentPage,
                                                      @RequestParam("rows") Integer pageSize);

    @GetMapping(value = "/jzhERP-boot2/person/getAllList")
     BaseResponseInfo personGetAllList();

    @GetMapping(value = "/jzhERP-boot2/person/getPersonByIds")
     BaseResponseInfo getPersonByIds(@RequestParam("personIds") String personIds);

    @GetMapping(value = "/jzhERP-boot2/person/getPersonByType")
     BaseResponseInfo getPersonByType(@RequestParam("type") String type);

    @GetMapping(value = "/jzhERP-boot2/person/getPersonByNumType")
     JSONArray getPersonByNumType(@RequestParam("type") String typeNum);

    @PostMapping(value = "/jzhERP-boot2/person/batchSetStatus")
     String personBatchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/inOutItem/findBySelect")
     String inOutItemFindBySelect(@RequestParam("type") String type);

    @PostMapping(value = "/jzhERP-boot2/inOutItem/batchSetStatus")
     String inOutItemBatchSetStatus(@RequestBody JSONObject jsonObject);


    @GetMapping(value = "/jzhERP-boot2/depotItem/findDetailByDepotIdsAndMaterialId")
     String findDetailByDepotIdsAndMaterialId(@RequestParam("forceFlag")boolean forceFlag,
            @RequestParam(value = Constants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = Constants.CURRENT_PAGE, required = false) Integer currentPage,
            @RequestParam(value = "depotIds",required = false) String depotIds,
            @RequestParam(value = "sku",required = false) String sku,
            @RequestParam(value = "batchNumber",required = false) String batchNumber,
            @RequestParam(value = "number",required = false) String number,
            @RequestParam(value = "beginTime",required = false) String beginTime,
            @RequestParam(value = "endTime",required = false) String endTime,
            @RequestParam("materialId") Long mId);

    @GetMapping(value = "/jzhERP-boot2/depotItem/findStockByDepotAndBarCode")
     BaseResponseInfo findStockByDepotAndBarCode(
            @RequestParam(value = "depotId",required = false) Long depotId,
            @RequestParam("barCode") String barCode);

    @GetMapping(value = "/jzhERP-boot2/depotItem/getDetailList")
     BaseResponseInfo getDetailList(@RequestParam("headerId") Long headerId,
                                          @RequestParam("mpList") String mpList,
                                          @RequestParam(value = "linkType", required = false) String linkType,
                                          @RequestParam(value = "isReadOnly", required = false) String isReadOnly);


    @GetMapping(value = "/jzhERP-boot2/depotItem/findByAll")
     BaseResponseInfo findByAll(@RequestParam("currentPage") Integer currentPage,
                                      @RequestParam("pageSize") Integer pageSize,
                                      @RequestParam(value = "depotIds",required = false) String depotIds,
                                      @RequestParam(value = "categoryId", required = false) Long categoryId,
                                      @RequestParam("monthTime") String monthTime,
                                      @RequestParam("materialParam") String materialParam,
                                      @RequestParam("mpList") String mpList);

    @GetMapping(value = "/jzhERP-boot2/depotItem/totalCountMoney")
     BaseResponseInfo totalCountMoney(@RequestParam(value = "depotIds",required = false) String depotIds,
                                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                                            @RequestParam("monthTime") String monthTime,
                                            @RequestParam("materialParam") String materialParam);

    @GetMapping(value = "/jzhERP-boot2/depotItem/buyIn")
     BaseResponseInfo buyIn(@RequestParam("forceFlag")boolean forceFlag,
             @RequestParam("currentPage") Integer currentPage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("beginTime") String beginTime,
                                  @RequestParam("endTime") String endTime,
                                  @RequestParam(value = "organId", required = false) Long organId,
                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                  @RequestParam("materialParam") String materialParam,
                                  @RequestParam("mpList") String mpList,
                                  @RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotItem/retailOut")
     BaseResponseInfo retailOut(@RequestParam("forceFlag")boolean forceFlag,
             @RequestParam("currentPage") Integer currentPage,
                                      @RequestParam("pageSize") Integer pageSize,
                                      @RequestParam("beginTime") String beginTime,
                                      @RequestParam("endTime") String endTime,
                                      @RequestParam(value = "organId", required = false) Long organId,
                                      @RequestParam(value = "depotId", required = false) Long depotId,
                                      @RequestParam("materialParam") String materialParam,
                                      @RequestParam("mpList") String mpList,
                                      @RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotItem/saleOut")
     BaseResponseInfo saleOut(@RequestParam("forceFlag")boolean forceFlag,
                              @RequestParam("currentPage") Integer currentPage,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("beginTime") String beginTime,
                                    @RequestParam("endTime") String endTime,
                                    @RequestParam(value = "organId", required = false) Long organId,
                                    @RequestParam(value = "depotId", required = false) Long depotId,
                                    @RequestParam("materialParam") String materialParam,
                                    @RequestParam("mpList") String mpList,
                                    @RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotItem/findStockWarningCount")
     BaseResponseInfo findStockWarningCount(@RequestParam("currentPage") Integer currentPage,
                                                  @RequestParam("pageSize") Integer pageSize,
                                                  @RequestParam("materialParam") String materialParam,
                                                  @RequestParam(value = "depotId", required = false) Long depotId,
                                                  @RequestParam("mpList") String mpList);

    @GetMapping(value = "/jzhERP-boot2/depotItem/buyOrSalePrice")
     BaseResponseInfo buyOrSalePrice(@RequestParam(value = "roleType", required = false) String roleType);

    @GetMapping(value = "/jzhERP-boot2/depotItem/getBatchNumberList")
     BaseResponseInfo getBatchNumberList(@RequestParam("name") String name,
                                               @RequestParam("depotItemId") Long depotItemId,
                                               @RequestParam("depotId") Long depotId,
                                               @RequestParam("barCode") String barCode,
                                               @RequestParam(value = "batchNumber", required = false) String batchNumber);

    @PostMapping(value = "/jzhERP-boot2/depotItem/importItemExcel")
     BaseResponseInfo importItemExcel(MultipartFile file,
                                            @RequestParam(required = false, value = "prefixNo") String prefixNo);

    @GetMapping(value = "/jzhERP-boot2/depot/getAllList")
     BaseResponseInfo depotGetAllList();

    @GetMapping(value = "/jzhERP-boot2/depot/findUserDepot")
     JSONArray findUserDepot(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId);

    @GetMapping(value = "/jzhERP-boot2/depot/findDepotByCurrentUser")
     BaseResponseInfo findDepotByCurrentUser();

    @PostMapping(value = "/jzhERP-boot2/depot/updateIsDefault")
     String updateIsDefault(@RequestBody JSONObject object);

    @GetMapping(value = "/jzhERP-boot2/depot/getAllListWithStock")
     BaseResponseInfo getAllList(@RequestParam("mId") Long mId);

    @PostMapping(value = "/jzhERP-boot2/depot/batchSetStatus")
     String depotBatchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/account/findBySelect")
     String findBySelect();

    @GetMapping(value = "/jzhERP-boot2/account/getAccount")
    @Operation(summary = "获取所有结算账户")
     BaseResponseInfo getAccount();

    @GetMapping(value = "/jzhERP-boot2/account/findAccountInOutList")
     BaseResponseInfo findAccountInOutList(@RequestParam("forceFlag")boolean forceFlag,
             @RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize,
                                                 @RequestParam("accountId") Long accountId,
                                                 @RequestParam("initialAmount") BigDecimal initialAmount);

    @PostMapping(value = "/jzhERP-boot2/account/updateIsDefault")
     String accountUpdateIsDefault(@RequestBody JSONObject object);

    @GetMapping(value = "/jzhERP-boot2/account/getStatistics")
     BaseResponseInfo getStatistics(@RequestParam("name") String name,
                                          @RequestParam("serialNo") String serialNo);

    @PostMapping(value = "/jzhERP-boot2/account/batchSetStatus")
     String accountBatchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/sequence/buildNumber")
     BaseResponseInfo buildNumber();

    @GetMapping(value = "/accountItem/getDetailList")
     BaseResponseInfo accountItemGetDetailList(@RequestParam("headerId") Long headerId);












}
