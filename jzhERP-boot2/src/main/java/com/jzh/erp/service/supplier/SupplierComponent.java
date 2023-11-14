package com.jzh.erp.service.supplier;

import com.alibaba.fastjson.JSONObject;
import com.jzh.erp.service.ICommonQuery;
import com.jzh.erp.utils.Constants;
import com.jzh.erp.utils.QueryUtils;
import com.jzh.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "supplier_component")
@SupplierResource
public class SupplierComponent implements ICommonQuery {

    @Resource
    private SupplierService supplierService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return supplierService.getSupplier(id);
    }

    @Override
    public List<?> select(Map<String, String> map)throws Exception {
        return getSupplierList(map);
    }

    private List<?> getSupplierList(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String supplier = StringUtil.getInfo(search, "supplier");
        String type = StringUtil.getInfo(search, "type");
        String phonenum = StringUtil.getInfo(search, "phonenum");
        String telephone = StringUtil.getInfo(search, "telephone");
        String order = QueryUtils.order(map);
        return supplierService.select(supplier, type, phonenum, telephone, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map)throws Exception {
        String search = map.get(Constants.SEARCH);
        String supplier = StringUtil.getInfo(search, "supplier");
        String type = StringUtil.getInfo(search, "type");
        String phonenum = StringUtil.getInfo(search, "phonenum");
        String telephone = StringUtil.getInfo(search, "telephone");
        return supplierService.countSupplier(supplier, type, phonenum, telephone);
    }

    @Override
    public long insert(JSONObject obj, HttpServletRequest request)throws Exception {
        return supplierService.insertSupplier(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request)throws Exception {
        return supplierService.updateSupplier(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request)throws Exception {
        return supplierService.deleteSupplier(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request)throws Exception {
        return supplierService.batchDeleteSupplier(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name)throws Exception {
        return supplierService.checkIsNameExist(id, name);
    }

}
