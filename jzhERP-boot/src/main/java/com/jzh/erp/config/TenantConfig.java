package com.jzh.erp.config;

/*import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.core.parser.ISqlParserFilter;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.jzh.erp.utils.Tools;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.mapper.MapperScannerConfigurer;*/
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

//@Service
public class TenantConfig {} /*{

    @Bean
    public PaginationInterceptor paginationInterceptor(HttpServletRequest request) {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();
        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        tenantSqlParser.setTenantHandler(new TenantHandler() {
            @Override
            public Expression getTenantId() {
                String token = request.getHeader("X-Access-Token");
                Long tenantId = Tools.getTenantIdByToken(token);
                if (tenantId!=0L) {
                    return new LongValue(tenantId);
                } else {
                    //超管
                    return null;
                }
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean doTableFilter(String tableName) {
                //获取开启状态
                Boolean res = true;
                String token = request.getHeader("X-Access-Token");
                Long tenantId = Tools.getTenantIdByToken(token);
                if (tenantId!=0L) {
                    // 这里可以判断是否过滤表
                    if ("jzh_material_property".equals(tableName) || "jzh_sequence".equals(tableName)
                            || "jzh_user_business".equals(tableName) || "jzh_function".equals(tableName)
                            || "jzh_platform_config".equals(tableName) || "jzh_tenant".equals(tableName)) {
                        res = true;
                    } else {
                        res = false;
                    }
                }
                return res;
            }
        });

        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        paginationInterceptor.setSqlParserFilter(new ISqlParserFilter() {
            @Override
            public boolean doFilter(MetaObject metaObject) {
                MappedStatement ms = SqlParserHelper.getMappedStatement(metaObject);
                // 过滤自定义查询此时无租户信息约束出现
                if ("com.jzh.erp.datasource.mappers.UserMapperEx.getUserListByUserNameOrLoginName".equals(ms.getId())) {
                    return true;
                } else if ("com.jzh.erp.datasource.mappers.UserMapperEx.disableUserByLimit".equals(ms.getId())) {
                    return true;
                } else if ("com.jzh.erp.datasource.mappers.RoleMapperEx.getRoleWithoutTenant".equals(ms.getId())) {
                    return true;
                } else if ("com.jzh.erp.datasource.mappers.LogMapperEx.insertLogWithUserId".equals(ms.getId())) {
                    return true;
                }
                return false;
            }
        });
        return paginationInterceptor;
    }

    *//**
     * 相当于顶部的：
     * {@code @MapperScan("com.jzh.erp.datasource.mappers*")}
     * 这里可以扩展，比如使用配置文件来配置扫描Mapper的路径
     *//*
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage("com.jzh.erp.datasource.mappers*");
        return scannerConfigurer;
    }

    *//**
     * 性能分析拦截器，不建议生产使用
     *//*
//    @Bean
//    public PerformanceInterceptor performanceInterceptor(){
//        return new PerformanceInterceptor();
//    }


}
*/