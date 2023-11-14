package com.jzh.hystrix.consumer.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.Supplier;
import com.jzh.erp.utils.BaseResponseInfo;
import com.jzh.hystrix.consumer.config.FeignConfigure;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Component
@FeignClient(value = "CLOUD-JZH-ERP2",configuration = FeignConfigure.class)
public interface MerchandiseCenterService {

/**
 *  以下接口为商品中心模块
 */
@GetMapping(value = "/jzhERP-boot2/materialCategory/getAllList")
BaseResponseInfo getAllList(@RequestParam("parentId") Long parentId);

    @GetMapping(value = "/jzhERP-boot2/materialCategory/findById")
     BaseResponseInfo findById(@RequestParam("id") Long id);
    @GetMapping(value = "/jzhERP-boot2/materialCategory/getMaterialCategoryTree")
     JSONArray getMaterialCategoryTree(@RequestParam("id") Long id);

    @PostMapping(value = "/jzhERP-boot2/materialCategory/addMaterialCategory")
     Object addMaterialCategory(@RequestParam("info") String beanJson);

    @PutMapping(value = "/jzhERP-boot2/materialCategory/editMaterialCategory")
    Object editMaterialCategory(@RequestParam("info") String beanJson);


    @GetMapping(value = "/jzhERP-boot2/unit/getAllList")
     BaseResponseInfo unitGetAllList();

    @PostMapping(value = "/jzhERP-boot2/unit/batchSetStatus")
     String unitBatchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/supplier/checkIsNameAndTypeExist")
     String checkIsNameAndTypeExist(@RequestParam Long id,
                                          @RequestParam(value ="name", required = false) String name,
                                          @RequestParam(value ="type") String type);

    @PostMapping(value = "/jzhERP-boot2/supplier/findBySelect_cus")
     JSONArray findBySelectCus();

    @PostMapping(value = "/jzhERP-boot2/supplier/findBySelect_sup")
     JSONArray findBySelectSup();

    @PostMapping(value = "/jzhERP-boot2/supplier/findBySelect_organ")
     JSONArray findBySelectOrgan();

    @PostMapping(value = "/jzhERP-boot2/supplier/findBySelect_retail")
     JSONArray findBySelectRetail();

    @PostMapping(value = "/jzhERP-boot2/supplier/batchSetStatus")
     String batchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/supplier/findUserCustomer")
     JSONArray findUserCustomer(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId);

    @GetMapping(value = "/jzhERP-boot2/supplier/getBeginNeedByOrganId")
     BaseResponseInfo getBeginNeedByOrganId(@RequestParam("organId") Long organId);

    @PostMapping(value = "/jzhERP-boot2/supplier/importVendor")
     BaseResponseInfo importVendor(MultipartFile file);

    @PostMapping(value = "/jzhERP-boot2/supplier/importCustomer")
    BaseResponseInfo importCustomer(MultipartFile file);

    @PostMapping(value = "/jzhERP-boot2/supplier/importMember")
    BaseResponseInfo importMember(MultipartFile file);

    @GetMapping(value = "/jzhERP-boot2/supplier/exportExcel")
    Response exportExcel(@RequestParam(value = "supplier", required = false) String supplier,
                         @RequestParam("type") String type,
                         @RequestParam(value = "phonenum", required = false) String phonenum,
                         @RequestParam(value = "telephone", required = false) String telephone);

    @GetMapping(value = "/jzhERP-boot2/supplier/getSupplier")
    Supplier getSupplierByNameAndType(@RequestParam(value = "supplier") String supplier,
                         @RequestParam("type") String type);


    @GetMapping(value = "/jzhERP-boot2/material/checkIsExist")
     String checkIsExist(@RequestParam("id") Long id, @RequestParam("name") String name,
                               @RequestParam("model") String model, @RequestParam("color") String color,
                               @RequestParam("standard") String standard, @RequestParam("mfrs") String mfrs,
                               @RequestParam("otherField1") String otherField1, @RequestParam("otherField2") String otherField2,
                               @RequestParam("otherField3") String otherField3, @RequestParam("unit") String unit,@RequestParam("unitId") Long unitId);

    @PostMapping(value = "/jzhERP-boot2/material/batchSetStatus")
     String materialBatchSetStatus(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/material/findById")
    @Operation(summary = "根据id来查询商品名称")
     BaseResponseInfo merchanFindById(@RequestParam("id") Long id);

    @GetMapping(value = "/jzhERP-boot2/material/findByIdWithBarCode")
     BaseResponseInfo findByIdWithBarCode(@RequestParam("meId") Long meId,
                                                @RequestParam("mpList") String mpList);

    @GetMapping(value = "/jzhERP-boot2/material/getMaterialByParam")
     BaseResponseInfo getMaterialByParam(@RequestParam("q") String q);

    @GetMapping(value = "/jzhERP-boot2/material/findBySelect")
     JSONObject findBySelect(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "mpList", required = false) String mpList,
                                   @RequestParam(value = "depotId", required = false) Long depotId,
                                   @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                                   @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                                   @RequestParam("page") Integer currentPage,
                                   @RequestParam("rows") Integer pageSize);

    @GetMapping(value = "/jzhERP-boot2/material/getMaterialByMeId")
    JSONObject getMaterialByMeId(@RequestParam(value = "meId", required = false) Long meId,
                                        @RequestParam("mpList") String mpList);

    @GetMapping(value = "/jzhERP-boot2/material/exportExcel")
    Response exportExcel(@RequestParam(value = "categoryId", required = false) String categoryId,
                            @RequestParam(value = "materialParam", required = false) String materialParam,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "weight", required = false) String weight,
                            @RequestParam(value = "expiryNum", required = false) String expiryNum,
                            @RequestParam(value = "enabled", required = false) String enabled,
                            @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                            @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "mpList", required = false) String mpList);

    @PostMapping(value = "/jzhERP-boot2/material/importExcel")
     BaseResponseInfo importExcel(MultipartFile file);

    @GetMapping(value = "/jzhERP-boot2/material/getMaterialEnableSerialNumberList")
    JSONObject getMaterialEnableSerialNumberList(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam("page") Integer currentPage,
            @RequestParam("rows") Integer pageSize);

    @GetMapping(value = "/jzhERP-boot2/material/getMaxBarCode")
     BaseResponseInfo getMaxBarCode();

    @GetMapping(value = "/jzhERP-boot2/material/getMaterialNameList")
     JSONArray getMaterialNameList();

    @GetMapping(value = "/jzhERP-boot2/material/getMaterialByBarCode")
     BaseResponseInfo getMaterialByBarCode(@RequestParam("barCode") String barCode,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam("mpList") String mpList,
                                                 @RequestParam(required = false, value = "prefixNo") String prefixNo);

    @GetMapping(value = "/jzhERP-boot2/material/getListWithStock")
    BaseResponseInfo getListWithStock(@RequestParam("currentPage") Integer currentPage,
                                             @RequestParam("pageSize") Integer pageSize,
                                             @RequestParam(value = "depotIds", required = false) String depotIds,
                                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                                             @RequestParam("materialParam") String materialParam,
                                             @RequestParam("zeroStock") Integer zeroStock,
                                             @RequestParam("mpList") String mpList,
                                             @RequestParam("column") String column,
                                             @RequestParam("order") String order);

    @PostMapping(value = "/jzhERP-boot2/material/batchSetMaterialCurrentStock")
    String batchSetMaterialCurrentStock(@RequestBody JSONObject jsonObject);

    @PostMapping(value = "/jzhERP-boot2/material/batchUpdate")
     String batchUpdate(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/jzhERP-boot2/materialsExtend/getDetailList")
     BaseResponseInfo getDetailList(@RequestParam("materialId") Long materialId);

    @GetMapping(value = "/jzhERP-boot2/materialsExtend/getInfoByBarCode")
     BaseResponseInfo getInfoByBarCode(@RequestParam("barCode") String barCode);

    @GetMapping(value = "/jzhERP-boot2/materialsExtend/checkIsBarCodeExist")
     BaseResponseInfo checkIsBarCodeExist(@RequestParam("id") Long id,
                                                @RequestParam("barCode") String barCode);

    @GetMapping(value = "/jzhERP-boot2/materialProperty/getAllList")
    BaseResponseInfo materialPropertyGetAllList();

    @GetMapping(value = "/jzhERP-boot2/materialAttribute/getNameList")
     JSONArray getNameList();

    @GetMapping(value = "/jzhERP-boot2/materialAttribute/getValueListById")
     JSONArray getValueListById(@RequestParam("id") Long id);
























}
