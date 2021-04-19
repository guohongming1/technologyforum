package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.web.dto.QuestionDTO;
import com.example.technologyforum.web.mapper.CollectMapper;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.Collect;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功能描述：
 * create by 小七 on 2021/4/14 20:20
 */
@Controller
@RequestMapping("front")
public class FrontPageController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CollectMapper collectMapper;

    @RequestMapping("login")
    public String login(){
        return "login";
    }

    @RequestMapping("/reg")
    public String reg(){
        return "reg";
    }

    @RequestMapping("/question")
    public String question(){
        return "front/question";
    }

    @GetMapping("/search-page")
    public String searchPage(HttpServletRequest request, String title){
        request.setAttribute("pattern", title);
        return "front/search";
    }

    /**
     * 问题详情
     * @param session
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/questionDetail")
    public String question_detail(HttpSession session, Model model, int id){
        User user = (User)session.getAttribute("userinfo");
        Question question = questionService.selectById(id);
        if(user != null){
            QueryWrapper<Collect> query = new QueryWrapper<>();
            query.eq("user_id",user.getId());
            query.eq("type",(byte)2);
            query.eq("pro_id",id);
            if(collectMapper.selectCount(query)>0){
                model.addAttribute("colstate","2");
            }else if(question.getUserId()==user.getId()){//作者本人不需要显示收藏按钮
                model.addAttribute("colstate","3");
            }else{
                model.addAttribute("colstate","1");
            }
        }else{
            model.addAttribute("colstate","1");
        }
        if(question != null){
            List<Question> list = new ArrayList<>();
            list.add(question);
            model.addAttribute("question",this.setQuestionDTOList(list).get(0));
            redisService.addHot(id,"1", Constants.QUESTION_HOT_NAME);
        }
        return "front/question_detail";
    }

    /**
     * 组装前端数据
     * @param list
     * @return
     */
    public List<QuestionDTO> setQuestionDTOList(List<Question> list){
        List<QuestionDTO> result = new ArrayList<>();
        list.forEach(item->{
            User user = userMapper.getUserInfoByPrimaryKey(item.getUserId());
            QuestionDTO dto = new QuestionDTO();
            BeanUtils.copyProperties(item,dto);
            if(dto.getImgUrl() == null || dto.getImgUrl() == ""){
                // 如果没有问题图像就使用用户头像
                dto.setImgUrl(user.getImgUrl());
            }
            dto.setUserName(user.getName());
            // 获取热度
            Number hotNum = redisService.getScore(Constants.QUESTION_HOT_NAME, item.getId());
            if(!Objects.isNull(hotNum)){
                dto.setHotNum(hotNum.intValue());
            }else{
                dto.setHotNum(0);
            }
            // 获取评论数目
            Number comNum = redisService.getViewNum(item.getId(), CollectionKey.QUESTION_KEY_COM_NUM);
            if(!Objects.isNull(comNum)){
                dto.setCommentNum(comNum.intValue());
            }else{
                dto.setCommentNum(0);
            }
            result.add(dto);
        });
        return result;
    }

    /**
     * 用户消息页面
     * @return
     */
    @RequestMapping("/usermsg")
    public String usermsg(){
        return "user/message";
    }

    @RequestMapping("/userset")
    public String userset(HttpSession session,Model model){
        return "user/set";
    }
}

