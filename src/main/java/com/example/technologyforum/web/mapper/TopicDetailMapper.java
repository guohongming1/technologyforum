package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TopicDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicDetailMapper extends BaseMapper<TopicDetail> {
    int deleteByPrimaryKey(Integer id);

    int insert(TopicDetail record);

    int insertSelective(TopicDetail record);

    TopicDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TopicDetail record);

    int updateByPrimaryKeyWithBLOBs(TopicDetail record);

    int updateByPrimaryKey(TopicDetail record);
}