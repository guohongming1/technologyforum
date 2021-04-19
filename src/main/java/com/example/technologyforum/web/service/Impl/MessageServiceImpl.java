package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.mapper.*;
import com.example.technologyforum.web.pojo.Collect;
import com.example.technologyforum.web.pojo.MsgConfig;
import com.example.technologyforum.web.pojo.Notify;
import com.example.technologyforum.web.pojo.UserNotify;
import com.example.technologyforum.web.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * create by 小七 on 2021/4/17 11:09
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserNotifyMapper userNotifyMapper;

    @Autowired
    private TechnologyMapper technologyMapper;

    @Autowired
    private MsgConfigMapper msgConfigMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private NotifyMapper notifyMapper;
    @Override
    public int getMsgCountByUserId(int id) {
        return userNotifyMapper.getMsgCountByUserId(id);
    }

    @Override
    public void sendRemind(int target, int targetType, int action, int sender, String content) {
        // 需要接收信息的用户id
        List<Integer> list = new ArrayList<Integer>();

        // 攻略消息 只包括评论和收藏
        if(Constants.STRATEGY_MSG == targetType){
            // 消息接收者id
            int acpt_id;
            acpt_id = technologyMapper.selectByPrimaryKey(target).getUserId();
            // 用户接收消息
            if(this.queryMsgFlag(acpt_id,target,targetType,action)<= 0){
                list.add(acpt_id);
            }
        }
        // 话题消息 只包括评论和收藏
        if(Constants.TOPIC_MSG == targetType){
            int acpt_id;
            acpt_id = topicMapper.selectByPrimaryKey(target).getUserId();
            // 用户接收消息
            if(acpt_id != sender && this.queryMsgFlag(acpt_id,target,targetType,action)<= 0){
                list.add(acpt_id);
            }
        }
        // 问答消息 问答消息会同步推送给关注该问答的所有用户
        if(Constants.QUESTION_MSG == targetType){
            // 评论或者收藏消息  //收藏消息已经采用私信方式通知
            if(Constants.COM_MSG == action || Constants.COLLECT_MSG == action){
                int author_id;//作者id
                author_id = questionMapper.selectByPrimaryKey(target).getUserId();
                if(!(this.queryMsgFlag(author_id,target,targetType,action)> 0)&&author_id != sender){
                    list.add(author_id);
                }
                // 查询关注的用户
                QueryWrapper<Collect> query = new QueryWrapper<>();
                query.eq("type",(byte)2);// 问答收藏
                query.eq("pro_id",target);
                query.eq("flag",Constants.ACPT);
                List<Collect> collectList = collectMapper.selectList(query);
                //循环获取需要接收消息的用户
                for(Collect co:collectList){
                    if(co.getUserId() != sender){  //// 不许要向发送者再次发送消息
                        list.add(co.getUserId());
                    }
                }
            }
            // 问题解决消息 向所有关注问题的用户发送消息
            if(Constants.QUESTION_FIN_MSG == action){
                // 查询关注的用户
                QueryWrapper<Collect> query = new QueryWrapper<>();
                query.eq("type",(byte)2);
                query.eq("pro_id",target);
                query.eq("flag",Constants.ACPT);
                List<Collect> collectList = collectMapper.selectList(query);
                //循环获取需要接收消息的用户
                for(Collect co:collectList){
                    if(co.getUserId() != sender){ // 不许要向发送者再次发送消息
                        list.add(co.getUserId());
                    }
                }
            }
            // TODO 问题采纳消息 向提出评论的用户发送消息
        }
        if(list != null && list.size() > 0){
            Notify notify = new Notify();
            notify.setType(Constants.MSG_TYPE_PER);
            notify.setTarget(target);
            notify.setTargetType(targetType);
            notify.setSender(sender);
            notify.setContent(content);
            notify.setAction(String.valueOf(action));
            notify.setCreateTime(new Date());
            // 创建消息
            if(notifyMapper.insertSelective(notify)> 0){
                int notify_id = notify.getId();
                for(int i:list){
                    UserNotify userNotify = new UserNotify();
                    userNotify.setReadflag(Constants.ACPT);
                    userNotify.setUserId(i);
                    userNotify.setNotifyId(notify_id);
                    userNotify.setCreateTime(new Date());
                    // 发送消息
                    userNotifyMapper.insert(userNotify);
                }
            }
        }
    }

    /**
     * 查询消息是否被屏蔽,适用攻略、话题
     * @param acpt_id
     * @param target 文章id
     * @param type 类型问答、攻略、话题
     * @param action
     * @return
     */
    int queryMsgFlag(int acpt_id,int target,int type,int action){
        // 查询消息是否被屏蔽
        QueryWrapper<MsgConfig> query = new QueryWrapper<>();
        query.eq("user_id",acpt_id);
        query.eq("target_id",target);
        query.eq("type",String.valueOf(type));
        // 评论消息
        if(Constants.COM_MSG == action){
            query.eq("comment",Constants.REJECT);
        }else{
            query.eq("collect",Constants.REJECT);
        }
        // 用户接收消息
        return msgConfigMapper.selectCount(query);
    }

    @Override
    public Response<String> sendMsg(int sender, int acpter, String content) {
        Notify notify = new Notify();
        notify.setType(Constants.MSG_TYPE_PER);
        notify.setContent(content);
        notify.setSender(sender);
        notify.setCreateTime(new Date());
        if(notifyMapper.insertSelective(notify)> 0){
            UserNotify userNotify = new UserNotify();
            userNotify.setReadflag(Constants.ACPT);
            userNotify.setNotifyId(notify.getId());
            userNotify.setUserId(acpter);
            userNotify.setCreateTime(new Date());
            if(userNotifyMapper.insert(userNotify)>0){
                return Response.success("发送成功");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    @Override
    public List<Notify> queryUserAcptMsg(int userId) {
        return null;
    }

    @Override
    public List<Notify> queryUserREJECTMsg(int userId) {
        List<Notify> msgResults = new ArrayList<Notify>();
        List<UserNotify> notifyList = userNotifyMapper.queryMsg(userId,Constants.ACPT);
        for(UserNotify i:notifyList){
            Notify notify = notifyMapper.selectByPrimaryKey(i.getNotifyId());
            notify.setId(i.getId()); // 将notify的id改为usernotify的id
            msgResults.add(notify);
        }
        return msgResults;
    }

    @Override
    public int delOneMsgById(int userId, int msgId) {
        UserNotify userNotify = userNotifyMapper.selectByPrimaryKey(msgId);
        if(userNotify != null && userNotify.getUserId() == userId){
            userNotify.setReadflag(Constants.REJECT);
            return userNotifyMapper.updateByPrimaryKeySelective(userNotify);
        }
        return 0;
    }

    @Override
    public int delBatchMsgById(int userId) {
        QueryWrapper<UserNotify> query = new QueryWrapper<>();
        query.eq("user_id",userId);
        List<UserNotify> list = userNotifyMapper.selectList(query);
        int i=0;
        for(UserNotify u:list){
            u.setReadflag(Constants.REJECT);
            userNotifyMapper.updateByPrimaryKeySelective(u);
            i++;
        }
        return i;
    }

    @Override
    public List<UserNotify> queryUserNotify(QueryWrapper<UserNotify> query) {
        return null;
    }

    @Override
    public int delNotifyById(int id) {
        return 0;
    }

    @Override
    public int delBatchUserNotify(List<Integer> ids) {
        return 0;
    }
}
