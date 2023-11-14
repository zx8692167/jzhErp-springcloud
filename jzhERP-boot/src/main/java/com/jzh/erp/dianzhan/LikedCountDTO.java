package com.jzh.erp.dianzhan;

public class LikedCountDTO {
    String key;
    String count;

    public LikedCountDTO(String key, String count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
