package com.jzh.erp.service.systemConfig;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.datasource.entities.SystemConfig;
import com.jzh.erp.datasource.entities.SystemConfigExample;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.datasource.mappers.SystemConfigMapper;
import com.jzh.erp.datasource.mappers.SystemConfigMapperEx;
import com.jzh.erp.exception.JzhException;
import com.jzh.erp.service.log.LogService;
import com.jzh.erp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class SystemConfigService {
    private Logger logger = LoggerFactory.getLogger(SystemConfigService.class);

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Resource
    private SystemConfigMapperEx systemConfigMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    private static final String TEST_USER = "jzh";

    public SystemConfig getSystemConfig(long id)throws Exception {
        SystemConfig result=null;
        try{
            result=systemConfigMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<SystemConfig> getSystemConfig()throws Exception {
        SystemConfigExample example = new SystemConfigExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<SystemConfig> list=null;
        try{
            list=systemConfigMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }
    public List<SystemConfig> select(String companyName, int offset, int rows)throws Exception {
        List<SystemConfig> list=null;
        try{
            list=systemConfigMapperEx.selectByConditionSystemConfig(companyName, offset, rows);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public Long countSystemConfig(String companyName)throws Exception {
        Long result=null;
        try{
            result=systemConfigMapperEx.countsBySystemConfig(companyName);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertSystemConfig(JSONObject obj, HttpServletRequest request) throws Exception{
        SystemConfig systemConfig = JSONObject.parseObject(obj.toJSONString(), SystemConfig.class);
        int result=0;
        try{
            result=systemConfigMapper.insertSelective(systemConfig);
            logService.insertLog("系统配置",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(systemConfig.getCompanyName()).toString(), request);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateSystemConfig(JSONObject obj, HttpServletRequest request) throws Exception{
        SystemConfig systemConfig = JSONObject.parseObject(obj.toJSONString(), SystemConfig.class);
        int result=0;
        try{
            result = systemConfigMapper.updateByPrimaryKeySelective(systemConfig);
            logService.insertLog("系统配置",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(systemConfig.getCompanyName()).toString(), request);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteSystemConfig(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteSystemConfigByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteSystemConfig(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteSystemConfigByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteSystemConfigByIds(String ids)throws Exception {
        logService.insertLog("系统配置",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_DELETE).append(ids).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        User userInfo=userService.getCurrentUser();
        String [] idArray=ids.split(",");
        int result=0;
        try{
            result = systemConfigMapperEx.batchDeleteSystemConfigByIds(new Date(), userInfo == null ? null : userInfo.getId(), idArray);
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name) throws Exception{
        SystemConfigExample example = new SystemConfigExample();
        example.createCriteria().andIdNotEqualTo(id).andCompanyNameEqualTo(name).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<SystemConfig> list =null;
        try{
            list=systemConfigMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    /**
     * 获取仓库开关
     * @return
     * @throws Exception
     */
    public boolean getDepotFlag() throws Exception {
        boolean depotFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getDepotFlag();
            if(("1").equals(flag)) {
                depotFlag = true;
            }
        }
        return depotFlag;
    }

    /**
     * 获取客户开关
     * @return
     * @throws Exception
     */
    public boolean getCustomerFlag() throws Exception {
        boolean customerFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getCustomerFlag();
            if(("1").equals(flag)) {
                customerFlag = true;
            }
        }
        return customerFlag;
    }

    /**
     * 获取负库存开关
     * @return
     * @throws Exception
     */
    public boolean getMinusStockFlag() throws Exception {
        boolean minusStockFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getMinusStockFlag();
            if(("1").equals(flag)) {
                minusStockFlag = true;
            }
        }
        return minusStockFlag;
    }

    /**
     * 获取更新单价开关
     * @return
     * @throws Exception
     */
    public boolean getUpdateUnitPriceFlag() throws Exception {
        boolean updateUnitPriceFlag = true;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getUpdateUnitPriceFlag();
            if(("0").equals(flag)) {
                updateUnitPriceFlag = false;
            }
        }
        return updateUnitPriceFlag;
    }

    /**
     * 获取超出关联单据开关
     * @return
     * @throws Exception
     */
    public boolean getOverLinkBillFlag() throws Exception {
        boolean overLinkBillFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getOverLinkBillFlag();
            if(("1").equals(flag)) {
                overLinkBillFlag = true;
            }
        }
        return overLinkBillFlag;
    }

    /**
     * 获取强审核开关
     * @return
     * @throws Exception
     */
    public boolean getForceApprovalFlag() throws Exception {
        boolean forceApprovalFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getForceApprovalFlag();
            if(("1").equals(flag)) {
                forceApprovalFlag = true;
            }
        }
        return forceApprovalFlag;
    }

    /**
     * 获取多级审核开关
     * @return
     * @throws Exception
     */
    public boolean getMultiLevelApprovalFlag() throws Exception {
        boolean multiLevelApprovalFlag = false;
        List<SystemConfig> list = getSystemConfig();
        if(list.size()>0) {
            String flag = list.get(0).getMultiLevelApprovalFlag();
            if(("1").equals(flag)) {
                multiLevelApprovalFlag = true;
            }
        }
        return multiLevelApprovalFlag;
    }
}
