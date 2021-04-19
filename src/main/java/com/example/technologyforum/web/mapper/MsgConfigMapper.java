package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.technologyforum.web.pojo.MsgConfig;
import org.springframework.stereotype.Repository;

@Repository
public interface MsgConfigMapper extends BaseMapper<MsgConfig> {
    int deleteByPrimaryKey(Integer id);

    int insert(MsgConfig record);

    int insertSelective(MsgConfig record);

    MsgConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MsgConfig record);

    int updateByPrimaryKey(MsgConfig record);
}