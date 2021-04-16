package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.Collect;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Collect record);

    int insertSelective(Collect record);

    Collect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Collect record);

    int updateByPrimaryKey(Collect record);
}