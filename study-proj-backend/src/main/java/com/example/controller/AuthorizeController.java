package com.example.controller;

import com.example.entity.Restbean;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AuthorizeService service;

    @PostMapping("/valid-email")
    public Restbean<String> validateEmail(@Pattern(regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\" +
            ".[a-zA-Z0-9]{2,6}$") @RequestParam("email") String email, HttpSession session) {
        if (service.sendValidateEmail(email,session.getId()))
            return Restbean.success("邮件已发送请注意查收");
        else
            return Restbean.failure(400, "邮件发送失败，请联系管理员");
    }
}
