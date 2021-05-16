package com.example.technologyforum.web.controller;

import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.UserDTO;
import com.example.technologyforum.web.pojo.Notify;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.MessageService;
import com.example.technologyforum.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功能描述：
 * create by xq on 2021/4/14 19:42
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


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
     * 用户登出
     *
     * @return
     */
    @PostMapping("/logout")
    @ResponseBody
    public Response<Boolean> logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute("userinfo");
            session.removeAttribute("msgnum");
            session.invalidate();
        }
        return Response.success(true);
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

    /**
     * 拉取用户未读消息
     */
    @PostMapping("/userReMsg")
    @ResponseBody
    public Response<List<Notify>> queryMsg(HttpSession session){
        List<Notify> list = new ArrayList<Notify>();
        User user = (User)session.getAttribute("userinfo");
        if(null != user && user.getId() !=null){
            list = messageService.queryUserREJECTMsg(user.getId());
        }
        return Response.success(list);
    }

    /**
     * 删除全部信息
     * @param session
     * @return
     */
    @PostMapping("/delAllMsg")
    @ResponseBody
    public Response<String> delAllMsg(HttpSession session){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            if(messageService.delBatchMsgById(user.getId())>0){
                return Response.success("成功");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 删除单条信息
     * @param session
     * @param msgId
     * @return
     */
    @PostMapping("/delOneMsg")
    @ResponseBody
    public Response<String> delOneMsgById(HttpSession session,int msgId){
        User user = (User)session.getAttribute("userinfo");
        if(user != null && !Objects.isNull(msgId)){
            if(messageService.delOneMsgById(user.getId(),msgId)>0){
                return Response.success("成功");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }
    /**
     * 修改头像
     * @param session
     * @param userHeadImgSrc
     * @return
     */
    @PostMapping("/reheadimg")
    @ResponseBody
    public Response<String> reheadimg(HttpSession session, String userHeadImgSrc){
        User user = (User)session.getAttribute("userinfo");
        user.setImgUrl(userHeadImgSrc);
        if(userService.reuserinfo(user)>0){
            // 更改session状态
            session.setAttribute("userinfo",user);
        }
        return Response.success("成功");
    }

    /**
     * 修改信息
     * @param userdto
     * @return
     */
    @PostMapping("/reuserinfo")
    @ResponseBody
    public Response<String> reuserinfo(HttpSession session,User userdto){
        User user = (User)session.getAttribute("userinfo");
        if(user == null){
            return Response.success("用户未登录");
        }
        if(user != null){
            if(userdto.getRemark()!= null){
                user.setRemark(userdto.getRemark());
            }
            if(userdto.getName() != null){
                user.setName(userdto.getName());
            }
            if(userdto.getTitle() !=null){
                user.setTitle(userdto.getTitle());
            }
            if(userdto.getSex() != null){
                user.setSex(userdto.getSex());
            }
            if(userService.reuserinfo(user)>0){
                // 更改session状态
                session.setAttribute("userinfo",user);
                return Response.success("成功");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 发送私信
     * @param session
     * @param acpter
     * @param content
     * @return
     */
    @PostMapping("/sendpermsg")
    @ResponseBody
    public Response<String> sendPerMsg(HttpSession session,int acpter,String content){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            // 组装消息
            String msg = "<a href='/front/userInfo?id="+user.getId()+"'><cite>"+user.getName()+"</cite></a>"+"向您发送了一条信息:"+content;
            return messageService.sendMsg(user.getId(),acpter,msg);
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 修改密码
     * @param session
     * @param nowpass
     * @param pass
     * @return
     */
    @PostMapping("/repass")
    @ResponseBody
    public Response<String> repass(HttpSession session, String nowpass,String pass){
        User user = (User)session.getAttribute("userinfo");
        if(user == null){
            return Response.success("用户未登录");
        }
        if(user.getPassword().equals(nowpass)){
            user.setPassword(pass);
            if(userService.reuserinfo(user)>0){
                // 清除session 重新登录
                session.removeAttribute("userinfo");
                session.removeAttribute("msgnum");
                session.invalidate();
            }
            return Response.success("成功");
        }else{
            return Response.success("密码错误");
        }
    }
    /**
     * 忘记密码
     */
    @PostMapping("/forgetpass")
    @ResponseBody
    public Response<String> forgetpass(User user,String vercode){
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword())) {
            return Response.fail(CodeMsg.FAIL);
        }
        return userService.forgetpass(user,vercode);
    }
}

