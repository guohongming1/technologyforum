package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.Question;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKey(Question record);
}