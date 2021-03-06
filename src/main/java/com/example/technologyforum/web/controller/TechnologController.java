package com.example.technologyforum.web.controller;

import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.TechnologyDTO;
import com.example.technologyforum.web.mapper.CollectMapper;
import com.example.technologyforum.web.pojo.Collect;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TechnologyComment;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.ITechnologyService;
import com.example.technologyforum.web.service.Impl.CommonServiceImpl;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 功能描述：
 * create by xq on 2021/5/10 19:57
 */

@Controller
@RequestMapping("/strategy")
public class TechnologController {

    @Autowired
    private ITechnologyService technologyService;

    @RequestMapping("/newstrategy")
    public String newstrategy(){
        return "front/newStrategy";
    }

    @Autowired
    public RedisService redisService;

    @Autowired
    public MessageService messageService;

    @Autowired
    public CommonServiceImpl commonService;

    @Autowired
    public CollectMapper collectMapper;

    /**
     * 创建
     * @param strategyDTO
     * @return
     */
    @PostMapping("/post")
    @ResponseBody
    public Response<String> postStrategy(TechnologyDTO strategyDTO, HttpSession session){
        // 未发表不需要进行数据校验
        if(null != strategyDTO.getPushFlag()&& strategyDTO.getPushFlag()){
            // 后台数据校验
            if(null == strategyDTO || strategyDTO.getTitle() == null || strategyDTO.getAddress() == null
                    || strategyDTO.getContent() == null || strategyDTO.getHeadImgUrl() ==null){
                Response.fail(CodeMsg.FAIL);
            }
        }
        Technology strategy = new Technology();
        if(strategyDTO.getPushFlag()){
            strategy.setPushFlag(Constants.PUSH_YES);
        }else{
            strategy.setPushFlag(Constants.PUSH_NO);
        }
        strategy.setTitle(strategyDTO.getTitle());
        strategy.setHeadImgUrl(strategyDTO.getHeadImgUrl());
        strategy.setSketch(strategyDTO.getSketch());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //使用SimpleDateFormat的parse()方法生成Date
            Date date = new Date();
            strategy.setDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = (User)session.getAttribute("userinfo");
        strategy.setUserId(user.getId());

        return technologyService.createStrategy(strategy,strategyDTO.getContent());
    }

    /**
     * 更新
     * @param strategyDTO
     * @return
     */
    @PostMapping("/updateorsave")
    @ResponseBody
    public Response<String> updateStrategy(TechnologyDTO strategyDTO,HttpSession session){
        Technology strategy = new Technology();
        if(strategyDTO.getPushFlag()){
            strategy.setPushFlag(Constants.PUSH_YES);
        }else{
            strategy.setPushFlag(Constants.PUSH_NO);
        }
        strategy.setTitle(strategyDTO.getTitle());
        strategy.setHeadImgUrl(strategyDTO.getHeadImgUrl());
        strategy.setSketch(strategyDTO.getSketch());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //使用SimpleDateFormat的parse()方法生成Date
            if(null != strategyDTO.getDate()){
                Date date = sf.parse(strategyDTO.getDate());
                strategy.setDate(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        User user = (User)session.getAttribute("userinfo");
        strategy.setUserId(user.getId());
        return technologyService.updateStrategy(strategy,strategyDTO.getContent());
    }


    @PostMapping("/pull")
    @ResponseBody
    public Response<TechnologyDTO> pullStrategy(HttpSession session){
        User user = (User)session.getAttribute("userinfo");
        return technologyService.pullStrategy(user.getId());
    }

    /**
     * 评论
     * @param session
     * @param id
     * @return
     */
    @PostMapping("/substracomment")
    @ResponseBody
    public Response<String> substracomment(HttpSession session,int id,String content){
        User user = (User)session.getAttribute("userinfo");
        if(!Objects.isNull(id)){
            redisService.addHot(id, "2",Constants.ESSAY_HOT_NAME);//增加热度
            // 增加评论数
            redisService.setCommentNum(id, CollectionKey.ESSAY_KEY_COM_NUM);
            Technology strategy = technologyService.selectStrategyById(id);
            //组装消息
            String msgcontent = "<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+"</cite></a>评论了您:"+
                    "<a href='/front/strategydetail?id="+strategy.getId()+"&detailId=+"+strategy.getDetailId()+"'><cite>"+strategy.getTitle()+"</cite></a>";
            //发送消息
            if(strategy.getUserId() != user.getId()){
                //作者本人发送评论不需要发送通知
                messageService.sendRemind(id,Constants.STRATEGY_MSG,Constants.COM_MSG,user.getId(),msgcontent);
            }
            TechnologyComment strategyComment = new TechnologyComment();
            strategyComment.setTechDeId(strategy.getDetailId());
            strategyComment.setContent(content);
            strategyComment.setUserId(user.getId());
            strategyComment.setDate(new Date());
            // 插入评论
            commonService.insertStrategyComment(strategyComment);
            return Response.success("发表成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    @PostMapping("/repstracomment")
    @ResponseBody
    @Transactional
    public Response<String> repstracomment(HttpSession session,int straId,int id,String content){
        User user = (User)session.getAttribute("userinfo");
        TechnologyComment strategyComment = commonService.queryById(id);
        if(strategyComment == null){
            return Response.fail(CodeMsg.FAIL);
        }
        strategyComment.setReply(content);
        //组装消息
        Technology strategy = technologyService.selectStrategyById(straId);
        String msgcontent = "来自攻略"+"<a href='/front/strategydetail?id="+strategy.getId()+"&detailId=+"+strategy.getDetailId()+"'><cite>"+strategy.getTitle()+"</cite></a>"+"回复您："+content;
        //发送消息
        messageService.sendMsg(user.getId(),strategyComment.getUserId(),msgcontent);
        commonService.updateStraComment(strategyComment);
        return Response.success("成功");
    }

    /**
     * 攻略收藏
     * @param session
     * @param straId 攻略表ID
     * @return
     */
    @PostMapping("/straCollect")
    @ResponseBody
    public Response<String> straCollect(HttpSession session,int straId){
        User user = (User)session.getAttribute("userinfo");
        if(user != null){
            redisService.addHot(straId, "3",Constants.ESSAY_HOT_NAME);//增加热度
            Technology strategy = technologyService.selectStrategyById(straId);
            //组装消息
            String msgcontent = "<a href='/userInfo?id="+user.getId()+"'><cite>"+user.getName()+"</cite></a>收藏了您的攻略:"+
                    "<a href='/front/strategydetail?id="+strategy.getId()+"&detailId=+"+strategy.getDetailId()+"'><cite>"+strategy.getTitle()+"</cite></a>";
            //发送消息
            messageService.sendRemind(straId,Constants.STRATEGY_MSG,Constants.COLLECT_MSG,user.getId(),msgcontent);
            Collect collect = new Collect();
            collect.setDate(new Date());
            collect.setProId(straId);
            collect.setType((byte)1);
            collect.setUserId(user.getId());
            collectMapper.insertSelective(collect);
            //收藏数量加一
            redisService.setCollectNum(straId,CollectionKey.ESSAY_KEY_COL_NUM);
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

}
