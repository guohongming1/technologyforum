package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.Topic;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Topic record);

    int insertSelective(Topic record);

    Topic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Topic record);

    int updateByPrimaryKey(Topic record);
}