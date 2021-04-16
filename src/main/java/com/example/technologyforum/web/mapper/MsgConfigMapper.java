package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.MsgConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface MsgConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MsgConfig record);

    int insertSelective(MsgConfig record);

    MsgConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MsgConfig record);

    int updateByPrimaryKey(MsgConfig record);
}