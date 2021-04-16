package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.TechnologyRecomd;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyRecomdMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TechnologyRecomd record);

    int insertSelective(TechnologyRecomd record);

    TechnologyRecomd selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnologyRecomd record);

    int updateByPrimaryKey(TechnologyRecomd record);
}