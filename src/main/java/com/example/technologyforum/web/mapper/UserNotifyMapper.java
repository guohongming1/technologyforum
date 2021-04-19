package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.technologyforum.web.pojo.UserNotify;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotifyMapper extends BaseMapper<UserNotify> {
    int deleteByPrimaryKey(Integer id);

    int insert(UserNotify record);

    int insertSelective(UserNotify record);

    UserNotify selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserNotify record);

    int updateByPrimaryKey(UserNotify record);

    @Select("select count(*) from user_notify where user_id=#{user_id} and readflag=1")
    int getMsgCountByUserId(@Param("user_id") int id);

    @Select("select * from user_notify where user_id=#{user_id} and readflag=#{readflag} order by create_time desc")
    List<UserNotify> queryMsg(@Param("user_id") int id, @Param("readflag")Byte readflag);
}