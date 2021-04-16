package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.TopicComment;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicCommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TopicComment record);

    int insertSelective(TopicComment record);

    TopicComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TopicComment record);

    int updateByPrimaryKey(TopicComment record);
}