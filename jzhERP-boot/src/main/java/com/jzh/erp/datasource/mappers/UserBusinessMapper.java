package com.jzh.erp.datasource.mappers;

import com.jzh.erp.datasource.entities.UserBusiness;
import com.jzh.erp.datasource.entities.UserBusinessExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserBusinessMapper {
    long countByExample(UserBusinessExample example);

    int deleteByExample(UserBusinessExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UserBusiness record);

    int insertSelective(UserBusiness record);

    List<UserBusiness> selectByExample(UserBusinessExample example);

    UserBusiness selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UserBusiness record, @Param("example") UserBusinessExample example);

    int updateByExample(@Param("record") UserBusiness record, @Param("example") UserBusinessExample example);

    int updateByPrimaryKeySelective(UserBusiness record);

    int updateByPrimaryKey(UserBusiness record);
}