package com.example.technologyforum.web.mapper;

import com.example.technologyforum.web.pojo.TechnologyDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TechnologyDetail record);

    int insertSelective(TechnologyDetail record);

    TechnologyDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnologyDetail record);

    int updateByPrimaryKeyWithBLOBs(TechnologyDetail record);
}