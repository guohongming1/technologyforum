package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.UserNotify;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotifyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserNotify record);

    int insertSelective(UserNotify record);

    UserNotify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserNotify record);

    int updateByPrimaryKey(UserNotify record);
}