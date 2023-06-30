package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Value("${spring.mail.username}")
    String from;
    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate template;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username==null)
            throw new UsernameNotFoundException("用户名不能为空");
        Account account = mapper.findAccountByNameOrEmail(username);
        if (account==null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

        /**
         * 1.生成对应的验证码
         * 2.把邮箱和对应的验证码直接放到Redis里面（过期时间3分钟，如果此时重新要求发送邮件
         * ，那么，只要时间低于2分钟就可以发送一次，重复此流程）
         * 3.发送验证码到指定邮箱
         * 4.如果发送失败，把Redis里面刚刚插入的删除
         * 5.用户在注册时，再从Redis里面取出对应的键值对，然后看验证码是否一致
         */

    @Override
    public String sendValidateEmail(String email, String sessionId,boolean hasAccount) {
        String key = "email" + sessionId + ":" + email + ':' + hasAccount;
        if (Boolean.TRUE.equals(template.hasKey(key))){
            Long expire = Optional.ofNullable(template.getExpire(key,TimeUnit.SECONDS)).orElse(0L);
            if (expire>120)
                return "请求过于频繁，请稍后再试！";
        }
        Account account = mapper.findAccountByNameOrEmail(email);
        if (hasAccount && account == null) return "没有此邮件地址的账户";
        if (!hasAccount && account != null) return "此邮箱已被其他用户注册";
        /**
         * 上面这段逻辑我不是很清楚是什么鬼，感觉好像有重复的
         * 他用一个布尔来控制然后这个布尔何时为true何时为false我没看懂
         * 按道理说只需要一个是否查询得到邮箱地址来判断是否存在有该用户即可
         * 这个逻辑不是很清楚，先记下来这段笔记后面我看看有没有可以改进的地方
         * 下面是初始项目编写时的代码
         */
        /*if (mapper.findAccountByNameOrEmail(email) != null){
            return "此邮箱已被其他用户注册！";
        }*/
        Random random = new Random();
        int code = random.nextInt(899999)+10000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("智能云学习平台验证邮件");
        message.setText("验证码是: "+code);
        try {
            mailSender.send(message);
            template.opsForValue().set(key,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
            e.printStackTrace();
            return "邮件发送失败，请检查邮件地址是否正确";
        }
    }


    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email" + sessionId + ":" + email +":false";
        if (Boolean.TRUE.equals(template.hasKey(key))){
            String s = template.opsForValue().get(key);
            if (s==null) return "验证码失效，请重新请求";
            if (s.equals(code)){
                Account account = mapper.findAccountByNameOrEmail(username);
                if(account != null) return "此用户名已被注册，请更换用户名";
                template.delete(key);
                password = encoder.encode(password);
                if (mapper.createAccount(username,password,email)>0){
                    return null;
                }else {
                    return "内部错误请联系管理员";
                }
            }else {
                return "验证码错误，请重新输入";
            }
        } else {
            return "请先请求一封验证码邮件";
        }
    }

    /**
     * 这里和上面有一个相同的东西就是在email后增加了一个布尔值
     * 为的是防止在注册页面发送了验证码邮箱后去重置页面输入密码
     * 出现bug
     */
    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email" + sessionId + ":" + email+":true";
        if (Boolean.TRUE.equals(template.hasKey(key))){
            String s = template.opsForValue().get(key);
            if (s==null) return "验证码失效，请重新请求";
            if (s.equals(code)){
                template.delete(key);
                return null;
            }else {
                return "验证码错误，请重新输入";
            }
        } else {
            return "请先请求一封验证码邮件";
        }
    }

    @Override
    public boolean resetPassword(String password, String email) {
        password = encoder.encode(password);
        return mapper.resetPasswordByEmail(password, email) > 0;
    }
}
