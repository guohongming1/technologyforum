package com.example.technologyforum.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.pojo.Question;
import com.example.technologyforum.web.pojo.QuestionComment;

import java.util.List;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2021/4/16
 */
public interface QuestionService {
    Response<String> newQuestion(Question question);
    List<Question> getQuestionListByUserId(int id);
    List<Question> queryList(QueryWrapper<Question> query);
    Question selectById(int id);
    List<Question> selectPageVo(int limit,int page,String title,Byte flag);
    int getCount(QueryWrapper<Question> query);
    List<QuestionComment> getQuestionComment(int questionId);
    int delQuestionCommment(int id,int userId);
    int createQuestionCommment(int userId,int questionId,String content);
    int acptComment(int Id,int questionId,int userId);
    int updateQuestion(Question question);
    int delQuestionBatch(List<Integer> ids);
}
