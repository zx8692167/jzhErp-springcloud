package com.jzh.erp.dianzhan;

/**
 * 用户点赞的状态
 */
public enum LikedStatusEnum {
    /**
     * 点赞
     */
    LIKE("1", "点赞"),
    /**
     * 取消赞
     */
    UNLIKE("0", "取消点赞/未点赞");

    //private Integer code;
    private String code;

    private String msg;

    LikedStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }
}

