package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.util.IPUtils;
import com.example.technologyforum.web.dto.QuestionDTO;
import com.example.technologyforum.web.dto.TechnologyDTO;
import com.example.technologyforum.web.mapper.CollectMapper;
import com.example.technologyforum.web.mapper.GroupTypeMapper;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.*;
import com.example.technologyforum.web.service.GroupService;
import com.example.technologyforum.web.service.ITechnologyService;
import com.example.technologyforum.web.service.Impl.CommonServiceImpl;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.QuestionService;
import com.example.technologyforum.web.service.RecommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private CommonServiceImpl commonService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ITechnologyService technologyService;

    @Autowired
    private RecommentService recommentService;

    @Autowired
    private GroupTypeMapper groupTypeMapper;

    @RequestMapping("/index")
    public String index(Model model, HttpSession session, HttpServletRequest request)throws Exception{
        if(session.getAttribute("city") != null){
            model.addAttribute("city",session.getAttribute("city"));
        }else{
            String ip = IPUtils.getIpAddr(request);
            //ip=218.192.3.42&json=true
            String content = "ip="+ip+"&json=true";
            String city = IPUtils.getAddresses(content,"GBK");
            if(city != null){
                model.addAttribute("city",city);
                // 保存在session中，避免过度使用API在
                session.setAttribute("city",city);
            }else{
                model.addAttribute("city","北京");
            }
        }
        // 返回游记推荐
        List<TechnologyRecomd> list = recommentService.getList(2,1);
        List<TechnologyDTO> result = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            TechnologyDTO strategyDTO = new TechnologyDTO();
            BeanUtils.copyProperties(list.get(i),strategyDTO);
            strategyDTO.setHeadImgUrl(list.get(i).getReserve3());
            User user = userMapper.selectByPrimaryKey(list.get(i).getUserId());
            if(user != null){
                strategyDTO.setUserHeadImg(user.getImgUrl());
                strategyDTO.setUserName(user.getName());
            }
            Number hotNum = redisService.getScore(Constants.ESSAY_HOT_NAME, list.get(i).getId());
            if(!Objects.isNull(hotNum)){
                strategyDTO.setViewNum(hotNum.intValue());
            }else{
                strategyDTO.setViewNum(0);
            }
            Number colNum = redisService.getViewNum(list.get(i).getId(), CollectionKey.ESSAY_KEY_COL_NUM);
            if(!Objects.isNull(colNum)){
                strategyDTO.setCollectnum(colNum.intValue());
            }else{
                strategyDTO.setCollectnum(0);
            }
            Number comNum = redisService.getViewNum(list.get(i).getId(), CollectionKey.ESSAY_KEY_COM_NUM);
            if(!Objects.isNull(comNum)){
                strategyDTO.setCommentnum(comNum.intValue());
            }else{
                strategyDTO.setCommentnum(0);
            }
            result.add(strategyDTO);
        }
        // TODO 轮播图未完成
        model.addAttribute("recommend",result);
        return "front/index";
    }

    /**
     * 话题明细页面
     * @param session
     * @param model
     * @param id topic表的id
     * @return
     */
    @RequestMapping("/topic_detail")
    public String topic(HttpSession session,Model model,int id){
        Topic topic = groupService.queryTopicById(id);
        if(topic != null){
            TopicDetail topicDetail = groupService.queryTopicDetail(topic.getGrDeId());
            // 查询用户收藏 和是否是用户本人操作
            User user = (User)session.getAttribute("userinfo");
            if(user != null){
                if(commonService.queryCollect(user.getId(),(byte)3,id) || user.getId() == topic.getUserId()){
                    model.addAttribute("colflag","1");
                }else{
                    model.addAttribute("colflag","2");
                }
            }else{
                model.addAttribute("colflag","2");
            }
            // 查询作者 tags字段存储用户名
            User author = userMapper.getUserInfoByPrimaryKey(topic.getUserId());
            topic.setTags(author.getName());
            // title字段存储用户头像url
            topicDetail.setTitle(author.getImgUrl());
            model.addAttribute("topicDetail",topicDetail);
        }
        model.addAttribute("topic",topic);
        return "front/topic-detail";
    }

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

    @RequestMapping("/newgroup")
    public String newgroup(){
        return "front/newGroup";
    }

    /**
     * 小组id
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/group_detail")
    public String group_detail(HttpSession session,Model model,int id){
        User user = (User)session.getAttribute("userinfo");
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("flag",Constants.PASS_YES);
        Group technologyGroup = groupService.selectTravelGroup(queryWrapper).size()==0?null:groupService.selectTravelGroup(queryWrapper).get(0);
        if(technologyGroup!=null)
            model.addAttribute("authorName",userMapper.getUserInfoByPrimaryKey(technologyGroup.getUserId()).getName());
        if(user != null){
            // 查询用户是否加入了该小组
            QueryWrapper<GroupMember> query = new QueryWrapper<>();
            query.eq("group_id",id);
            query.eq("user_id",user.getId());
            if(groupService.getCountMember(query)>0 || technologyGroup.getUserId() == user.getId()){
                model.addAttribute("gflag","1");
            }else{
                model.addAttribute("gflag","2");
            }
        }else{
            model.addAttribute("gflag","2");
        }
        model.addAttribute("Group",technologyGroup);
        return "front/group-detail";
    }

    @PostMapping("/getGroupType")
    @ResponseBody
    public Response<List<GroupType>> getGroupType(HttpSession session){
        QueryWrapper<GroupType> query = new QueryWrapper<>();
        List<GroupType> groupTypeList = groupTypeMapper.selectList(query);
        return Response.success(groupTypeList);
    }


    @RequestMapping("/group")
    public String group(){
        return "front/group";
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

    @GetMapping("/strategydetail")
    public String detail(Model model,HttpSession session,int id,int detailId){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            boolean colstate = commonService.queryCollect(user.getId(),(byte)1,id);
            model.addAttribute("colstate",colstate);
        }
        // 热度增加
        redisService.addHot(id, "1",Constants.ESSAY_HOT_NAME);
        // 页面数据封装
        Technology strategy = technologyService.selectStrategyById(id);
        // 攻略明细ID，用于获取评论
        model.addAttribute("id",strategy.getId());
        model.addAttribute("detailId",strategy.getDetailId());
        model.addAttribute("title",strategy.getTitle());
        model.addAttribute("date",strategy.getDate());
        User author = userMapper.getUserInfoByPrimaryKey(strategy.getUserId());
        model.addAttribute("authorId",author.getId());
        model.addAttribute("authorName",author.getName());
        model.addAttribute("authorImg",author.getImgUrl());
        // 获取文章热度/评论数/收藏数目
        Number hotNum = redisService.getScore(Constants.ESSAY_HOT_NAME, id);
        if(!Objects.isNull(hotNum)){
            model.addAttribute("hotnum",hotNum.intValue());
        }else{
            model.addAttribute("hotnum",0);
        }
        Number colNum = redisService.getViewNum(id, CollectionKey.ESSAY_KEY_COL_NUM);
        if(!Objects.isNull(colNum)){
            model.addAttribute("colnum",colNum.intValue());
        }else{
            model.addAttribute("colnum",0);
        }
        Number comNum = redisService.getViewNum(id, CollectionKey.ESSAY_KEY_COM_NUM);
        if(!Objects.isNull(comNum)){
            model.addAttribute("comnum",comNum.intValue());
        }else{
            model.addAttribute("comnum",0);
        }
        TechnologyDetail detail = technologyService.getDetailById(strategy.getDetailId());
        if(detail != null){
            model.addAttribute("content",detail.getContent());
        }
        return "front/strategy-detail";
    }

    @RequestMapping("/userInfo")
    public String user(Model model,int id){
        User user = null;
        if(!Objects.isNull(id)){
            user = userMapper.selectByPrimaryKey(id);
        }
        // TODO 可能不需要暴露整个用户信息
        model.addAttribute("info",user);
        return "user/index";
    }
    @RequestMapping("/usergroup")
    public String usergroup(){
        return "user/group";
    }
    @RequestMapping("/userquestion")
    public String userquestion(){
        return "user/question";
    }
    @RequestMapping("/userstrategy")
    public String userstrategy(){
        return "user/strategy";
    }
    @RequestMapping("/forget")
    public String forget(){
        return "forget";
    }
}

