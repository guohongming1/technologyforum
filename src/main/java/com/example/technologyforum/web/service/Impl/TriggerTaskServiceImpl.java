package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.UserNotify;
import com.example.technologyforum.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭红明
 * @version 1.0
 * @Date: 2020/5/12
 */
@Service
public class TriggerTaskServiceImpl implements TriggerTaskService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ITechnologyService strategyService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private GroupService groupService;
    @Override
    public String initTask() {
        String content = "";
        content = this.delStrategy();
        content += this.delQuestion();
        content += this.delMsg();
        System.out.println("日终任务结束");
        return content;
    }

    public String delStrategy(){
        QueryWrapper<Technology> query = new QueryWrapper<>();
        query.eq("del_flag",(byte)1);
        List<Technology> list = strategyService.queryStrategy(query);
        List<Integer> listIds = new ArrayList<>();
        if(list != null && list.size() > 0){
            list.forEach(item->listIds.add(item.getId()));
        }
        // 删除全部缓存
        listIds.forEach(id->{
            redisService.deleteViewKey(id, CollectionKey.ESSAY_KEY_COM_NUM);
            redisService.deleteViewKey(id, CollectionKey.ESSAY_KEY_COL_NUM);
            redisService.delHot(id, Constants.ESSAY_HOT_NAME);
        });
        int ret = strategyService.delBatchStrategy(listIds);
        return "<p>攻略应删：&nbsp;"+listIds.size()+"&nbsp;成功：&nbsp;"+ret+"</p>";
    }

    public String delQuestion(){
        QueryWrapper<Question> query = new QueryWrapper<>();
        query.eq("flag",(byte)3);
        List<Question> list = questionService.queryList(query);
        List<Integer> listIds = new ArrayList<>();
        if(list != null && list.size() > 0){
            list.forEach(item->listIds.add(item.getId()));
        }
        // 删除全部缓存
        listIds.forEach(id->{
            redisService.deleteViewKey(id, CollectionKey.QUESTION_KEY_COM_NUM);
            redisService.deleteViewKey(id, CollectionKey.QUESTION_KEY_COL_NUM);
            redisService.delHot(id, Constants.QUESTION_HOT_NAME);
        });
        int ret = questionService.delQuestionBatch(listIds);
        return "<p>问答应删：&nbsp;"+listIds.size()+"&nbsp;成功：&nbsp;"+ret+"</p>";
     }

     public String delMsg(){
        QueryWrapper<UserNotify> query = new QueryWrapper<>();
        query.eq("readflag",(byte)2);
        List<UserNotify> notifyList = messageService.queryUserNotify(query);
        List<Integer> listIds = new ArrayList<>();
         if(notifyList != null && notifyList.size()>0){
             notifyList.forEach(item->{
                 //删除通知
                 messageService.delNotifyById(item.getNotifyId());
                 listIds.add(item.getId());
             });
         }
         int ret = messageService.delBatchUserNotify(listIds);
         return "<p>消息应删：&nbsp;"+listIds.size()+"&nbsp;成功：&nbsp;"+ret+"</p>";
     }
}
