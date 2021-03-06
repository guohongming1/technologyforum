package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.NewTopicDTO;
import com.example.technologyforum.web.dto.TopicCommentDTO;
import com.example.technologyforum.web.mapper.CollectMapper;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.*;
import com.example.technologyforum.web.service.GroupService;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CollectMapper collectMapper;


    @PostMapping("/newgroup")
    @ResponseBody
    public Response<String> createGroup(Group group, HttpSession session){
        User user = (User)session.getAttribute("userinfo");
        group.setFlag(Constants.PASS_NO);
        group.setDate(new Date());
        group.setTopicNum(0);
        group.setMember(0);
        group.setUserId(user.getId());
        if(group.getHeadImg() == null){
            group.setHeadImg(user.getImgUrl());
        }
        return groupService.createGroup(group);
    }
    @RequestMapping("/tonewtopic")
    public String newtopic(Model model, int groupId){
        QueryWrapper<Group> query = new QueryWrapper<>();
        query.eq("id",groupId);
        query.ne("flag",(byte)3);
        List<Group> group = groupService.selectTravelGroup(query);
        List<String> tags = new ArrayList<>();
        if(group != null && group.size()>0){
            Group travelGroup = group.get(0);
            String[] tag = travelGroup.getTags().split("#");
            for(int i=0;i<tag.length;i++){
                if(tag[i]!=null && !"".equals(tag[i])){
                    tags.add(tag[i]);
                }
            }
        }
        model.addAttribute("tags",tags);
        model.addAttribute("groupId",groupId);
        return "front/newTopic";
    }

    @PostMapping("/newtopic")
    @ResponseBody
    public Response<String> createTopic(HttpSession session, NewTopicDTO topicDTO) {
        User user = (User)session.getAttribute("userinfo");
        if(!Objects.isNull(topicDTO)){
            //??????????????????????????????
            if(!groupService.checkGroupMById(Integer.valueOf(topicDTO.getGroupId()),user.getId())){
                return Response.fail(CodeMsg.FAIL);
            }
            return groupService.createTopic(topicDTO);
        }
        return Response.fail(100,"????????????");
    }

    /**
     *????????????
     * @param session
     * @param id
     * @return
     */
    @PostMapping("/joinGroup")
    @ResponseBody
    public Response<String> joinGroup(HttpSession session, int id){
        User user = (User)session.getAttribute("userinfo");
        // ????????????????????????
        Group group = groupService.queryTravelGroupById(id);
        if(group == null){
            return Response.fail(CodeMsg.FAIL);
        }
        if(user!=null && !Objects.isNull(id) && !groupService.checkGroupMById(id,user.getId())){
           if(groupService.joinGroup(user.getId(),id)>0){
               redisService.addHot(id,"2",Constants.TOPIC_HOT_NAME);
               // ????????????
               String content = "????????????:<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+ "</cite></a>????????????????????????"+
                       "<a href='/front/group_detail?id="+group.getId()+"'><cite>"+group.getTitle()+"</cite></a>";
               messageService.sendMsg(user.getId(),group.getUserId(),content);
               return Response.success("????????????");
           }
        }
        return Response.fail(CodeMsg.FAIL);
    }


    /**
     * ????????????
     * @param session
     * @param topicId
     * @param content
     * @param userIds
     * @return
     */
    @PostMapping("/createTopicComment")
    @ResponseBody
    public Response<String> createTopicComment(HttpSession session, int topicId, String content, String userIds){
        User user = (User)session.getAttribute("userinfo");
        String[] userId = null;
        Topic topic = groupService.queryTopicById(topicId);
        if(topic !=null && user != null && !("").equals(userIds)){
            userId = userIds.split("-");
            // ????????????
            for(int i=0;i<userId.length;i++){
                String msg ="????????????:<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+ "</cite></a>????????????"+"<a href='/topic_detail?id="+topic.getId()+"'><cite>"+topic.getTitle()+"</cite></a>"+
                        "????????????"+content;
                messageService.sendMsg(user.getId(),Integer.valueOf(userId[i]),msg);
            }
        }
        if(user != null && groupService.createTopicComment(topicId,user.getId(),content)>0){
            //??????????????????
            redisService.setCommentNum(topicId,CollectionKey.TOPIC_KEY_COM_NUM);
            // ????????????
            redisService.addHot(topicId,"2",Constants.TOPIC_HOT_NAME);
            // ????????????????????????
            String msgcontent = "???????????????"+"<a href='/front/topic_detail?id="+topic.getId()+"'><cite>"+topic.getTitle()+"</cite></a>"+"?????????????????????????????????";
            messageService.sendRemind(topicId,Constants.TOPIC_MSG,Constants.COM_MSG,user.getId(),msgcontent);
            return Response.success("??????");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * ????????????
     * @param session
     * @param id
     * @param topicId
     * @return
     */
    @PostMapping("/delTopicComment")
    @ResponseBody
    public Response<String> delTopicComment(HttpSession session, int id, int topicId){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            if(groupService.delTopicComment(id,user.getId())>0){
                redisService.reCollectOrCommentNum(topicId,CollectionKey.TOPIC_KEY_COM_NUM);
                return Response.success("????????????");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * ????????????
     * @param session
     * @param topicId
     * @return
     */
    @PostMapping("/collectTopic")
    @ResponseBody
    public Response<String> collectTopic(HttpSession session, int topicId){
        User user = (User)session.getAttribute("userinfo");
        QueryWrapper<Collect> query = new QueryWrapper<>();
        query.eq("user_id",user.getId());
        query.eq("type",(byte)3);
        query.eq("pro_id",topicId);
        if(collectMapper.selectCount(query)>0){
            return Response.fail(CodeMsg.FAIL);
        }
        if(user != null){
            Topic topic = groupService.queryTopicById(topicId);
            if(topic != null){
                //????????????
                String msgcontent = "<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+"</cite></a>?????????????????????:"+
                        "<a href='/front/questionDetail?id="+topic.getId()+"'><cite>"+topic.getTitle()+"</cite></a>";
                //????????????
                messageService.sendMsg(user.getId(),topic.getUserId(),msgcontent);
                // ???????????????
                Collect collect = new Collect();
                collect.setFlag(Constants.ACPT);
                collect.setUserId(user.getId());
                collect.setType((byte)3);
                collect.setProId(topic.getId());
                collect.setDate(new Date());
                collectMapper.insertSelective(collect);
                //??????????????????
                redisService.setCollectNum(topicId,CollectionKey.TOPIC_KEY_COL_NUM);
                return Response.success("??????");
            }
            redisService.addHot(topicId, "3",Constants.TOPIC_HOT_NAME);//????????????
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * ????????????????????????????????????
     * @param session
     * @return
     */
    @PostMapping("/UserGroupAndTopicInfo")
    @ResponseBody
    public Response<Map<String,Object>> UserGroupAndTopicInfo(HttpSession session){
        User user = (User)session.getAttribute("userinfo");
        Map<String,Object> map = new HashMap<>();
        if(user != null){
            // ???????????????????????????
            QueryWrapper<Group> query = new QueryWrapper<>();
            query.eq("user_id",user.getId());
            query.ne("flag",(byte)3);
            query.orderByDesc("date");
            map.put("usergroup",groupService.selectTravelGroup(query));
            // ???????????????????????????
            map.put("userjoingroup",groupService.selectJoinGroup(user.getId()));
            // ???????????????????????????
            map.put("usercoltopic",groupService.selectColGroup(user.getId()));
            // ???????????????????????????
            map.put("usertopic",groupService.selectUserTopic(user.getId()));
            return Response.success(map);
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * ????????????
     * @param id
     * @param type 1??????????????? 2????????????????????? 3 ???????????? 4 ????????????
     * @return
     */
    @PostMapping("/groupDel")
    @ResponseBody
    public Response<String> groupDel(HttpSession session, int id, String type){
        User user = (User)session.getAttribute("userinfo");
        // ????????????
        if("1".equals(type)){
            // ???????????????????????????
            if(groupService.outGroup(user.getId(),id)>0){
                return Response.success("??????");
            }
        }
        // ?????????????????????
        if("2".equals(type)){
            if(groupService.delColTopic(user.getId(),id)>0){
                //??????redis????????????
                redisService.delHot(id,Constants.TOPIC_HOT_NAME);
                return Response.success("??????");
            }
        }
        // ???????????? ???????????????
        if("3".equals(type)){
            if (groupService.disGroup(user.getId(),id)>0){
                return Response.success("??????");
            }
        }
        //???????????? ????????????
        if("4".equals(type)){
            if(groupService.delTopic(user.getId(),id)>0){
                return Response.success("??????");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }
    /**
     *  ??????????????????
     * @param list
     * @return
     */
    public List<TopicCommentDTO> setCommentData(List<TopicComment> list){
        List<TopicCommentDTO> result = new ArrayList<>();
        list.forEach(item->{
            User user = userMapper.getUserInfoByPrimaryKey(item.getUserId());
            TopicCommentDTO dto = new TopicCommentDTO();
            BeanUtils.copyProperties(item,dto);
            dto.setUserImg(user.getImgUrl());
            dto.setUserName(user.getName());
            result.add(dto);
        });
        return result;
    }
}
