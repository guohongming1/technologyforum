package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.TableResultDTO;
import com.example.technologyforum.web.mapper.CronMapper;
import com.example.technologyforum.web.mapper.TechnologyRecomdMapper;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.*;
import com.example.technologyforum.web.service.*;
import com.example.technologyforum.web.service.Impl.RedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 功能描述：
 * create by 小七 on 2021/4/14 22:37
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private IMailService mailService;
    @Autowired
    private ITechnologyService technologyService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CronMapper cronMapper;
    @Autowired
    private TriggerTaskService triggerTaskService;
    @Autowired
    private TechnologyRecomdMapper technologyRecomdMapper;


    // 页面跳转 start
    /**
     * 跳转后台控制台页面
     *
     * @return
     */
    @GetMapping("/index")
    public String toIndexPage() {
        return "background/index";
    }

    /**
     * 跳转到控制页面
     *
     * @return
     */
    @GetMapping("/console")
    public String toConsolePage() {
        return "background/home/console";
    }

    @GetMapping("/home1")
    public String toHome1Page() {
        return "background/home/homepage1";
    }

    @GetMapping("/strategy")
    public String strategy() {
        return "background/app/content/strategy";
    }

    @GetMapping("/question")
    public String question() {
        return "background/app/content/question";
    }

    @GetMapping("/group")
    public String group() {
        return "background/app/content/group";
    }

    @GetMapping("/userinfo")
    public String user() {
        return "background/app/content/userinfo-list";
    }

    @GetMapping("/TriggerTask")
    public String TriggerTask() {
        return "background/app/content/Scheduling-task";
    }
    // 页面跳转 end


    /**
     * 获取全部帖子
     * @param session
     * @param limit
     * @param page
     * @param title
     * @return
     */
    @GetMapping("/strategy-data")
    @ResponseBody
    public TableResultDTO<List<Technology>> strategyData(HttpSession session, int limit, int page, @RequestParam(value = "title", required = false)String title){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Technology> list = technologyService.adminGetList(limit,page,title);
        int count = technologyService.getCount();
        return new TableResultDTO<>(200,"",count,list);
    }

    /**
     * 批次删除攻略 物理删除
     * @param session
     * @param strategies
     * @return
     */
    @PostMapping("/strateBatchdelete")
    @ResponseBody
    public Response<String> strateBatchdelete(HttpSession session, @RequestBody Technology[] strategies){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Integer> listIds = new ArrayList<>();
        for(int i=0;i<strategies.length;i++){
            Technology strategy = strategies[i];
            listIds.add(strategy.getId());
        }
        listIds.forEach(id->{
            redisService.deleteViewKey(id, CollectionKey.ESSAY_KEY_COM_NUM);
            redisService.deleteViewKey(id, CollectionKey.ESSAY_KEY_COL_NUM);
            redisService.delHot(id, Constants.ESSAY_HOT_NAME);
        });
        int ret = technologyService.delBatchStrategy(listIds);
        if(ret > 0){
            return Response.success("删除成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 加入推荐
     * @param session
     * @param strategies
     * @return
     */
    @PostMapping("/addRecom")
    @ResponseBody
    public Response<String> addRecom(HttpSession session, @RequestBody Technology[] strategies){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Integer> listIds = new ArrayList<>();
        for(int i=0;i<strategies.length;i++){
            Technology strategy = technologyService.selectStrategyById(strategies[i].getId());
            if(strategy !=null){
                if(technologyRecomdMapper.selectByPrimaryKey(strategy.getId()) != null){
                    return Response.fail(CodeMsg.EXITES_FAIL);
                }
                TechnologyRecomd technologyRecomd = new TechnologyRecomd();
                BeanUtils.copyProperties(strategy,technologyRecomd);
                technologyRecomd.setReserve3(strategy.getHeadImgUrl());
                technologyRecomdMapper.insertSelective(technologyRecomd);
            }
        }
        return Response.success("推荐成功");
    }

    /**
     * 获取全部问答
     * @param session
     * @param limit
     * @param page
     * @param title
     * @return
     */
    @GetMapping("/question-data")
    @ResponseBody
    public TableResultDTO<List<Question>> questionData(HttpSession session, int limit, int page, @RequestParam(value = "title", required = false)String title){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Question> list = questionService.selectPageVo(limit,page,title,null);
        int count = questionService.getCount(new QueryWrapper<Question>());
        return new TableResultDTO<>(200,"",count,list);
    }

    /**
     * 检查用户角色
     * @param user
     * @return
     */
    public boolean checkUserRole(User user){
        if(user != null){
            User d = userMapper.selectByPrimaryKey(user.getId());
            if("2".equals(d.getRole())){
                return true;
            }
        }
        return false;
    }
    /**
     * 批次删除问答  物理删除
     * @param session
     * @param questions
     * @return
     */
    @PostMapping("/questionBatchdel")
    @ResponseBody
    public Response<String> strateBatchdelete(HttpSession session,@RequestBody Question[] questions){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Integer> listIds = new ArrayList<>();
        for(int i=0;i<questions.length;i++){
            Question question = questions[i];
            listIds.add(question.getId());
        }
        listIds.forEach(id->{
            redisService.deleteViewKey(id, CollectionKey.QUESTION_KEY_COM_NUM);
            redisService.deleteViewKey(id, CollectionKey.QUESTION_KEY_COL_NUM);
            redisService.delHot(id, Constants.QUESTION_HOT_NAME);
        });
        int ret = questionService.delQuestionBatch(listIds);
        if(ret > 0){
            return Response.success("删除成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 获取审核的小组
     * @param session
     * @param limit
     * @param page
     * @param title
     * @return
     */
    @GetMapping("/groupReview-data")
    @ResponseBody
    public TableResultDTO<List<Group>> groupReviewData(HttpSession session, int limit, int page, @RequestParam(value = "title", required = false)String title){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Group> list = groupService.selectPageVoGroupWithFlag(limit,page,Constants.PASS_NO);
        QueryWrapper<Group> query = new QueryWrapper<>();
        query.eq("flag",Constants.PASS_NO);
        int count = groupService.getGroupCount(query);
        return new TableResultDTO<>(200,"",count,list);
    }

    /**
     * 小组审核
     * @param id
     * @param flag
     * @return
     */
    @PostMapping("/grouppass")
    @ResponseBody
    public Response<String> shenghe(int id ,String flag){
        Group group = groupService.queryTravelGroupById(id);
        String msg = "";
        //审核通过
        if("1".equals(flag)){
            group.setFlag(Constants.PASS_YES);
            groupService.updateTravelGroupById(group);
            // 发送信息
            msg = "系统消息：您申请的小组["+group.getTitle()+"]审核通过了";
        }
        //审核驳回
        if("0".equals(flag)){
            group.setFlag(Constants.PASS_REJECT);
            groupService.updateTravelGroupById(group);
            // 发送信息
            msg = "系统消息：您申请的小组["+group.getTitle()+"]被驳回，请检查修改后重新申请";
        }
        User user = userMapper.selectByPrimaryKey(group.getUserId());
        // 邮件主题设置
        String subject = "小七平台提醒";
        mailService.sendHtmlMail(user.getEmail(),subject,msg);
        messageService.sendMsg(0,group.getUserId(),msg);
        return Response.success("成功");
    }

    /**
     * 获取全部用户数据
     * @param session
     * @param limit
     * @param page
     * @param title
     * @return
     */
    @GetMapping("/userinfo-data")
    @ResponseBody
    public TableResultDTO<List<User>> getUserData(HttpSession session,int limit, int page,@RequestParam(value = "title", required = false)String title){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<User> list = userService.selectPageVo(limit,page,title);
        int count = userService.getCount(new QueryWrapper<User>());
        return new TableResultDTO<>(200,"",count,list);
    }

    /**
     * 删除用户，非物理删除
     * @return
     */
    @PostMapping("/delBatchuser")
    @ResponseBody
    public Response<String> shenghe(HttpSession session,@RequestBody User[] users){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return null;
        }
        List<Integer> listIds = new ArrayList<>();
        for(int i=0;i<users.length;i++){
            User item = users[i];
            listIds.add(item.getId());
        }
        listIds.forEach(item->{
            User user = userMapper.getUserInfoByPrimaryKey(item);
            if(user !=null){
                user.setName("匿名");
                user.setEmail("000000");
                userMapper.updateByPrimaryKeySelective(user);
            }
        });
        return Response.success("成功");
    }
    /**
     * 获取未审核的小组数量
     * @return
     */
    @PostMapping("/getGroupScount")
    @ResponseBody
    public Response<Integer> getGroupScount(){
        QueryWrapper<Group> query = new QueryWrapper<>();
        query.eq("flag",Constants.PASS_NO);
        int count = groupService.getGroupCount(query);
        return Response.success(count);
    }

    /**
     * 获取月终任务信息
     * @param session
     * @return
     */
    @PostMapping("/triggerTaskInfo")
    @ResponseBody
    public Response<Cron> triggerTaskInfo(HttpSession session){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return Response.fail(CodeMsg.FAIL);
        }
        Cron cron = cronMapper.selectByPrimaryKey(1);
        if(cron != null){
            return Response.success(cron);
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 执行定时任务
     * @param session
     * @return
     */
    @PostMapping("/execuTriggerTask")
    @ResponseBody
    public Response<String> execuTriggerTask(HttpSession session){
        if(!checkUserRole((User)session.getAttribute("userinfo"))){
            return Response.fail(CodeMsg.FAIL);
        }
        String content = triggerTaskService.initTask();
        Cron cron = new Cron();
        cron.setId(1);
        cron.setDate(new Date());
        cron.setContent(content);
        if(cronMapper.updateByPrimaryKeySelective(cron)>0){
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

}
