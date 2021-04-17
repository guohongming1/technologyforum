package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.QuestionCommentDTO;
import com.example.technologyforum.web.dto.QuestionDTO;
import com.example.technologyforum.web.dto.TableResultDTO;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.QuestionComment;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功能描述：
 * create by 小七 on 2021/4/17 11:40
 */
@Controller
@RequestMapping("/comm")
public class CommonController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserMapper userMapper;



    /**
     * 获取热门问题
     * @return
     */
    @PostMapping("/questionHot")
    @ResponseBody
    public Response<List<QuestionDTO>> questionHot(){
        List<Integer> listId = redisService.getTopNum(Constants.QUESTION_HOT_NAME);
        List<Question> list = new ArrayList<>();
        if(listId != null && listId.size() >0){
            listId.forEach(item->list.add(questionService.selectById(item)));
        }
         return Response.success(this.setQuestionDTOList(list));
    }

    /**
     * 获取问答前十的排名
     * @return
     */
    @PostMapping("/quesrank-list")
    @ResponseBody
    public Response<List<User>> userQuestionComRank(){
        List<Integer> listId = redisService.getTopNum(Constants.USER_QUESTION_COM_NUM);
        List<User> users = new ArrayList<>();
        if(listId != null && listId.size() >0) {
            listId.forEach(item -> {
                User user = userMapper.getUserInfoByPrimaryKey(item);
                if(user != null){
                    Number num = redisService.getScore(Constants.USER_QUESTION_COM_NUM,item);
                    if(!Objects.isNull(num)){
                        int i = num.intValue();
                        user.setPassword(String.valueOf(i));
                    }
                    users.add(user);
                }
            });
        }
        return Response.success(users);
    }

    /**
     * 获取评论
     * @param questionId
     * @return
     */
    @PostMapping("/getQuestionComment")
    @ResponseBody
    public Response<List<QuestionCommentDTO>> getQuestionComment(int questionId){
        // TODO 点赞功能未考虑
        return Response.success(this.setQuesCommentData(questionService.getQuestionComment(questionId)));
    }

    public List<QuestionCommentDTO> setQuesCommentData(List<QuestionComment> list){
        List<QuestionCommentDTO> result = new ArrayList<>();
        list.forEach(item->{
            User user = userMapper.getUserInfoByPrimaryKey(item.getUserId());
            QuestionCommentDTO dto = new QuestionCommentDTO();
            BeanUtils.copyProperties(item,dto);
            dto.setUserName(user.getName());
            dto.setUserImg(user.getImgUrl());
            result.add(dto);
        });
        return result;
    }

    /**
     * 获取最新问题，按时间排序 分页
     * @return
     */
    @PostMapping("/getnewQuestion")
    @ResponseBody
    public TableResultDTO<List<QuestionDTO>> getnewQuestion(int limit, int page){
        List<Question> list = questionService.selectPageVo(limit,page,null,null);
        QueryWrapper<Question> query = new QueryWrapper<>();
        int count = questionService.getCount(query);
        return  new TableResultDTO<>(200, "", count, this.setQuestionDTOList(list));
    }

    /**
     * 获取最新已解决问题 分页
     */
    @PostMapping("/getnewSQuestion")
    @ResponseBody
    public TableResultDTO<List<QuestionDTO>> getnewSQuestion(int limit,int page){
        List<Question> list = questionService.selectPageVo(limit,page,null,Constants.QUESTION_YES);
        QueryWrapper<Question> query = new QueryWrapper<>();
        query.eq("flag",Constants.QUESTION_YES);
        int count = questionService.getCount(query);
        return new TableResultDTO<>(200, "", count, this.setQuestionDTOList(list));
    }

    /**
     * 组装前端数据
     * @param list
     * @return
     */
    public List<QuestionDTO> setQuestionDTOList(List<Question> list){
        List<QuestionDTO> result = new ArrayList<>();
        list.forEach(item->{
            User user = userMapper.selectByPrimaryKey(item.getUserId());
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
}
