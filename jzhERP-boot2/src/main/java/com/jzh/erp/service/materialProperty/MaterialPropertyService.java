package com.jzh.erp.service.materialProperty;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.datasource.entities.MaterialProperty;
import com.jzh.erp.datasource.entities.MaterialPropertyExample;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.datasource.mappers.MaterialPropertyMapper;
import com.jzh.erp.datasource.mappers.MaterialPropertyMapperEx;
import com.jzh.erp.exception.JzhException;
import com.jzh.erp.service.log.LogService;
import com.jzh.erp.service.redis.RedisService;
//import com.jzh.erp.service.user.UserService;
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
import java.util.Objects;

@Service
public class MaterialPropertyService {
    private Logger logger = LoggerFactory.getLogger(MaterialPropertyService.class);

    @Resource
    private MaterialPropertyMapper materialPropertyMapper;

    @Resource
    private MaterialPropertyMapperEx materialPropertyMapperEx;
    /*@Resource
    private UserService userService;*/
    @Resource
    private LogService logService;

    @Resource
    private RedisService redisService;

    public MaterialProperty getMaterialProperty(long id)throws Exception {
        MaterialProperty result=null;
        try{
            result=materialPropertyMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialProperty> getMaterialProperty()throws Exception {
        MaterialPropertyExample example = new MaterialPropertyExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialProperty>  list=null;
        try{
            list=materialPropertyMapper.selectByExample(example);
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialProperty> select(String name, int offset, int rows)throws Exception {
        List<MaterialProperty>  list=null;
        try{
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            Object loginName = redisService.getObjectFromSessionByKey(request,"loginName");

            if(BusinessConstants.DEFAULT_MANAGER.equals(loginName.toString())) {
                list = materialPropertyMapperEx.selectByConditionMaterialProperty(name, offset, rows);
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialProperty(String name)throws Exception {
        Long  result=null;
        try{
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            Object loginName = redisService.getObjectFromSessionByKey(request,"loginName");
            if(BusinessConstants.DEFAULT_MANAGER.equals(loginName.toString())) {
                result = materialPropertyMapperEx.countsByMaterialProperty(name);
            }
        }catch(Exception e){
            JzhException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterialProperty(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialProperty materialProperty = JSONObject.parseObject(obj.toJSONString(), MaterialProperty.class);
        int  result=0;
        try{
            Object loginName = redisService.getObjectFromSessionByKey(request,"loginName");
            if(BusinessConstants.DEFAULT_MANAGER.equals(loginName.toString())) {
                result = materialPropertyMapper.insertSelective(materialProperty);
                logService.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(materialProperty.getNativeName()).toString(), request);
            }
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateMaterialProperty(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialProperty materialProperty = JSONObject.parseObject(obj.toJSONString(), MaterialProperty.class);
        int  result=0;
        try{
            Object loginName = redisService.getObjectFromSessionByKey(request,"loginName");

            if(BusinessConstants.DEFAULT_MANAGER.equals(loginName.toString())) {
                result = materialPropertyMapper.updateByPrimaryKeySelective(materialProperty);
                logService.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(materialProperty.getNativeName()).toString(), request);
            }
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteMaterialProperty(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialPropertyByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialProperty(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialPropertyByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialPropertyByIds(String ids) throws Exception{
        //User userInfo=userService.getCurrentUser();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object userId = redisService.getObjectFromSessionByKey(request,"userId");
        Object loginName = redisService.getObjectFromSessionByKey(request,"loginName");
        String [] idArray=ids.split(",");
        int  result=0;
        try{
            if(BusinessConstants.DEFAULT_MANAGER.equals(loginName.toString())) {
                result = materialPropertyMapperEx.batchDeleteMaterialPropertyByIds(new Date(), userId == null ? null : Long.valueOf(userId.toString()), idArray);
                logService.insertLog("商品属性",
                        new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_DELETE).append(ids).toString(),
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            }
        }catch(Exception e){
            JzhException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        return 0;
    }
}
