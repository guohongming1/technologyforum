package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.Cron;
import org.springframework.stereotype.Repository;

@Repository
public interface CronMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cron record);

    int insertSelective(Cron record);

    Cron selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cron record);

    int updateByPrimaryKey(Cron record);
}