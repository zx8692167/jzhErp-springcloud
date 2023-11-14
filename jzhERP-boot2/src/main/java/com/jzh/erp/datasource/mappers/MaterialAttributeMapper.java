package com.jzh.erp.datasource.mappers;

import com.jzh.erp.datasource.entities.MaterialAttribute;
import com.jzh.erp.datasource.entities.MaterialAttributeExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialAttributeMapper {
    long countByExample(MaterialAttributeExample example);

    int deleteByExample(MaterialAttributeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MaterialAttribute record);

    int insertSelective(MaterialAttribute record);

    List<MaterialAttribute> selectByExample(MaterialAttributeExample example);

    MaterialAttribute selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MaterialAttribute record, @Param("example") MaterialAttributeExample example);

    int updateByExample(@Param("record") MaterialAttribute record, @Param("example") MaterialAttributeExample example);

    int updateByPrimaryKeySelective(MaterialAttribute record);

    int updateByPrimaryKey(MaterialAttribute record);
}