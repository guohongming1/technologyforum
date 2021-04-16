package com.example.technologyforum.web.controller;

import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.service.IMailService;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述：
 * create by 小七 on 2021/4/14 22:37
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private IMailService mailService;



    // 页面跳转 start
    /**
     * 跳转后台控制台页面
     *
     * @return
     */
    @GetMapping("/index")
    public String toIndexPage() {
        return "background/index";
    }

    /**
     * 跳转到控制页面
     *
     * @return
     */
    @GetMapping("/console")
    public String toConsolePage() {
        return "background/home/console";
    }

    @GetMapping("/home1")
    public String toHome1Page() {
        return "background/home/homepage1";
    }

    @GetMapping("/strategy")
    public String strategy() {
        return "background/app/content/strategy";
    }

    @GetMapping("/question")
    public String question() {
        return "background/app/content/question";
    }

    @GetMapping("/group")
    public String group() {
        return "background/app/content/group";
    }

    @GetMapping("/userinfo")
    public String user() {
        return "background/app/content/userinfo-list";
    }

    @GetMapping("/TriggerTask")
    public String TriggerTask() {
        return "background/app/content/Scheduling-task";
    }
    // 页面跳转 end

}
