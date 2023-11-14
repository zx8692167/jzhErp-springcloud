package com.jzh.erp.config;

import com.jzh.erp.securityauthentication.PlainTextPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class SecurityConfiguration{


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 关闭安全验证 csrf().disable();.

        //请求授权
        httpSecurity.authorizeHttpRequests(registry->{
            //,"/doc.html","/swagger-ui/index.html","/user/login"  /jzhERP-boot
            registry.requestMatchers("/","/api-docs","/api-docs/*","/doc.html","/swagger-*/*","/login","/user/login").permitAll()//首页都能访问
                    .anyRequest().anonymous()/*authenticated()*/;//除首页外都需要认证
                    //anonymous
        })/*.csrf(withDefaults()).cors(withDefaults())*/;
        //表单登录:使用springSecurity默认表单登录 .loginProcessingUrl("/user/login")
        httpSecurity.formLogin().defaultSuccessUrl("/doc.html", true);
        //使用自己的登录
        /*httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
            httpSecurityFormLoginConfigurer.loginPage("/login").permitAll();//自定义登录页位置
        });*/
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }
    //查询用户详情
    /*@Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
        UserDetails laowang = User.withUsername("laowang")
                .password(passwordEncoder.encode("123456"))//密码必须加密
                .roles("admin")
                .authorities("file_read")
                .build();
        UserDetails zhangsan = User.withUsername("zhangsan")
                .password(passwordEncoder.encode("123456"))
                .roles("admin","hr")
                .authorities("file_write")
                .build();
        //模拟内存中保存所有用户信息
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(laowang,zhangsan);
        return manager;
    }*/
    //密码加密器
    @Bean
    PasswordEncoder passwordEncoder(){
        //return new BCryptPasswordEncoder();
         return new PlainTextPasswordEncoder();
    }


    /**
     * 配置全局的某些通用事物，例如静态资源等
     * @return
     */
    @Bean
    public WebSecurityCustomizer securityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**");//"/static/**"
    }
}
