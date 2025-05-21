package com.example.banktransaction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 处理首页请求
 */
@Controller
public class HomeController {
    
    /**
     * 首页，重定向到静态页面
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
} 