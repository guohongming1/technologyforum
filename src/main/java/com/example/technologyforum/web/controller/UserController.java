package com.example.technologyforum.web.controller;

import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.UserDTO;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 功能描述：
 * create by guohm on 2021/4/14 19:42
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /*
     用户登录
     */
    @PostMapping("/login")
    @ResponseBody
    public Response<String> login(UserDTO user, HttpSession session){
        if(user == null){
            return Response.fail(CodeMsg.USER_NULL);
        }
        return userService.login(user,session);
    }

    /**
     * 发送邮箱随机数
     *
     * @return
     */
    @PostMapping("email-send")
    @ResponseBody
    public Response<Boolean> emailSend(String email, String mode) {
        // 邮箱为空
        if (StringUtils.isEmpty(email)) {
            return Response.fail(CodeMsg.MAIL_NULL);
        }

        return userService.sendVercode(email, mode);
    }

    /**
     * 用户注册
     * @param user
     * @param vercode
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public Response<Boolean> register(User user, String vercode){
        if(user == null){
            return Response.fail(CodeMsg.USER_NULL);
        }
        return  userService.register(user, vercode);
    }
}

