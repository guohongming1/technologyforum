package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.technologyforum.web.pojo.TechnologyComment;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyCommentMapper extends BaseMapper<TechnologyComment> {
    int deleteByPrimaryKey(Integer id);

    int insert(TechnologyComment record);

    int insertSelective(TechnologyComment record);

    TechnologyComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnologyComment record);

    int updateByPrimaryKey(TechnologyComment record);
}