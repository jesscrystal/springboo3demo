package com.example.controller;

import com.example.entity.Restbean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    private final String USERNAME_REGEXP = "^(?!_)(?!.*?_$)[a-zA-Z0-9_一-龥]+$";
    private final String EMAIL_REGEXP = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public Restbean<String> validateEmail(@Pattern(regexp = EMAIL_REGEXP) @RequestParam("email") String email,
                                          HttpSession session) {
        String s = service.sendValidateEmail(email,session.getId());
        if (s==null)
            return Restbean.success("邮件已发送请注意查收");
        else
            return Restbean.failure(400, s);
    }
    @PostMapping("/register")
    public Restbean<String> registerUser(@Pattern(regexp = USERNAME_REGEXP)
                                         @Length(min = 6 ,max = 12) @RequestParam("username") String username,
                                         @Length(min = 6,max = 18) @RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEXP)
                                             @RequestParam("email") String email,
                                         @Length(min = 6,max = 6) @RequestParam("code") String code,
                                         HttpSession session){
        String s = service.validateAndRegister(username, password, email, code, session.getId());
        if (s==null)
            return Restbean.success("注册成功");
        else
            return Restbean.failure(400,s);

    }
}
