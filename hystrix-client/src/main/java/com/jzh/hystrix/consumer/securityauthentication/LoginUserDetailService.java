package com.jzh.hystrix.consumer.securityauthentication;

import com.jzh.erp.constants.BusinessConstants;
import com.jzh.erp.datasource.entities.Role;
import com.jzh.erp.datasource.entities.RoleExample;
import com.jzh.erp.datasource.entities.User;
import com.jzh.erp.datasource.entities.UserExample;
import com.jzh.erp.datasource.mappers.RoleMapper;
import com.jzh.erp.datasource.mappers.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Component
// "登录数据源"
public class LoginUserDetailService implements UserDetailsService {

    // 数据库查询User信息
    @Resource
    private UserMapper userMapper;
    @Resource
    private  RoleMapper roleMapper;

    @Autowired
    public LoginUserDetailService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        //LambdaQueryWrapper<User> queryWrapper_User = new LambdaQueryWrapper<>();
        //queryWrapper_User.eq(User::getUsername,username);
        //User user = userMapper.selectOne(queryWrapper_User);
        UserExample example = new UserExample();
        example.createCriteria().andLoginNameEqualTo(username);
        List<User> list = userMapper.selectByExample(example);
        User user=null;
        if(list!=null&&list.get(0)!=null) {
            user=list.get(0);
        }
        // 工具类判断是否查询user对象是否有效
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //LambdaQueryWrapper<Role> queryWrapper_Role = new LambdaQueryWrapper<>();
        //queryWrapper_Role.eq(Role::getUid,user.getId());
        //List<Role> roles = roleMapper.selectList(queryWrapper_Role);
        RoleExample example2 = new RoleExample();
        example2.setOrderByClause("Id");
        //DelegatingPasswordEncoder
        example2.createCriteria().andEnabledEqualTo(true).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Role> list2=null;
        try{
            list2=roleMapper.selectByExample(example2);
        }catch(Exception e){
            throw new UsernameNotFoundException(e.getMessage(),e.getCause());
        }
        user.setRoles(list2);
        // User类继承了UserDetails,返回时会进行向上转型，所以我们将正常查询到的user数据装进User对象返回即可
        //System.out.println(user.getLoginName()+" 查詢到的用戶名密碼 "+user.getPassword() +" "+Tools.md5Encryp("123456"));
            //PasswordEncoder  passwordEncoder=new BCryptPasswordEncoder();
            //PasswordEncoder  passwordEncoder=new PlainTextPasswordEncoder();
            //Tools.md5Encryp()
        //PasswordEncoderFactories
        UserDetails       userDetail = org.springframework.security.core.userdetails.User.withUsername(user.getLoginName())    //
                   // .password(user.getPassword()) passwordEncoder.encode("123456") new PlainTextPasswordEncoder().encode("123456")
                    .password(user.getPassword())//密码必须加密
                    .roles("admin")
                    .authorities("file_read")
                    .build();        /*try {

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }*/
        /*UserDetails zhangsan = User.withUsername("zhangsan")
                .password(passwordEncoder.encode("123456"))
                .roles("admin","hr")
                .authorities("file_write")
                .build();*/
        //模拟内存中保存所有用户信息
        //InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(userDetail);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("passwordMD5",user.getPassword());
        return userDetail;
    }
}
