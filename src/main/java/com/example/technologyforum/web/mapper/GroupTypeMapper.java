package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.GroupType;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupType record);

    int insertSelective(GroupType record);

    GroupType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupType record);

    int updateByPrimaryKey(GroupType record);
}