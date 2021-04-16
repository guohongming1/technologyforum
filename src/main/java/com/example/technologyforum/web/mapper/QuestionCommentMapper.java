package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.QuestionComment;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionCommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(QuestionComment record);

    int insertSelective(QuestionComment record);

    QuestionComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(QuestionComment record);

    int updateByPrimaryKey(QuestionComment record);
}