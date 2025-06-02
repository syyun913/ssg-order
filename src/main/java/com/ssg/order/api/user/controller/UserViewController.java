package com.ssg.order.api.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/user")
public class UserViewController {

    @GetMapping("/login-page")
    public String loginPage() {
        return "user/login";
    }

    @GetMapping("/register-page")
    public String registerPage() {
        return "user/register";
    }
} 