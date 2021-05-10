package com.example.technologyforum.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.cache.CollectionKey;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.util.PageInfo;
import com.example.technologyforum.util.PageQuery;
import com.example.technologyforum.web.dto.*;
import com.example.technologyforum.web.mapper.UserMapper;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.QuestionComment;
import com.example.technologyforum.web.pojo.User;
import com.example.technologyforum.web.service.ILuceneService;
import com.example.technologyforum.web.service.Impl.CommonServiceImpl;
import com.example.technologyforum.web.service.Impl.RedisService;
import com.example.technologyforum.web.service.QuestionService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private CommonServiceImpl commonService;


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
}
