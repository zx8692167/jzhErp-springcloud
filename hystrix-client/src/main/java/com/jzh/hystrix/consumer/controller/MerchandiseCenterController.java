package com.jzh.hystrix.consumer.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.datasource.entities.MaterialVo4Unit;
import com.jzh.erp.datasource.entities.Supplier;
import com.jzh.erp.utils.*;
import com.jzh.hystrix.consumer.service.MerchandiseCenterService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping(value = "/merchan_center")
@Tag(name = "商品中心模块")
@Slf4j
public class MerchandiseCenterController {
    @Resource
    private MerchandiseCenterService merchandiseCenterService;

    /**
     * 获取全部商品类别
     * @param parentId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getAllList")
    @Operation(summary = "获取全部商品类别")
    public BaseResponseInfo getAllList(@RequestParam("parentId") Long parentId, HttpServletRequest request) throws Exception{

        return merchandiseCenterService.getAllList(parentId);
    }

    /**
     * 根据商品类别id来查询商品名称
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/category/findById")
    @Operation(summary = "根据id来查询商品名称")
    public BaseResponseInfo categoryFindById(@RequestParam("id") Long id, HttpServletRequest request)throws Exception {
        return merchandiseCenterService.findById(id);
    }
    /**
     * create by: jwh
     * description:
     * 获取商品类别树数据
     * create time: 2019/2/19 11:49
     * @Param:
     * @return com.alibaba.fastjson.JSONArray
     */
    @GetMapping(value = "/getMaterialCategoryTree")
    @Operation(summary = "获取商品类别树数据")
    public JSONArray getMaterialCategoryTree(@RequestParam("id") Long id) throws Exception{
        return merchandiseCenterService.getMaterialCategoryTree(id);
    }
    /**
     * create by: jwh
     * description:
     *  新增商品类别数据
     * create time: 2019/2/19 17:17
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PostMapping(value = "/addMaterialCategory")
    @Operation(summary = "新增商品类别数据")
    public Object addMaterialCategory(@RequestParam("info") String beanJson) throws Exception {
        return merchandiseCenterService.addMaterialCategory(beanJson);
    }
    /**
     * create by: jwh
     * description:
     *  修改商品类别数据
     * create time: 2019/2/20 9:30
     * @Param: beanJson
     * @return java.lang.Object
     */
    @PutMapping(value = "/editMaterialCategory")
    @Operation(summary = "修改商品类别数据")
    public Object editMaterialCategory(@RequestParam("info") String beanJson) throws Exception {
        return merchandiseCenterService.editMaterialCategory(beanJson);
    }


    /**
     * 单位列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/unit/getAllList")
    @Operation(summary = "单位列表")
    public BaseResponseInfo unitGetAllList(HttpServletRequest request) throws Exception{
        return merchandiseCenterService.unitGetAllList();
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     */
    @PostMapping(value = "/unit/batchSetStatus")
    @Operation(summary = "批量设置状态")
    public String unitBatchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
            return merchandiseCenterService.unitBatchSetStatus(jsonObject);
    }




    @GetMapping(value = "/checkIsNameAndTypeExist")
    @Operation(summary = "检查名称和类型是否存在")
    public String checkIsNameAndTypeExist(@RequestParam Long id,
                                          @RequestParam(value ="name", required = false) String name,
                                          @RequestParam(value ="type") String type,
                                          HttpServletRequest request)throws Exception {

        return merchandiseCenterService.checkIsNameAndTypeExist(id,name,type);
    }

    /**
     * 查找客户信息-下拉框
     * @param request
     * @return
     */
    @PostMapping(value = "/findBySelect_cus")
    @Operation(summary = "查找客户信息")
    public JSONArray findBySelectCus(HttpServletRequest request) {
        return merchandiseCenterService.findBySelectCus();
    }

    /**
     * 查找供应商信息-下拉框
     * @param request
     * @return
     */
    @PostMapping(value = "/findBySelect_sup")
    @Operation(summary = "查找供应商信息")
    public JSONArray findBySelectSup(HttpServletRequest request) throws Exception{

        return merchandiseCenterService.findBySelectSup();
    }

    /**
     * 查找往来单位，含供应商和客户信息-下拉框
     * @param request
     * @return
     */
    @PostMapping(value = "/findBySelect_organ")
    @Operation(summary = "查找往来单位，含供应商和客户信息")
    public JSONArray findBySelectOrgan(HttpServletRequest request) throws Exception{

        return merchandiseCenterService.findBySelectOrgan();
    }

    /**
     * 查找会员信息-下拉框
     * @param request
     * @return
     */
    @PostMapping(value = "/findBySelect_retail")
    @Operation(summary = "查找会员信息")
    public JSONArray findBySelectRetail(HttpServletRequest request)throws Exception {

        return merchandiseCenterService.findBySelectRetail();
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

        return merchandiseCenterService.batchSetStatus(jsonObject);
    }

    /**
     * 用户对应客户显示
     * @param type
     * @param keyId
     * @param request
     * @return
     */
    @GetMapping(value = "/findUserCustomer")
    @Operation(summary = "用户对应客户显示")
    public JSONArray findUserCustomer(@RequestParam("UBType") String type, @RequestParam("UBKeyId") String keyId,
                                      HttpServletRequest request) throws Exception{

        return merchandiseCenterService.findUserCustomer(type,keyId);
    }

    /**
     * 根据客户或供应商查询期初、期初已收等信息
     * @param organId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getBeginNeedByOrganId")
    @Operation(summary = "根据客户或供应商查询期初、期初已收等信息")
    public BaseResponseInfo getBeginNeedByOrganId(@RequestParam("organId") Long organId,
                                                  HttpServletRequest request)throws Exception {

        return merchandiseCenterService.getBeginNeedByOrganId(organId);
    }

    /**
     * 导入供应商
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importVendor")
    @Operation(summary = "导入供应商")
    public BaseResponseInfo importVendor(MultipartFile file,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception{
        return merchandiseCenterService.importVendor(file);
    }

    /**
     * 导入客户
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importCustomer")
    @Operation(summary = "导入客户")
    public BaseResponseInfo importCustomer(MultipartFile file,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception{

        return merchandiseCenterService.importCustomer(file);
    }

    /**
     * 导入会员
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importMember")
    @Operation(summary = "导入会员")
    public BaseResponseInfo importMember(MultipartFile file,
                                         HttpServletRequest request, HttpServletResponse response) throws Exception{

        return merchandiseCenterService.importMember(file);
    }

    /**
     * 生成excel表格
     * @param supplier
     * @param type
     * @param phonenum
     * @param telephone
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/exportExcel2")
    @Operation(summary = "生成excel表格")
    public void exportExcel(@RequestParam(value = "supplier", required = false) String supplier,
                            @RequestParam("type") String type,
                            @RequestParam(value = "phonenum", required = false) String phonenum,
                            @RequestParam(value = "telephone", required = false) String telephone,
                            HttpServletRequest request, HttpServletResponse response) {
        Response responseFeign=merchandiseCenterService.exportExcel(supplier,type,phonenum,phonenum);
        ConvertUtil.feignResp2ServletResp(responseFeign,response);
    }

    @GetMapping(value = "/jzhERP-boot2/supplier/getSupplier")
    @Operation(summary = "根据供应商名称跟类型查询供应商信息")   //, required = false
    public BaseResponseInfo getSupplierByNameAndType(@RequestParam(value = "supplier") String supplier,
                                      @RequestParam("type") String type,HttpServletRequest request){
        BaseResponseInfo res=new BaseResponseInfo();
        try {
            Supplier suppliers=merchandiseCenterService.getSupplierByNameAndType(supplier,type);
            res.code = 200;
            res.data = suppliers;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }


    /**
     * 检查商品是否存在
     * @param id
     * @param name
     * @param model
     * @param color
     * @param standard
     * @param mfrs
     * @param otherField1
     * @param otherField2
     * @param otherField3
     * @param unit
     * @param unitId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsExist")
    @Operation(summary = "检查商品是否存在")
    public String checkIsExist(@RequestParam("id") Long id, @RequestParam("name") String name,
                               @RequestParam("model") String model, @RequestParam("color") String color,
                               @RequestParam("standard") String standard, @RequestParam("mfrs") String mfrs,
                               @RequestParam("otherField1") String otherField1, @RequestParam("otherField2") String otherField2,
                               @RequestParam("otherField3") String otherField3, @RequestParam("unit") String unit,@RequestParam("unitId") Long unitId,
                               HttpServletRequest request)throws Exception {

        return merchandiseCenterService.checkIsExist(id,name,model,color,standard,mfrs,otherField1,otherField2,otherField3,unit,unitId);
    }

    /**
     * 批量设置状态-启用或者禁用
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/material/batchSetStatus")
    @Operation(summary = "批量设置状态-启用或者禁用")
    public String materialBatchSetStatus(@RequestBody JSONObject jsonObject,
                                 HttpServletRequest request)throws Exception {
        return merchandiseCenterService.materialBatchSetStatus(jsonObject);
    }

    /**
     * 根据id来查询商品名称
     * @param id
     * @param request
     * @return
     */
    @GetMapping(value = "/findById")
    @Operation(summary = "根据id来查询商品名称")
    public BaseResponseInfo merchanFindById(@RequestParam("id") Long id, HttpServletRequest request) throws Exception{

        return merchandiseCenterService.merchanFindById(id);
    }

    /**
     * 根据meId来查询商品名称
     * @param meId
     * @param request
     * @return
     */
    @GetMapping(value = "/findByIdWithBarCode")
    @Operation(summary = "根据meId来查询商品名称")
    public BaseResponseInfo findByIdWithBarCode(@RequestParam("meId") Long meId,
                                                @RequestParam("mpList") String mpList,
                                                HttpServletRequest request) throws Exception{

        return merchandiseCenterService.findByIdWithBarCode(meId,mpList);
    }

    /**
     * 根据关键词查找商品信息-条码、名称、规格、型号
     * @param q
     * @param request
     * @return
     */
    @GetMapping(value = "/getMaterialByParam")
    @Operation(summary = "根据关键词查找商品信息")
    public BaseResponseInfo getMaterialByParam(@RequestParam("q") String q,
                                               HttpServletRequest request) throws Exception{

        return merchandiseCenterService.getMaterialByParam(q);
    }

    /**
     * 查找商品信息-下拉框
     * @param mpList
     * @param request
     * @return
     */
    @GetMapping(value = "/findBySelect")
    @Operation(summary = "查找商品信息")
    public JSONObject findBySelect(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "mpList", required = false) String mpList,
                                   @RequestParam(value = "depotId", required = false) Long depotId,
                                   @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                                   @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                                   @RequestParam("page") Integer currentPage,
                                   @RequestParam("rows") Integer pageSize,
                                   HttpServletRequest request) throws Exception{

        return merchandiseCenterService.findBySelect(categoryId,q,mpList,depotId,enableSerialNumber,enableBatchNumber,currentPage,pageSize);
    }

    /**
     * 根据商品id查找商品信息
     * @param meId
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByMeId")
    @Operation(summary = "根据商品id查找商品信息")
    public JSONObject getMaterialByMeId(@RequestParam(value = "meId", required = false) Long meId,
                                        @RequestParam("mpList") String mpList,
                                        HttpServletRequest request) throws Exception{

        return merchandiseCenterService.getMaterialByMeId(meId,mpList);

    }

    /**
     * 生成excel表格
     * @param categoryId
     * @param materialParam
     * @param color
     * @param weight
     * @param expiryNum
     * @param enabled
     * @param enableSerialNumber
     * @param enableBatchNumber
     * @param remark
     * @param mpList
     * @param request
     * @param response
     */
    @GetMapping(value = "/exportExcel")
    @Operation(summary = "生成excel表格")
    public void exportExcel(@RequestParam(value = "categoryId", required = false) String categoryId,
                            @RequestParam(value = "materialParam", required = false) String materialParam,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "weight", required = false) String weight,
                            @RequestParam(value = "expiryNum", required = false) String expiryNum,
                            @RequestParam(value = "enabled", required = false) String enabled,
                            @RequestParam(value = "enableSerialNumber", required = false) String enableSerialNumber,
                            @RequestParam(value = "enableBatchNumber", required = false) String enableBatchNumber,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "mpList", required = false) String mpList,
                            HttpServletRequest request, HttpServletResponse response) {
        Response responseFeign=merchandiseCenterService.exportExcel(categoryId,materialParam,color,weight,expiryNum,enabled,enableSerialNumber,enableBatchNumber,remark,mpList);
        ConvertUtil.feignResp2ServletResp(responseFeign,response);
    }

    /**
     * excel表格导入产品（含初始库存）
     * @param file
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/importExcel")
    @Operation(summary = "excel表格导入产品")
    public BaseResponseInfo importExcel(MultipartFile file,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception{

        return merchandiseCenterService.importExcel(file);
    }

    /**
     * 获取商品序列号
     * @param q
     * @param currentPage
     * @param pageSize
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialEnableSerialNumberList")
    @Operation(summary = "获取商品序列号")
    public JSONObject getMaterialEnableSerialNumberList(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam("page") Integer currentPage,
            @RequestParam("rows") Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response)throws Exception {

        return merchandiseCenterService.getMaterialEnableSerialNumberList(q,currentPage,pageSize);
    }

    /**
     * 获取最大条码
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaxBarCode")
    @Operation(summary = "获取最大条码")
    public BaseResponseInfo getMaxBarCode() throws Exception {

        return merchandiseCenterService.getMaxBarCode();
    }

    /**
     * 商品名称模糊匹配
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialNameList")
    @Operation(summary = "商品名称模糊匹配")
    public JSONArray getMaterialNameList() throws Exception {

        return merchandiseCenterService.getMaterialNameList();
    }

    /**
     * 根据条码查询商品信息
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getMaterialByBarCode")
    @Operation(summary = "根据条码查询商品信息")
    public BaseResponseInfo getMaterialByBarCode(@RequestParam("barCode") String barCode,
                                                 @RequestParam(value = "depotId", required = false) Long depotId,
                                                 @RequestParam("mpList") String mpList,
                                                 @RequestParam(required = false, value = "prefixNo") String prefixNo,
                                                 HttpServletRequest request) throws Exception {

        return merchandiseCenterService.getMaterialByBarCode(barCode,depotId,mpList,prefixNo);
    }


    /**
     * 商品库存查询
     * @param currentPage
     * @param pageSize
     * @param depotIds
     * @param categoryId
     * @param materialParam
     * @param mpList
     * @param column
     * @param order
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getListWithStock")
    @Operation(summary = "商品库存查询")
    public BaseResponseInfo getListWithStock(@RequestParam("currentPage") Integer currentPage,
                                             @RequestParam("pageSize") Integer pageSize,
                                             @RequestParam(value = "depotIds", required = false) String depotIds,
                                             @RequestParam(value = "categoryId", required = false) Long categoryId,
                                             @RequestParam("materialParam") String materialParam,
                                             @RequestParam("zeroStock") Integer zeroStock,
                                             @RequestParam("mpList") String mpList,
                                             @RequestParam("column") String column,
                                             @RequestParam("order") String order,
                                             HttpServletRequest request)throws Exception {

        return merchandiseCenterService.getListWithStock(currentPage,pageSize,depotIds,categoryId,materialParam,zeroStock,mpList,column,order);
    }

    /**
     * 批量设置商品当前的实时库存（按每个仓库）
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchSetMaterialCurrentStock")
    @Operation(summary = "批量设置商品当前的实时库存（按每个仓库）")
    public String batchSetMaterialCurrentStock(@RequestBody JSONObject jsonObject,
                                               HttpServletRequest request)throws Exception {
        return merchandiseCenterService.batchSetMaterialCurrentStock(jsonObject);

    }

    /**
     * 批量更新商品信息
     * @param jsonObject
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/batchUpdate")
    @Operation(summary = "批量更新商品信息")
    public String batchUpdate(@RequestBody JSONObject jsonObject,
                              HttpServletRequest request)throws Exception {
        return merchandiseCenterService.batchUpdate(jsonObject);
    }



    @GetMapping(value = "/getDetailList")
    @Operation(summary = "价格信息列表")
    public BaseResponseInfo getDetailList(@RequestParam("materialId") Long materialId,
                                          HttpServletRequest request)throws Exception {

        return merchandiseCenterService.getDetailList(materialId);
    }

    /**
     * 根据条码查询商品信息
     * @param barCode
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getInfoByBarCode")
    @Operation(summary = "根据条码查询商品信息")
    public BaseResponseInfo getInfoByBarCode(@RequestParam("barCode") String barCode,
                                             HttpServletRequest request)throws Exception {

        return merchandiseCenterService.getInfoByBarCode(barCode);
    }

    /**
     * 校验条码是否存在
     * @param id
     * @param barCode
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/checkIsBarCodeExist")
    @Operation(summary = "校验条码是否存在")
    public BaseResponseInfo checkIsBarCodeExist(@RequestParam("id") Long id,
                                                @RequestParam("barCode") String barCode,
                                                HttpServletRequest request)throws Exception {

        return merchandiseCenterService.checkIsBarCodeExist(id,barCode);
    }



    @GetMapping(value = "/materialProperty/getAllList")
    @Operation(summary = "查询全部商品扩展字段信息")
    public BaseResponseInfo materialPropertyGetAllList(HttpServletRequest request) throws Exception{
        return merchandiseCenterService.materialPropertyGetAllList();
    }


    /**
     * 获取商品属性的名称列表
     * @param request
     * @return
     */
    @GetMapping(value = "/getNameList")
    @Operation(summary = "获取商品属性的名称列表")
    public JSONArray getNameList(HttpServletRequest request)throws Exception {
        return merchandiseCenterService.getNameList();
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
        return merchandiseCenterService.getValueListById(id);
    }






























}
