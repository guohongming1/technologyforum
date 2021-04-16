package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.cache.UserKey;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.util.CodeUtil;
import com.example.technologyforum.util.RedisUtil;
import com.example.technologyforum.web.dto.UserDTO;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.IMailService;
import com.example.technologyforum.web.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 郭红明
 * @version 1.0
 * @Date: 2020/4/12
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IMailService mailService;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    @Transactional
    public Response<Boolean> register(User user, String vercode) {
        String cacheCode = redisUtil.get(UserKey.MAIL_KEY, "register");
        // 缓存验证码为空
        if (StringUtils.isEmpty(cacheCode)) {
            return Response.fail(CodeMsg.CODE_TIME_OUT);
        }
        // 验证码错误
        if (!cacheCode.equals(vercode)) {
            return Response.fail(CodeMsg.VERCODE_ERROR);
        }
        // 查询当前注册用户是否已存在
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email",user.getEmail());
        User dbEmailUser = userMapper.selectOne(query);
        if(!Objects.isNull(dbEmailUser)){
            return Response.fail(CodeMsg.USER_EXIST_EMAIL);
        }
        // TODO 用户昵称暂时不考虑

        // 用户角色硬编码
        user.setRole("2");
        user.setDate(new Date());

        // 向数据库中新增用户
        if(!(userMapper.insert(user)>0)){
            return Response.fail(CodeMsg.USER_REGISTER_ERROR);
        }


        return Response.success(true);
    }

    /**
     * 用户登录
     * @param user
     * @param session
     * @return
     */
    @Override
    public Response<String> login(UserDTO user, HttpSession session) {
        String email = user.getEmail();
        String password = user.getPassword();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email",email);
        query.eq("password",password);
        User userdata = userMapper.selectOne(query);
        if(Objects.isNull(userdata)){
           return Response.fail(CodeMsg.USER_PASS_ERROR);
        }
        session.setAttribute("userinfo",userdata);
        // 设置session存在最长时间
        session.setMaxInactiveInterval(60 * 60 * 2);
        return Response.success(userdata.getRole());
    }

    /**
     * 邮箱验证码
     * @param email
     * @param mode
     * @return
     */
    @Override
    public Response<Boolean> sendVercode(String email, String mode) {
        // 如果是注册先验证用户是否已经存在
        if("register".equals(mode)) {
            // 查询当前注册用户是否已存在
            QueryWrapper<User> query = new QueryWrapper<>();
            query.eq("email", email);
            User dbEmailUser = userMapper.selectOne(query);
            dbEmailUser.setPassword("123");
            if(userMapper.updateByPrimaryKeySelective(dbEmailUser)>0){
                return Response.success(true);
            }

        }
        // 邮件主题设置
        String subject = "HOW玩攻略平台注册验证";
        String verCode = CodeUtil.randomCode();
        // 邮件内容设置
        String content = "随机验证码：" + verCode + "\n" + "请在60秒内完整验证";
      //  mailService.sendHtmlMail(email,subject,content);
        // 缓存vercode至redis 缓存有效时间为60s
        return redisUtil.set(UserKey.MAIL_KEY, mode, verCode)
                ? Response.success(true)
                : Response.fail(CodeMsg.SERVER_ERROR);
    }

    /**
     * 忘记密码
     * @param user
     * @param vercode
     * @return
     */
    public Response<String> forgetpass(User user,String vercode){
        String cacheCode = redisUtil.get(UserKey.MAIL_KEY, "forget");
        // 缓存验证码为空
        if (StringUtils.isEmpty(cacheCode)) {
            return Response.fail(CodeMsg.CODE_TIME_OUT);
        }
        // 验证码错误
        if (!cacheCode.equals(vercode)) {
            return Response.fail(CodeMsg.VERCODE_ERROR);
        }
        // 查询当前注册用户是否已存在
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("email",user.getEmail());
        User dbEmailUser = userMapper.selectOne(query);
        if(!Objects.isNull(dbEmailUser)){
            return Response.fail(CodeMsg.USER_EXIST_EMAIL);
        }
        dbEmailUser.setPassword(user.getPassword());
        if(userMapper.updateByPrimaryKeySelective(dbEmailUser)>0){
            return Response.success("修改成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 用户信息修改
     * @param user
     * @return
     */
    @Override
    public int reuserinfo(User user){
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public List<User> selectPageVo(int limit, int page, String pattern){
        // 分页
        Page<User> pageHelper = new Page<>();
        pageHelper.setSize(limit);
        pageHelper.setCurrent(page);
        IPage<User> pageVo = userMapper.selectPageVo(pageHelper,pattern);
        return pageVo.getRecords();
    }

    /**
     * 查询用户数量
     * @param queryWrapper
     * @return
     */
    @Override
    public int getCount(QueryWrapper<User> queryWrapper){
        return userMapper.selectCount(new QueryWrapper<User>());
    }

    /**
     * 根据用户ID查询
     * @param id
     * @return
     */
    @Override
    public User queryUserById(int id){
        return userMapper.selectByPrimaryKey(id);
    }


}
