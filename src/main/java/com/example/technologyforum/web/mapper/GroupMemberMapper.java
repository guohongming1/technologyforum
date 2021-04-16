package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.GroupMember;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMember record);

    int insertSelective(GroupMember record);

    GroupMember selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupMember record);

    int updateByPrimaryKey(GroupMember record);
}