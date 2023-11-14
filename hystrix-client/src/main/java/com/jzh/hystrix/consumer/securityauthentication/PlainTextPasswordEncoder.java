package com.jzh.hystrix.consumer.securityauthentication;

import com.jzh.erp.utils.Tools;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.NoSuchAlgorithmException;


public class PlainTextPasswordEncoder extends BCryptPasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        //并没有变更密码，而是原样返回
        String password=null;
        try {
            password= Tools.md5Encryp(rawPassword.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return password;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        //检查两个字符是否相等
        return encode(rawPassword).equals(encodedPassword);
    }
}
