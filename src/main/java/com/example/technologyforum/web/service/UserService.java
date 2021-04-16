package com.example.technologyforum.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.UserDTO;
import com.example.technologyforum.web.pojo.User;


import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 郭红明
 * @version 1.0
 * @Date: 2020/4/12
 */
public interface UserService {
    Response<Boolean> register(User user, String vercode);

    Response<String> login(UserDTO userDTO, HttpSession session);
    Response<String> forgetpass(User user,String vercode);
    User queryUserById(int id);
    int reuserinfo(User user);
    List<User> selectPageVo(int limit,int page,String pattern);
    int getCount(QueryWrapper<User> queryWrapper);
    Response<Boolean> sendVercode(String email, String mode);
}
