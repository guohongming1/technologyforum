package com.example.technologyforum.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述：
 * create by guohm on 2021/4/14 20:20
 */
@Controller
@RequestMapping("front")
public class FrontPageController {

    @RequestMapping("login")
    public String login(){
        return "login";
    }

    @RequestMapping("/reg")
    public String reg(){
        return "reg";
    }
}
