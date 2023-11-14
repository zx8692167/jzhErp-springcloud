package com.jzh.erp.service.depotHead;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.constants.ExceptionConstants;
import com.jzh.erp.datasource.entities.*;
import com.jzh.erp.datasource.mappers.DepotHeadMapper;
import com.jzh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jzh.erp.datasource.mappers.DepotItemMapperEx;
import com.jzh.erp.datasource.vo.*;
import com.jzh.erp.exception.BusinessRunTimeException;
import com.jzh.erp.exception.JzhException;
import com.jzh.erp.service.account.AccountService;
import com.jzh.erp.service.accountHead.AccountHeadService;
import com.jzh.erp.service.accountItem.AccountItemService;
import com.jzh.erp.service.depot.DepotService;
import com.jzh.erp.service.depotItem.DepotItemService;
import com.jzh.erp.service.log.LogService;
//import com.jzh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jzh.erp.service.person.PersonService;
//import com.jzh.erp.service.role.RoleService;
import com.jzh.erp.service.redis.RedisService;
import com.jzh.erp.service.serialNumber.SerialNumberService;
import com.jzh.erp.service.supplier.SupplierService;
//import com.jzh.erp.service.systemConfig.SystemConfigService;
//import com.jzh.erp.service.user.UserService;
//import com.jzh.erp.service.userBusiness.UserBusinessService;
import com.jzh.erp.utils.StringUtil;
import com.jzh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DepotHeadService {
    private Logger logger = LoggerFactory.getLogger(DepotHeadService.class);

    @Resource
    private DepotHeadMapper depotHeadMapper;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;
    /*@Resource
    private UserService userService;*/
    /*@Resource
    private RoleService roleService;*/
    @Resource
    private DepotService depotService;
    @Resource
    DepotItemService depotItemService;
    @Resource
    private SupplierService supplierService;
    /*@Resource
    private UserBusinessService userBusinessService;*/
    /*@Resource
    private SystemConfigService systemConfigService;*/
    @Resource
    private SerialNumberService serialNumberService;
/*    @Resource
    private OrgaUserRelService orgaUserRelService;*/
    @Resource
    private PersonService personService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountHeadService accountHeadService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    DepotItemMapperEx depotItemMapperEx;
    @Resource
    private LogService logService;

    @Resource
    private RedisService redisService;


    public DepotHead getDepotHead(long id)throws Exception {
        DepotHead result=null;
        try{
            result=depotHeadMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHead()throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list=null;
        try{
            list=depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> select(String type, String subType, String roleType, String hasDebt, String status, String purchaseStatus, String number, String linkNumber,
                                         String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId, Long accountId, String remark, int offset, int rows) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String [] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String [] organArray = getOrganArray(subType, purchaseStatus);
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.selectByConditionDepotHead(type, subType, creatorArray, hasDebt, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                 materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                //通过批量查询去构造map
                Map<String,BigDecimal> finishDepositMap = getFinishDepositMapByNumberList(numberList);
                Map<Long,Integer> financialBillNoMap = getFinancialBillNoMapByBillIdList(idList);
                Map<String,Integer> billSizeMap = getBillSizeMapByLinkNumberList(numberList);
                Map<Long,String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                Map<Long,BigDecimal> materialCountListMap = getMaterialCountListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if(dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if(dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    } else {
                        dh.setChangeAmount(BigDecimal.ZERO);
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    //已经完成的欠款
                    if(finishDepositMap!=null) {
                        dh.setFinishDeposit(finishDepositMap.get(dh.getNumber()) != null ? finishDepositMap.get(dh.getNumber()) : BigDecimal.ZERO);
                    }
                    //欠款计算
                    BigDecimal discountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
                    BigDecimal otherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
                    BigDecimal deposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
                    BigDecimal changeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount():BigDecimal.ZERO;
                    dh.setDebt(discountLastMoney.add(otherMoney).subtract((deposit.add(changeAmount))));
                    //是否有付款单或收款单
                    if(financialBillNoMap!=null) {
                        Integer financialBillNoSize = financialBillNoMap.get(dh.getId());
                        dh.setHasFinancialFlag(financialBillNoSize!=null && financialBillNoSize>0);
                    }
                    //是否有退款单
                    if(billSizeMap!=null) {
                        Integer billListSize = billSizeMap.get(dh.getNumber());
                        dh.setHasBackFlag(billListSize!=null && billListSize>0);
                    }
                    if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(Tools.getCenternTime(dh.getOperTime()));
                    }
                    //商品信息简述
                    if(materialsListMap!=null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    //商品总数量
                    if(materialCountListMap!=null) {
                        dh.setMaterialCount(materialCountListMap.get(dh.getId()));
                    }
                    //以销定购的情况（不能显示销售单据的金额和客户名称）
                    if(StringUtil.isNotEmpty(purchaseStatus)) {
                        dh.setOrganName("****");
                        dh.setTotalPrice(null);
                        dh.setDiscountLastMoney(null);
                    }
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return resList;
    }

    public Long countDepotHead(String type, String subType, String roleType, String hasDebt, String status, String purchaseStatus, String number, String linkNumber,
           String beginTime, String endTime, String materialParam, Long organId, Long creator, Long depotId, Long accountId, String remark) throws Exception{
        Long result=null;
        try{
            String [] depotArray = getDepotArray(subType);
            String [] creatorArray = getCreatorArray(roleType);
            String [] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String [] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String [] organArray = getOrganArray(subType, purchaseStatus);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            result=depotHeadMapperEx.countsByDepotHead(type, subType, creatorArray, hasDebt, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime,
                   materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据单据类型获取仓库数组
     * @param subType
     * @return
     * @throws Exception
     */
    public String[] getDepotArray(String subType) throws Exception {
        String [] depotArray = null;
        if(!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(subType) && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)) {
            String depotIds = depotService.findDepotStrByCurrentUser();
            depotArray = StringUtil.isNotEmpty(depotIds) ? depotIds.split(",") : null;
        }
        return depotArray;
    }

    /**
     * 根据角色类型获取操作员数组
     * @param roleType
     * @return
     * @throws Exception
     */
    public String[] getCreatorArray(String roleType) throws Exception {
        String creator = getCreatorByRoleType(roleType);
        String [] creatorArray=null;
        if(StringUtil.isNotEmpty(creator)){
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    /**
     * 获取机构数组
     * @return
     */
    public String[] getOrganArray(String subType, String purchaseStatus) throws Exception {
        String [] organArray = null;
        String type = "UserCustomer";
        //Long userId = userService.getCurrentUser().getId();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object basicDataConfig = redisService.getObjectFromSessionByKey(request,"basicDataConfig");
        //获取权限信息
        //String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, userId.toString());
        String ubValue = ((Map)basicDataConfig).get(type).toString();
        List<Supplier> supplierList = supplierService.findBySelectCus();
        if(BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType) || BusinessConstants.SUB_TYPE_SALES.equals(subType)
                ||BusinessConstants.SUB_TYPE_SALES_RETURN.equals(subType) ) {
            //采购订单里面选择销售订单的时候不要过滤
            if(StringUtil.isEmpty(purchaseStatus)) {
                if (null != supplierList && supplierList.size() > 0) {
                    Object systemConfig = redisService.getObjectFromSessionByKey(request,"systemConfig");
                    boolean cusFlag = ((Map)systemConfig).get("customerFlag").toString()=="1"?true:false;
                    //boolean customerFlag = systemConfigService.getCustomerFlag();

                    List<String> organList = new ArrayList<>();
                    for (Supplier supplier : supplierList) {
                        boolean flag = ubValue.contains("[" + supplier.getId().toString() + "]");
                        if (!cusFlag || flag) {
                            organList.add(supplier.getId().toString());
                        }
                    }
                    if(organList.size() > 0) {
                        organArray = StringUtil.listToStringArray(organList);
                    }
                }
            }
        }
        return organArray;
    }

    /**
     * 根据角色类型获取操作员
     * @param roleType
     * @return
     * @throws Exception
     */
    public String getCreatorByRoleType(String roleType) throws Exception {
        String creator = "";
        //User user = userService.getCurrentUser();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");
        //再从后端获取一次角色类型，防止前端关闭了缓存功能
        if(StringUtil.isEmpty(roleType)) {
            Object role = redisService.getObjectFromSessionByKey(request,"role");
            roleType = String.valueOf(((Map)role).get("type"));
            //roleType =  userService.getRoleTypeByUserId(Long.valueOf(userId.toString())).getType(); //角色类型

        }
        if(BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            //creator = user.getId().toString();
            creator = userId.toString();
        } else if(BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            //creator = orgaUserRelService.getUserIdListByUserId(user.getId());
            creator = String.valueOf(redisService.getObjectFromSessionByKey(request,"orgParentUsers"));
        }
        return creator;
    }

    public Map<String, BigDecimal> getFinishDepositMapByNumberList(List<String> numberList) {
        List<FinishDepositVo> list = depotHeadMapperEx.getFinishDepositByNumberList(numberList);
        Map<String,BigDecimal> finishDepositMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (FinishDepositVo finishDepositVo : list) {
                if(finishDepositVo!=null) {
                    finishDepositMap.put(finishDepositVo.getNumber(), finishDepositVo.getFinishDeposit());
                }
            }
        }
        return finishDepositMap;
    }

    public Map<String, Integer> getBillSizeMapByLinkNumberList(List<String> numberList) throws Exception {
        List<DepotHead> list = getBillListByLinkNumberList(numberList);
        Map<String, Integer> billListMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (DepotHead depotHead : list) {
                if(depotHead!=null) {
                    billListMap.put(depotHead.getLinkNumber(), list.size());
                }
            }
        }
        return billListMap;
    }

    public Map<Long,Integer> getFinancialBillNoMapByBillIdList(List<Long> idList) {
        List<AccountItem> list = accountHeadService.getFinancialBillNoByBillIdList(idList);
        Map<Long, Integer> billListMap = new HashMap<>();
        if(list!=null && list.size()>0) {
            for (AccountItem accountItem : list) {
                if(accountItem!=null) {
                    billListMap.put(accountItem.getBillId(), list.size());
                }
            }
        }
        return billListMap;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertDepotHead(JSONObject obj, HttpServletRequest request)throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        int result=0;
        try{
            result=depotHeadMapper.insert(depotHead);
            logService.insertLog("单据", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateDepotHead(JSONObject obj, HttpServletRequest request) throws Exception{
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        DepotHead dh=null;
        try{
            dh = depotHeadMapper.selectByPrimaryKey(depotHead.getId());
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        depotHead.setStatus(dh.getStatus());
        depotHead.setCreateTime(dh.getCreateTime());
        int result=0;
        try{
            result = depotHeadMapper.updateByPrimaryKey(depotHead);
            logService.insertLog("单据",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getId()).toString(), request);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteDepotHead(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHead(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteBillByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteBillByIds(String ids)throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<DepotHead> dhList = getDepotHeadListByIds(ids);
        for(DepotHead depotHead: dhList){
            sb.append("[").append(depotHead.getNumber()).append("]");
            //只有未审核的单据才能被删除
            if("0".equals(depotHead.getStatus())) {
                // User userInfo = userService.getCurrentUser();
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                Object userId = redisService.getObjectFromSessionByKey(request,"userId");
                User userInfo=new User();
                userInfo.setId(Long.valueOf(userId.toString()));
                //删除入库单据，先校验序列号是否出库，如果未出库则同时删除序列号，如果已出库则不能删除单据
                if (BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType())) {
                    List<DepotItem> depotItemList = depotItemMapperEx.findDepotItemListBydepotheadId(depotHead.getId(), BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED);
                    if (depotItemList != null && depotItemList.size() > 0) {
                        //单据明细里面存在序列号商品
                        int serialNumberSellCount = depotHeadMapperEx.getSerialNumberBySell(depotHead.getNumber());
                        if (serialNumberSellCount > 0) {
                            //已出库则不能删除单据
                            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_SERIAL_IS_SELL_CODE,
                                    String.format(ExceptionConstants.DEPOT_HEAD_SERIAL_IS_SELL_MSG, depotHead.getNumber()));
                        } else {
                            //删除序列号
                            SerialNumberExample example = new SerialNumberExample();
                            example.createCriteria().andInBillNoEqualTo(depotHead.getNumber());
                            serialNumberService.deleteByExample(example);
                        }
                    }
                }
                //删除出库数据回收序列号
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && !BusinessConstants.SUB_TYPE_TRANSFER.equals(depotHead.getSubType())) {
                    //查询单据子表列表
                    List<DepotItem> depotItemList = depotItemMapperEx.findDepotItemListBydepotheadId(depotHead.getId(), BusinessConstants.ENABLE_SERIAL_NUMBER_ENABLED);
                    /**回收序列号*/
                    if (depotItemList != null && depotItemList.size() > 0) {
                        for (DepotItem depotItem : depotItemList) {
                            //BasicNumber=OperNumber*ratio
                            serialNumberService.cancelSerialNumber(depotItem.getMaterialId(), depotHead.getNumber(), (depotItem.getBasicNumber() == null ? 0 : depotItem.getBasicNumber()).intValue(), userInfo);
                        }
                    }
                }
                //对于零售出库单据，更新会员的预收款信息
                if (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType())
                        && BusinessConstants.SUB_TYPE_RETAIL.equals(depotHead.getSubType())){
                    if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())) {
                        if (depotHead.getOrganId() != null) {
                            supplierService.updateAdvanceIn(depotHead.getOrganId(), depotHead.getTotalPrice().abs());
                        }
                    }
                }
                List<DepotItem> list = depotItemService.getListByHeaderId(depotHead.getId());
                //删除单据子表数据
                depotItemMapperEx.batchDeleteDepotItemByDepotHeadIds(new Long[]{depotHead.getId()});
                //删除单据主表信息
                batchDeleteDepotHeadByIds(depotHead.getId().toString());
                //将关联的单据置为审核状态-针对采购入库、销售出库和盘点复盘
                if(StringUtil.isNotEmpty(depotHead.getLinkNumber())){
                    if((BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_PURCHASE.equals(depotHead.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_SALES.equals(depotHead.getSubType()))
                    || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                        BusinessConstants.SUB_TYPE_REPLAY.equals(depotHead.getSubType()))) {
                        String status = BusinessConstants.BILLS_STATUS_AUDIT;
                        //查询除当前单据之外的关联单据列表
                        List<DepotHead> exceptCurrentList = getListByLinkNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber(), depotHead.getType());
                        if(exceptCurrentList!=null && exceptCurrentList.size()>0) {
                            status = BusinessConstants.BILLS_STATUS_SKIPING;
                        }
                        DepotHead dh = new DepotHead();
                        dh.setStatus(status);
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //将关联的销售订单单据置为未采购状态-针对销售订单转采购订单的情况
                if(StringUtil.isNotEmpty(depotHead.getLinkNumber())){
                    if(BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(depotHead.getSubType())) {
                        DepotHead dh = new DepotHead();
                        //获取分批操作后单据的商品和商品数量（汇总）
                        List<DepotItemVo4MaterialAndSum> batchList = depotItemMapperEx.getBatchBillDetailMaterialSum(depotHead.getLinkNumber(), depotHead.getType());
                        if(batchList.size()>0) {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_SKIPING);
                        } else {
                            dh.setPurchaseStatus(BusinessConstants.PURCHASE_STATUS_UN_AUDIT);
                        }
                        DepotHeadExample example = new DepotHeadExample();
                        example.createCriteria().andNumberEqualTo(depotHead.getLinkNumber());
                        depotHeadMapper.updateByExampleSelective(dh, example);
                    }
                }
                //更新当前库存
                for (DepotItem depotItem : list) {
                    depotItemService.updateCurrentStock(depotItem);
                }
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
        }
        logService.insertLog("单据", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    /**
     * 删除单据主表信息
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHeadByIds(String ids)throws Exception {
        //User userInfo=userService.getCurrentUser();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");

        String [] idArray=ids.split(",");
        int result=0;
        try{
            result = depotHeadMapperEx.batchDeleteDepotHeadByIds(new Date(),userId==null?null:Long.valueOf(userId.toString()),idArray);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHeadListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<DepotHead> list = new ArrayList<>();
        try{
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(idList);
            list = depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 校验单据编号是否存在
     * @param id
     * @param number
     * @return
     * @throws Exception
     */
    public int checkIsBillNumberExist(Long id, String number)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andIdNotEqualTo(id).andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try{
            list = depotHeadMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotHeadIDs)throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        for(Long id: ids) {
            DepotHead depotHead = getDepotHead(id);
            if("0".equals(status)){
                if("1".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_MSG));
                }
            } else if("1".equals(status)){
                if("0".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_MSG));
                }
            }
        }
        if(dhIds.size()>0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            Object systemConfig = redisService.getObjectFromSessionByKey(request,"systemConfig");
            boolean forceFlag = ((Map)systemConfig).get("forceApprovalFlag").toString()=="1"?true:false;
            //更新当前库存
            //if(systemConfigService.getForceApprovalFlag()) {
            if(forceFlag) {
                for(Long dhId: dhIds) {
                    List<DepotItem> list = depotItemService.getListByHeaderId(dhId);
                    for (DepotItem depotItem : list) {
                        depotItemService.updateCurrentStock(depotItem);
                    }
                }
            }
        }
        return result;
    }

    public Map<Long,String> findMaterialsListMapByHeaderIdList(List<Long> idList)throws Exception {
        List<MaterialsListVo> list = depotHeadMapperEx.findMaterialsListMapByHeaderIdList(idList);
        Map<Long,String> materialsListMap = new HashMap<>();
        for(MaterialsListVo materialsListVo : list){
            materialsListMap.put(materialsListVo.getHeaderId(), materialsListVo.getMaterialsList());
        }
        return materialsListMap;
    }

    public Map<Long,BigDecimal> getMaterialCountListMapByHeaderIdList(List<Long> idList)throws Exception {
        List<MaterialCountVo> list = depotHeadMapperEx.getMaterialCountListByHeaderIdList(idList);
        Map<Long,BigDecimal> materialCountListMap = new HashMap<>();
        for(MaterialCountVo materialCountVo : list){
            materialCountListMap.put(materialCountVo.getHeaderId(), materialCountVo.getMaterialCount());
        }
        return materialCountListMap;
    }

    public List<DepotHeadVo4InDetail> findInOutDetail(String beginTime, String endTime, String type, String [] creatorArray,
                                                      String [] organArray, Boolean forceFlag, String materialParam, List<Long> depotList, Integer oId, String number,
                                                      String remark, Integer offset, Integer rows) throws Exception{
        List<DepotHeadVo4InDetail> list = null;
        try{
            list =depotHeadMapperEx.findInOutDetail(beginTime, endTime, type, creatorArray, organArray, forceFlag, materialParam, depotList, oId, number, remark, offset, rows);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutDetailCount(String beginTime, String endTime, String type, String [] creatorArray,
                              String [] organArray, Boolean forceFlag, String materialParam, List<Long> depotList, Integer oId, String number,
                              String remark) throws Exception{
        int result = 0;
        try{
            result =depotHeadMapperEx.findInOutDetailCount(beginTime, endTime, type, creatorArray, organArray, forceFlag, materialParam, depotList, oId, number, remark);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InOutMCount> findInOutMaterialCount(String beginTime, String endTime, String type, Boolean forceFlag, String materialParam,
                              List<Long> depotList, Integer oId, String roleType, Integer offset, Integer rows)throws Exception {
        List<DepotHeadVo4InOutMCount> list = null;
        try{
            String [] creatorArray = getCreatorArray(roleType);
            String subType = "出库".equals(type)? "销售" : "";
            String [] organArray = getOrganArray(subType, "");
            list =depotHeadMapperEx.findInOutMaterialCount(beginTime, endTime, type, forceFlag, materialParam, depotList, oId,
                    creatorArray, organArray, offset, rows);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutMaterialCountTotal(String beginTime, String endTime, String type, Boolean forceFlag, String materialParam,
                               List<Long> depotList, Integer oId, String roleType)throws Exception {
        int result = 0;
        try{
            String [] creatorArray = getCreatorArray(roleType);
            String subType = "出库".equals(type)? "销售" : "";
            String [] organArray = getOrganArray(subType, "");
            result =depotHeadMapperEx.findInOutMaterialCountTotal(beginTime, endTime, type, forceFlag, materialParam, depotList, oId,
                    creatorArray, organArray);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InDetail> findAllocationDetail(String beginTime, String endTime, String subType, String number,
                            String [] creatorArray, Boolean forceFlag, String materialParam, List<Long> depotList, List<Long> depotFList,
                            String remark, Integer offset, Integer rows) throws Exception{
        List<DepotHeadVo4InDetail> list = null;
        try{
            list =depotHeadMapperEx.findAllocationDetail(beginTime, endTime, subType, number, creatorArray, forceFlag,
                    materialParam, depotList, depotFList, remark, offset, rows);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public int findAllocationDetailCount(String beginTime, String endTime, String subType, String number,
                            String [] creatorArray, Boolean forceFlag, String materialParam, List<Long> depotList,  List<Long> depotFList,
                            String remark) throws Exception{
        int result = 0;
        try{
            result =depotHeadMapperEx.findAllocationDetailCount(beginTime, endTime, subType, number, creatorArray, forceFlag,
                    materialParam, depotList, depotFList, remark);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccount(String beginTime, String endTime, Integer organId, String [] organArray,
                                              String supplierType, String type, String subType, String typeBack, String subTypeBack, String billType, Integer offset, Integer rows) {
        List<DepotHeadVo4StatementAccount> list = null;
        try{
            list = depotHeadMapperEx.getStatementAccount(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack, billType, offset, rows);
        } catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public int getStatementAccountCount(String beginTime, String endTime, Integer organId,
                                        String [] organArray, String supplierType, String type, String subType, String typeBack, String subTypeBack, String billType) {
        int result = 0;
        try{
            result = depotHeadMapperEx.getStatementAccountCount(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack, billType);
        } catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccountTotalPay(String beginTime, String endTime, Integer organId,
                                                                          String [] organArray, String supplierType,
                                        String type, String subType, String typeBack, String subTypeBack, String billType) {
        List<DepotHeadVo4StatementAccount> list = null;
        try{
            list = depotHeadMapperEx.getStatementAccountTotalPay(beginTime, endTime, organId, organArray, supplierType, type, subType,typeBack, subTypeBack, billType);
        } catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> getDetailByNumber(String number)throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<DepotHeadVo4List>();
        try{
            Map<Long,String> personMap = personService.getPersonMap();
            Map<Long,String> accountMap = accountService.getAccountMap();
            List<DepotHeadVo4List> list = depotHeadMapperEx.getDetailByNumber(number);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                //通过批量查询去构造map
                Map<Long,Integer> financialBillNoMap = getFinancialBillNoMapByBillIdList(idList);
                Map<String,Integer> billSizeMap = getBillSizeMapByLinkNumberList(numberList);
                Map<Long,String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                DepotHeadVo4List dh = list.get(0);
                if(accountMap!=null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                    String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                    dh.setAccountName(accountStr);
                }
                if(dh.getAccountIdList() != null) {
                    String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                    dh.setAccountIdList(accountidlistStr);
                }
                if(dh.getAccountMoneyList() != null) {
                    String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                    dh.setAccountMoneyList(accountmoneylistStr);
                }
                if(dh.getChangeAmount() != null) {
                    dh.setChangeAmount(dh.getChangeAmount().abs());
                }
                if(dh.getTotalPrice() != null) {
                    dh.setTotalPrice(dh.getTotalPrice().abs());
                }
                //欠款计算
                BigDecimal discountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
                BigDecimal otherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
                BigDecimal deposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
                BigDecimal changeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount():BigDecimal.ZERO;
                dh.setDebt(discountLastMoney.add(otherMoney).subtract((deposit.add(changeAmount))));
                //是否有付款单或收款单
                if(financialBillNoMap!=null) {
                    Integer financialBillNoSize = financialBillNoMap.get(dh.getId());
                    dh.setHasFinancialFlag(financialBillNoSize!=null && financialBillNoSize>0);
                }
                //是否有退款单
                if(billSizeMap!=null) {
                    Integer billListSize = billSizeMap.get(dh.getNumber());
                    dh.setHasBackFlag(billListSize!=null && billListSize>0);
                }
                if(StringUtil.isNotEmpty(dh.getSalesMan())) {
                    dh.setSalesManStr(personService.getPersonByMapAndIds(personMap,dh.getSalesMan()));
                }
                if(dh.getOperTime() != null) {
                    dh.setOperTimeStr(Tools.getCenternTime(dh.getOperTime()));
                }
                //商品信息简述
                if(materialsListMap!=null) {
                    dh.setMaterialsList(materialsListMap.get(dh.getId()));
                }
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                Object username = redisService.getObjectFromSessionByKey(request,"username");
                dh.setCreatorName(username.toString());
                resList.add(dh);
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return resList;
    }

    /**
     * 查询除当前单据之外的关联单据列表
     * @param linkNumber
     * @param number
     * @return
     * @throws Exception
     */
    public List<DepotHead> getListByLinkNumberExceptCurrent(String linkNumber, String number, String type)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andTypeEqualTo(type)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 根据原单号查询关联的单据列表(批量)
     * @param linkNumberList
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberList(List<String> linkNumberList)throws Exception {
        if(linkNumberList!=null && linkNumberList.size()>0) {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andLinkNumberIn(linkNumberList).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            return depotHeadMapper.selectByExample(example);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据原单号查询关联的单据列表
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumber(String linkNumber)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 根据原单号查询关联的单据列表(排除当前的单据编号)
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberExceptNumber(String linkNumber, String number)throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 新增单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addDepotHeadAndDetail(String beanJson, String rows,
                                      HttpServletRequest request) throws Exception {
        /**处理单据主表数据*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        //校验单号是否重复
        if(checkIsBillNumberExist(0L, depotHead.getNumber())>0) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_MSG));
        }
        String subType = depotHead.getSubType();
        //结算账户校验
        if("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
            if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        //判断用户是否已经登录过，登录过不再处理
        //User userInfo=userService.getCurrentUser();
        //HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");

        depotHead.setCreator(userId==null?null:Long.valueOf(userId.toString()));
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if(StringUtil.isEmpty(depotHead.getStatus())) {
            depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        }
        depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        depotHead.setPayType(depotHead.getPayType()==null?"现付":depotHead.getPayType());
        if(StringUtil.isNotEmpty(depotHead.getAccountIdList())){
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if(manyAccountSum.compareTo(depotHead.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //校验累计扣除订金是否超出订单中的金额
        if(depotHead.getDeposit()!=null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //订单中的订金金额
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if(changeAmount!=null) {
                BigDecimal preDeposit = changeAmount.abs();
                if(depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit)>0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        try{
            depotHeadMapper.insertSelective(depotHead);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        /**入库和出库处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())){
            if(depotHead.getOrganId()!=null) {
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if(currentAdvanceIn.compareTo(depotHead.getTotalPrice())>=0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice()));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        //根据单据编号查询单据id
        DepotHeadExample dhExample = new DepotHeadExample();
        dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
        if(list!=null) {
            Long headId = list.get(0).getId();
            /**入库和出库处理单据子表信息*/
            depotItemService.saveDetials(rows,headId, "add",request);
        }
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 更新单据主表及单据子表信息
     * @param beanJson
     * @param rows
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateDepotHeadAndDetail(String beanJson, String rows,HttpServletRequest request)throws Exception {
        /**更新单据主表信息*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        //校验单号是否重复
        if(checkIsBillNumberExist(depotHead.getId(), depotHead.getNumber())>0) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_MSG));
        }
        //校验单据状态，如何不是未审核则提示
        if(!"0".equals(getDepotHead(depotHead.getId()).getStatus())) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_CANNOT_EDIT_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_BILL_CANNOT_EDIT_MSG));
        }
        //获取之前的金额数据
        BigDecimal preTotalPrice = getDepotHead(depotHead.getId()).getTotalPrice().abs();
        String subType = depotHead.getSubType();
        //结算账户校验
        if("采购".equals(subType) || "采购退货".equals(subType) || "销售".equals(subType) || "销售退货".equals(subType)) {
            if (StringUtil.isEmpty(depotHead.getAccountIdList()) && depotHead.getAccountId() == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_ACCOUNT_FAILED_MSG));
            }
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountIdList())){
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if(StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if(manyAccountSum.compareTo(depotHead.getChangeAmount().abs())!=0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        //校验累计扣除订金是否超出订单中的金额
        if(depotHead.getDeposit()!=null && StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
            BigDecimal finishDeposit = depotHeadMapperEx.getFinishDepositByNumberExceptCurrent(depotHead.getLinkNumber(), depotHead.getNumber());
            //订单中的订金金额
            BigDecimal changeAmount = getDepotHead(depotHead.getLinkNumber()).getChangeAmount();
            if(changeAmount!=null) {
                BigDecimal preDeposit = changeAmount.abs();
                if(depotHead.getDeposit().add(finishDeposit).compareTo(preDeposit)>0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_DEPOSIT_OVER_PRE_MSG));
                }
            }
        }
        try{
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        //如果存在多账户结算需要将原账户的id置空
        if(StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
            depotHeadMapperEx.setAccountIdToNull(depotHead.getId());
        }
        /**入库和出库处理预付款信息*/
        if(BusinessConstants.PAY_TYPE_PREPAID.equals(depotHead.getPayType())){
            if(depotHead.getOrganId()!=null){
                BigDecimal currentAdvanceIn = supplierService.getSupplier(depotHead.getOrganId()).getAdvanceIn();
                if(currentAdvanceIn.compareTo(depotHead.getTotalPrice())>=0) {
                    supplierService.updateAdvanceIn(depotHead.getOrganId(), BigDecimal.ZERO.subtract(depotHead.getTotalPrice().subtract(preTotalPrice)));
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MEMBER_PAY_LACK_MSG));
                }
            }
        }
        /**入库和出库处理单据子表信息*/
        depotItemService.saveDetials(rows,depotHead.getId(), "update",request);
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 退货单对应的原单实际欠款（这里面要除去收付款的金额）
     * @param linkNumber 原单单号
     * @param number 当前单号
     * @return
     */
    public BigDecimal getOriginalRealDebt(String linkNumber, String number) throws Exception {
        DepotHead depotHead = getDepotHead(linkNumber);
        BigDecimal discountLastMoney = depotHead.getDiscountLastMoney()!=null?depotHead.getDiscountLastMoney():BigDecimal.ZERO;
        BigDecimal otherMoney = depotHead.getOtherMoney()!=null?depotHead.getOtherMoney():BigDecimal.ZERO;
        BigDecimal deposit = depotHead.getDeposit()!=null?depotHead.getDeposit():BigDecimal.ZERO;
        BigDecimal changeAmount = depotHead.getChangeAmount()!=null?depotHead.getChangeAmount().abs():BigDecimal.ZERO;
        //原单欠款
        BigDecimal debt = discountLastMoney.add(otherMoney).subtract((deposit.add(changeAmount)));
        //完成欠款
        BigDecimal finishDebt = accountItemService.getEachAmountByBillId(depotHead.getId());
        finishDebt = finishDebt!=null?finishDebt:BigDecimal.ZERO;
        //原单对应的退货单欠款(总数)
        List<DepotHead> billList = getBillListByLinkNumberExceptNumber(linkNumber, number);
        BigDecimal allBillDebt = BigDecimal.ZERO;
        for(DepotHead dh: billList) {
            BigDecimal billDiscountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
            BigDecimal billOtherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
            BigDecimal billDeposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
            BigDecimal billChangeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount().abs():BigDecimal.ZERO;
            BigDecimal billDebt = billDiscountLastMoney.add(billOtherMoney).subtract((billDeposit.add(billChangeAmount)));
            allBillDebt = allBillDebt.add(billDebt);
        }
        //原单实际欠款
        return debt.subtract(finishDebt).subtract(allBillDebt);
    }

    public Map<String, Object> getBuyAndSaleStatistics(String today, String monthFirstDay, String yesterdayBegin, String yesterdayEnd,
                                                       String yearBegin, String yearEnd, String roleType, HttpServletRequest request) throws Exception {
        String [] creatorArray = getCreatorArray(roleType);
        Map<String, Object> map = new HashMap<>();
        //今日
        BigDecimal todayBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, today, Tools.getNow3(), creatorArray); //今日采购入库
        BigDecimal todayBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, today, Tools.getNow3(), creatorArray); //今日采购退货
        BigDecimal todaySale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, today, Tools.getNow3(), creatorArray); //今日销售出库
        BigDecimal todaySaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, today, Tools.getNow3(), creatorArray); //今日销售退货
        BigDecimal todayRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                today, Tools.getNow3(), creatorArray); //今日零售出库
        BigDecimal todayRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                today, Tools.getNow3(), creatorArray); //今日零售退货
        //本月
        BigDecimal monthBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, monthFirstDay, Tools.getNow3(), creatorArray); //本月采购入库
        BigDecimal monthBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, monthFirstDay, Tools.getNow3(), creatorArray); //本月采购退货
        BigDecimal monthSale = getBuyAndSaleBasicStatistics("出库", "销售",
                1,monthFirstDay, Tools.getNow3(), creatorArray); //本月销售出库
        BigDecimal monthSaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1,monthFirstDay, Tools.getNow3(), creatorArray); //本月销售退货
        BigDecimal monthRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                monthFirstDay, Tools.getNow3(), creatorArray); //本月零售出库
        BigDecimal monthRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                monthFirstDay, Tools.getNow3(), creatorArray); //本月零售退货
        //昨日
        BigDecimal yesterdayBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购入库
        BigDecimal yesterdayBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购退货
        BigDecimal yesterdaySale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售出库
        BigDecimal yesterdaySaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售退货
        BigDecimal yesterdayRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售出库
        BigDecimal yesterdayRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售退货
        //今年
        BigDecimal yearBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, yearBegin, yearEnd, creatorArray); //今年采购入库
        BigDecimal yearBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, yearBegin, yearEnd, creatorArray); //今年采购退货
        BigDecimal yearSale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, yearBegin, yearEnd, creatorArray); //今年销售出库
        BigDecimal yearSaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, yearBegin, yearEnd, creatorArray); //今年销售退货
        BigDecimal yearRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                yearBegin, yearEnd, creatorArray); //今年零售出库
        BigDecimal yearRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                yearBegin, yearEnd, creatorArray); //今年零售退货
        map.put("todayBuy", depotService.parsePriceByLimit(todayBuy.subtract(todayBuyBack), "buy", "***", request));
        map.put("todaySale", depotService.parsePriceByLimit(todaySale.subtract(todaySaleBack), "sale", "***", request));
        map.put("todayRetailSale", depotService.parsePriceByLimit(todayRetailSale.subtract(todayRetailSaleBack), "retail", "***", request));
        map.put("monthBuy", depotService.parsePriceByLimit(monthBuy.subtract(monthBuyBack), "buy", "***", request));
        map.put("monthSale", depotService.parsePriceByLimit(monthSale.subtract(monthSaleBack), "sale", "***", request));
        map.put("monthRetailSale", depotService.parsePriceByLimit(monthRetailSale.subtract(monthRetailSaleBack), "retail", "***", request));
        map.put("yesterdayBuy", depotService.parsePriceByLimit(yesterdayBuy.subtract(yesterdayBuyBack), "buy", "***", request));
        map.put("yesterdaySale", depotService.parsePriceByLimit(yesterdaySale.subtract(yesterdaySaleBack), "sale", "***", request));
        map.put("yesterdayRetailSale", depotService.parsePriceByLimit(yesterdayRetailSale.subtract(yesterdayRetailSaleBack), "retail", "***", request));
        map.put("yearBuy", depotService.parsePriceByLimit(yearBuy.subtract(yearBuyBack), "buy", "***", request));
        map.put("yearSale", depotService.parsePriceByLimit(yearSale.subtract(yearSaleBack), "sale", "***", request));
        map.put("yearRetailSale", depotService.parsePriceByLimit(yearRetailSale.subtract(yearRetailSaleBack), "retail", "***", request));
        return map;
    }


    public BigDecimal getBuyAndSaleBasicStatistics(String type, String subType, Integer hasSupplier,
                                                   String beginTime, String endTime, String[] creatorArray) throws Exception {
        //Boolean forceFlag = systemConfigService.getForceApprovalFlag();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object systemConfig = redisService.getObjectFromSessionByKey(request,"systemConfig");
        boolean forceFlag = ((Map)systemConfig).get("forceApprovalFlag").toString()=="1"?true:false;

        return depotHeadMapperEx.getBuyAndSaleBasicStatistics(type, subType, hasSupplier, beginTime, endTime, creatorArray, forceFlag);
    }

    public BigDecimal getBuyAndSaleRetailStatistics(String type, String subType,
                                                    String beginTime, String endTime, String[] creatorArray) throws Exception {
        //Boolean forceFlag = systemConfigService.getForceApprovalFlag();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object systemConfig = redisService.getObjectFromSessionByKey(request,"systemConfig");
        boolean forceFlag = ((Map)systemConfig).get("forceApprovalFlag").toString()=="1"?true:false;
        return depotHeadMapperEx.getBuyAndSaleRetailStatistics(type, subType, beginTime, endTime, creatorArray, forceFlag).abs();
    }

    public DepotHead getDepotHead(String number)throws Exception {
        DepotHead depotHead = new DepotHead();
        try{
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<DepotHead> list = depotHeadMapper.selectByExample(example);
            if(null!=list && list.size()>0) {
                depotHead = list.get(0);
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return depotHead;
    }

    public List<DepotHeadVo4List> debtList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                              String roleType, String status, Integer offset, Integer rows) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try{
            String depotIds = depotService.findDepotStrByCurrentUser();
            String [] depotArray=depotIds.split(",");
            String [] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list=depotHeadMapperEx.debtList(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                }
                //通过批量查询去构造map
                Map<Long,String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if(dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if(dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if(dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    if(dh.getOperTime() != null) {
                        dh.setOperTimeStr(Tools.getCenternTime(dh.getOperTime()));
                    }
                    BigDecimal discountLastMoney = dh.getDiscountLastMoney()!=null?dh.getDiscountLastMoney():BigDecimal.ZERO;
                    BigDecimal otherMoney = dh.getOtherMoney()!=null?dh.getOtherMoney():BigDecimal.ZERO;
                    BigDecimal deposit = dh.getDeposit()!=null?dh.getDeposit():BigDecimal.ZERO;
                    BigDecimal changeAmount = dh.getChangeAmount()!=null?dh.getChangeAmount().abs():BigDecimal.ZERO;
                    //本单欠款(如果退货则为负数)
                    dh.setNeedDebt(discountLastMoney.add(otherMoney).subtract(deposit.add(changeAmount)));
                    if(BusinessConstants.SUB_TYPE_PURCHASE_RETURN.equals(dh.getSubType()) || BusinessConstants.SUB_TYPE_SALES_RETURN.equals(dh.getSubType())) {
                        dh.setNeedDebt(BigDecimal.ZERO.subtract(dh.getNeedDebt()));
                    }
                    BigDecimal needDebt = dh.getNeedDebt()!=null?dh.getNeedDebt():BigDecimal.ZERO;
                    BigDecimal finishDebt = accountItemService.getEachAmountByBillId(dh.getId());
                    finishDebt = finishDebt!=null?finishDebt:BigDecimal.ZERO;
                    //已收欠款
                    dh.setFinishDebt(finishDebt);
                    //待收欠款
                    dh.setDebt(needDebt.subtract(finishDebt));
                    //商品信息简述
                    if(materialsListMap!=null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    resList.add(dh);
                }
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return resList;
    }

    public int debtListCount(Long organId, String materialParam, String number, String beginTime, String endTime,
                                           String roleType, String status) {
        int total = 0;
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            total = depotHeadMapperEx.debtListCount(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray);
        } catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return total;

    }
}
