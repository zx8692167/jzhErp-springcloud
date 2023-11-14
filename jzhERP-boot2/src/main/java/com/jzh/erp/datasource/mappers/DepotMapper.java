package com.jzh.erp.datasource.mappers;

import com.jzh.erp.datasource.entities.Depot;
import com.jzh.erp.datasource.entities.DepotExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepotMapper {
    long countByExample(DepotExample example);

    int deleteByExample(DepotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Depot record);

    int insertSelective(Depot record);

    List<Depot> selectByExample(DepotExample example);

    Depot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Depot record, @Param("example") DepotExample example);

    int updateByExample(@Param("record") Depot record, @Param("example") DepotExample example);

    int updateByPrimaryKeySelective(Depot record);

    int updateByPrimaryKey(Depot record);
}