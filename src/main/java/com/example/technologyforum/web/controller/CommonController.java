package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.util.PageInfo;
import com.example.technologyforum.util.PageQuery;
import com.example.technologyforum.web.dto.*;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.*;
import com.example.technologyforum.web.service.*;
import com.example.technologyforum.web.service.Impl.CommonServiceImpl;
import com.example.technologyforum.web.service.Impl.RedisService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

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

    @Autowired
    private ILuceneService service;

    @Autowired
    private GroupService groupService;

    @Autowired
    private CommonServiceImpl commonService;

    @Autowired
    private RecommentService recommentService;
    @Autowired
    private ITechnologyService technologyService;


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
            listId.forEach(item->{
                Question question = questionService.selectById(item);
                if(question != null){
                    list.add(question);
                }
            });
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
     * 全局搜索相关 搜索优化 Lucene算法
     * @param limit
     * @param page
     * @param pattern
     * @return
     */
    @PostMapping("/search-list")
    @ResponseBody
    public TableResultDTO<List<SearchResultDTO>> LuceneSearchResult(int limit, int page, String pattern, String type) throws IOException, ParseException, InvalidTokenOffsetsException {
        if("ques".equals(type)){
            return searchQuestion(limit,page,pattern);
        }
        PageQuery<SearchResultDTO> pageQuery = new PageQuery<>();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("searchKeyStr",pattern);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(limit);
        pageQuery.setPageInfo(pageInfo);
        pageQuery.setQueryParam(queryParam);
        pageQuery.setParams(new SearchResultDTO());
        PageQuery<SearchResultDTO> pageResult= service.searchProduct(pageQuery);
        List<SearchResultDTO> list = pageResult.getResults();
        return new TableResultDTO<>(200, "",Integer.parseInt(String.valueOf(pageResult.getPageInfo().getTotal())) , list);
    }

    /**
     * 问答搜索
     * @param limit
     * @param page
     * @param pattern
     * @return
     */

    public TableResultDTO<List<SearchResultDTO>> searchQuestion(int limit,int page,String pattern){
        List<SearchResultDTO> list = new ArrayList<>();
        QueryWrapper<Question> questionQuery = new QueryWrapper<>();
        questionQuery.like("address",pattern);
        questionQuery.or().like("title",pattern);
        questionQuery.or().like("tags",pattern);
        List<Question> questionList = questionService.queryList(questionQuery);
        if(questionList != null &&  questionList.size()>0){
            questionList.forEach(item->{
                SearchResultDTO dto = new SearchResultDTO();
                dto.setId(item.getId());
                dto.setType(2);
                dto.setTitle(item.getTitle());
                dto.setTags(item.getTags());
                dto.setDate(item.getDate());
                list.add(dto);
            });
            list.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        }
        int start = (page - 1) * limit;
        int end = page * limit - 1;
        List<SearchResultDTO> result = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (i > list.size() - 1) {
                break;
            }
            result.add(list.get(i));
        }
        return new TableResultDTO<>(200, "", list.size(), result);
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

    /**
     * 攻略评论
     * @param detailId
     * @param page
     * @return
     */
    @PostMapping("/strcomment")
    @ResponseBody
    public Response<List<CommentDTO>> straComment(int detailId, int page){
        return commonService.straComment(detailId,page);
    }

    /**
     * 小组排名
     * @return
     */
    @PostMapping("/topGroup")
    @ResponseBody
    public Response<List<Group>> topGroup(){
        return Response.success(groupService.selectPageVoGroupWithFlag(6,1,Constants.PASS_YES));
    }

    /**
     * 最新话题
     * @param limit
     * @param page
     * @return
     */
    @PostMapping("/getnewtopic")
    @ResponseBody
    public TableResultDTO<List<Topic>> getnewtopic(int limit, int page){
        List<Topic> list = groupService.selectPageVo(limit,page,null);
        list.forEach(item->{
            Number comNum = redisService.getViewNum(item.getId(), CollectionKey.TOPIC_KEY_COM_NUM);
            if(!Objects.isNull(comNum)){
                item.setReplyNum(comNum.intValue());
            }else{
                item.setReplyNum(0);
            }
            Number readNum = redisService.getViewNum(item.getId(), CollectionKey.TOPIC_KEY_COL_NUM);
            if(!Objects.isNull(readNum)){
                item.setReadNum(readNum.intValue());
            }else{
                item.setReadNum(0);
            }
        });
        int count = groupService.getTopicCount(new QueryWrapper<Topic>());
        return  new TableResultDTO<>(200, "", count, list);
    }

    /**
     * 获取小组下的话题
     * @param id
     * @return
     */
    @PostMapping("/getgrouptopic")
    @ResponseBody
    public Response<List<Topic>> getgrouptopic(int id){
        List<Topic> list = groupService.queryTopicByGroupId(id);
        list.forEach(item->{
            Number comNum = redisService.getViewNum(item.getId(), CollectionKey.TOPIC_KEY_COM_NUM);
            if(!Objects.isNull(comNum)){
                item.setReplyNum(comNum.intValue());
            }else{
                item.setReplyNum(0);
            }
            //话题热度
            Number hotNum = redisService.getScore(Constants.TOPIC_HOT_NAME, item.getId());
            if(!Objects.isNull(hotNum)){
                item.setReadNum(hotNum.intValue());
            }else{
                item.setReadNum(0);
            }
            // tags字段存储用户名
            item.setTags(userMapper.getUserInfoByPrimaryKey(item.getUserId()).getName());
        });
        return Response.success(list);
    }

    /**
     * 获取全部小组
     * @return
     */
    @PostMapping("/getgroup")
    @ResponseBody
    public Response<Map<String,List<Group>>> getAllGroup(){
        List<GroupType> typeList = groupService.selectList(new QueryWrapper<GroupType>());
        Map<String,List<Group>> groupMap = new HashMap<>();
        if(typeList != null && typeList.size()>0){
            for(int i=0;i<typeList.size();i++){
                QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("flag",Constants.PASS_YES);
                queryWrapper.eq("type_id",typeList.get(i).getId());
                List<Group> list = groupService.selectTravelGroup(queryWrapper);
                if(list!=null && list.size()>0){
                    groupMap.put(typeList.get(i).getName(),list);
                }
            }
        }
        return Response.success(groupMap);
    }

    /**
     * 返回技术帖子推荐
     * @param limit
     * @param page
     * @return
     */
    @PostMapping("/strategy-list")
    @ResponseBody
    @Transactional
    public TableResultDTO<List<TechnologyDTO>> strategyList(int limit,int page){
        if(limit > 0 && page > 0){
            // 返回游记推荐
            List<TechnologyRecomd> list = recommentService.getList(limit,page);
            List<TechnologyDTO> result = new ArrayList<>();
            for(int i=0;i<list.size();i++){
                TechnologyDTO strategyDTO = new TechnologyDTO();
                BeanUtils.copyProperties(list.get(i),strategyDTO);
                strategyDTO.setHeadImgUrl(list.get(i).getReserve3());
                User user = userMapper.selectByPrimaryKey(list.get(i).getUserId());
                strategyDTO.setUserHeadImg(user.getImgUrl());
                strategyDTO.setUserName(user.getName());
                Number hotNum = redisService.getViewNum(list.get(i).getId(), CollectionKey.ESSAY_KEY_HOT);
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
                if(!Objects.isNull(colNum)){
                    strategyDTO.setCommentnum(comNum.intValue());
                }else{
                    strategyDTO.setCommentnum(0);
                }
                result.add(strategyDTO);
            }
            return new TableResultDTO<>(200, "", page+1, result);
        }
        return new TableResultDTO<>(500, "参数错误", 0, null);
    }

    /**
     * 获取热度前十的问答
     */
    @PostMapping("/hotquestion-list")
    @ResponseBody
    public Response<List<Question>> topQuesion(){
        List<Integer> listId = redisService.getTopNum(Constants.QUESTION_HOT_NAME);
        List<Question> result = new ArrayList<>();
        if(listId != null && listId.size() >0){
            listId.forEach(id->{
                Question question = questionService.selectById(id);
                if(question != null && question.getImgUrl()==null){
                    User user = userMapper.getUserInfoByPrimaryKey(question.getUserId());
                    question.setImgUrl(user.getImgUrl());
                    result.add(question);
                }
            });
        }
        if(result != null && result.size()>0){
            return Response.success(result);
        }
        return Response.success(null);
    }

    /**
     * 获取热度前十的话题
     * @return
     */
    @PostMapping("/hottopic-list")
    @ResponseBody
    public Response<List<Topic>> topTopic(){
        List<Integer> listId = redisService.getTopNum(Constants.TOPIC_HOT_NAME);
        List<Topic> result = new ArrayList<>();
        if(listId != null && listId.size() >0){
            listId.forEach(id->{
                Topic topic = groupService.queryTopicById(id);
                if(topic != null){
                    User user = userMapper.getUserInfoByPrimaryKey(topic.getUserId());
                    topic.setTags(user.getImgUrl());
                    result.add(topic);
                }
            });
        }
        return Response.success(result);
    }

    /**
     * 获取热度前十的攻略
     * @return
     */
    @PostMapping("/hotstra-list")
    @ResponseBody
    public Response<List<Technology>> topRecommendTenList() {
        List<Integer> listId = redisService.getTopNum(Constants.ESSAY_HOT_NAME);
        List<Technology> result = new ArrayList<>();
        if(listId != null && listId.size()>0){
            listId.forEach(id-> {
                Technology technology = technologyService.selectStrategyById(id);
                if(technology != null && technology.getPushFlag() == Constants.PUSH_YES){
                    result.add(technology);
                }
            });
        }
        return Response.success(result);
    }

    /**
     * 最新攻略
     * @return
     */
    @PostMapping("/besnew_strategy")
    @ResponseBody
    public Response<List<Technology>> bestNewStrategy() {
        return Response.success(technologyService.getList(10,1));
    }

    /**
     * 用户小组
     * @param session
     * @return
     */
    @PostMapping("/around_strategy")
    @ResponseBody
    public Response<List<Group>> aroundStrategy(HttpSession session) {
        return Response.success(groupService.getList(1,10));
    }

    /**
     * 获取话题评论
     * @param topicId
     * @return
     */
    @PostMapping("/getTopicComment")
    @ResponseBody
    public Response<List<TopicCommentDTO>> getTopicComment(int topicId){
        return Response.success(this.setTopicCommentData(groupService.selectTopicCommemnt(topicId)));
    }
    /**
     *  组装前端数据
     * @param list
     * @return
     */
    public List<TopicCommentDTO> setTopicCommentData(List<TopicComment> list){
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

    /**
     * 用户主页
     * @param id
     * @return
     */
    @PostMapping("/userslive")
    @ResponseBody
    public Response<Map<String,Object>> userslive(int id){
        Map<String,Object> result = new HashMap<>();
        if(!Objects.isNull(id)){
            List<Technology> strategyList = technologyService.getStrategyByUserId(id);
            result.put("strategy",strategyList);
            List<Question> questionList = questionService.getQuestionListByUserId(id);
            result.put("question",questionList);
            return Response.success(result);
        }
        return Response.fail(CodeMsg.FAIL);
    }
}
