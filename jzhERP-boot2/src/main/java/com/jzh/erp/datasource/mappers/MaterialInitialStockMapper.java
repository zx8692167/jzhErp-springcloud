package com.jzh.erp.datasource.mappers;

import com.jzh.erp.datasource.entities.MaterialInitialStock;
import com.jzh.erp.datasource.entities.MaterialInitialStockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MaterialInitialStockMapper {
    long countByExample(MaterialInitialStockExample example);

    int deleteByExample(MaterialInitialStockExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MaterialInitialStock record);

    int insertSelective(MaterialInitialStock record);

    List<MaterialInitialStock> selectByExample(MaterialInitialStockExample example);

    MaterialInitialStock selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MaterialInitialStock record, @Param("example") MaterialInitialStockExample example);

    int updateByExample(@Param("record") MaterialInitialStock record, @Param("example") MaterialInitialStockExample example);

    int updateByPrimaryKeySelective(MaterialInitialStock record);

    int updateByPrimaryKey(MaterialInitialStock record);
}