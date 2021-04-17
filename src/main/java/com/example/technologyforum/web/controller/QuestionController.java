package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.QuestionCommentDTO;
import com.example.technologyforum.web.mapper.CollectMapper;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.Collect;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.QuestionComment;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.MessageService;
import com.example.technologyforum.web.service.QuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * create by 小七 on 2021/4/17 11:22
 */
@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CollectMapper collectMapper;

    @RequestMapping("/newquestion")
    public String newquestion(){
        return "front/newQuestion";
    }

    /**
     * 发表问题
     * @param question
     * @param session
     * @return
     */
    @PostMapping("/submitquestion")
    @ResponseBody
    public Response<String> submitquestion(Question question, HttpSession session) {
        Question result = new Question();
        BeanUtils.copyProperties(question,result);
        result.setDate(new Date());
        result.setFlag(Constants.QUESTION_NO);
        User user = (User)session.getAttribute("userinfo");
        result.setUserId(user.getId());
        return questionService.newQuestion(result);
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

    /**
     * 创建评论
     * @param session
     * @param questionId
     * @param content
     * @return
     */
    @PostMapping("/createQuestionCommment")
    @ResponseBody
    public Response<String> createQuestionCommment(HttpSession session,int questionId,String content,String userIds){
        User user = (User)session.getAttribute("userinfo");
        String[] userId = null;
        Question question = questionService.selectById(questionId);
        if(question !=null && user != null && !("").equals(userIds)){
            userId = userIds.split("-");
            // 发送通知
            for(int i=0;i<userId.length;i++){
                String msg ="<a href='/userinfo?id="+user.getId()+"'><cite>"+user.getName()+ "</cite></a>在问答："+"<a href='/front/questionDetail?id="+question.getId()+"'><cite>"+question.getTitle()+"</cite></a>"+
                        "回复您："+content;
                messageService.sendMsg(user.getId(),Integer.valueOf(userId[i]),msg);
            }
        }
        if(user != null && questionService.createQuestionCommment(user.getId(),questionId,content)>0){
            //评论数量加一
            redisService.setCommentNum(questionId, CollectionKey.QUESTION_KEY_COM_NUM);
            //问答排名加一
            redisService.addQuestionCommentNum(user.getId(),Constants.USER_QUESTION_COM_NUM);
            // 热度增加
            redisService.addHot(questionId,"2",Constants.QUESTION_HOT_NAME);
            // 发送消息通知全部关注此问题的用户
            String msgcontent = "来自问答："+"<a href='/front/questionDetail?id="+question.getId()+"'><cite>"+question.getTitle()+"</cite></a>"+"评论有更新，请注意关注";
            messageService.sendRemind(questionId,Constants.QUESTION_MSG,Constants.COM_MSG,user.getId(),msgcontent);
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 收藏问题
     * @param session
     * @param questionId
     * @return
     */
    @PostMapping("/collectQuestion")
    @ResponseBody
    public Response<String> collectQuestion(HttpSession session,int questionId){
        User user = (User)session.getAttribute("userinfo");
        QueryWrapper<Collect> query = new QueryWrapper<>();
        query.eq("user_id",user.getId());
        query.eq("type",(byte)2);
        query.eq("pro_id",questionId);
        if(collectMapper.selectCount(query)>0){
            return Response.fail(CodeMsg.FAIL);
        }
        if(user != null){
            Question question = questionService.selectById(questionId);
            if(question != null){
                //组装消息
                String msgcontent = "<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+"</cite></a>收藏了您的提问:"+
                        "<a href='/front/questionDetail?id="+question.getId()+"'><cite>"+question.getTitle()+"</cite></a>";
                //发送消息
                messageService.sendMsg(user.getId(),question.getUserId(),msgcontent);
                // 插入收藏表
                Collect collect = new Collect();
                collect.setFlag(Constants.ACPT);
                collect.setUserId(user.getId());
                collect.setType((byte)2);
                collect.setProId(questionId);
                collect.setDate(new Date());
                collectMapper.insertSelective(collect);
                //收藏数量加一
                redisService.setCollectNum(questionId,CollectionKey.QUESTION_KEY_COM_NUM);
                return Response.success("成功");
            }
            redisService.addHot(questionId, "3",Constants.QUESTION_HOT_NAME);//增加热度
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 删除评论
     * @param session
     * @param id
     * @return
     */
    @PostMapping("/delQuestionComment")
    @ResponseBody
    public Response<String> delQuestionCommment(HttpSession session,int id,int questionId){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            int flag = questionService.delQuestionCommment(id,user.getId());
            if(flag==1){
                redisService.reCollectOrCommentNum(questionId,CollectionKey.QUESTION_KEY_COM_NUM);
                return Response.success("删除成功");
            }
            if(flag==-1){
                return Response.success("最佳答案不允许删除！");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 采纳评论
     * @param session
     * @param Id 评论表的id
     * @param questionId 问题表的id
     * @return
     */
    @PostMapping("/acptComment")
    @ResponseBody
    public Response<String> acptComment(HttpSession session,int Id,int questionId){
        User user = (User)session.getAttribute("userinfo");
        Question question = questionService.selectById(questionId);
        if(question!=null && user != null && questionService.acptComment(Id,questionId,user.getId())>0){
            String msgcontent = "问题："+"<a href='/front/questionDetail?id="+question.getId()+"'><cite>"+question.getTitle()+"</cite></a>"+"已解决,请注意关注";
            messageService.sendRemind(questionId,Constants.QUESTION_MSG,Constants.QUESTION_FIN_MSG,user.getId(),msgcontent);
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.FAIL);
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
}
