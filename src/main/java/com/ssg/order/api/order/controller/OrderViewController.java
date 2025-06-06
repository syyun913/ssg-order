package com.ssg.order.api.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/order")
public class OrderViewController {
    @GetMapping("/main-page")
    public String mainPage() {
        return "main";
    }

    @GetMapping("/details")
    public String orderDetails() {
        return "order/detail";
    }
}
