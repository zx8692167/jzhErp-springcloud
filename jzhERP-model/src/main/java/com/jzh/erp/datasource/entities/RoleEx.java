package com.jzh.erp.datasource.entities;

public class RoleEx extends Role{

    private String priceLimitStr;

    public String getPriceLimitStr() {
        return priceLimitStr;
    }

    public void setPriceLimitStr(String priceLimitStr) {
        this.priceLimitStr = priceLimitStr;
    }
}