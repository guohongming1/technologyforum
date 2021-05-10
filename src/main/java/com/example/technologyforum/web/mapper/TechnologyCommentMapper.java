package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.web.pojo.TechnologyComment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyCommentMapper extends BaseMapper<TechnologyComment> {
    int deleteByPrimaryKey(Integer id);

    int insert(TechnologyComment record);

    int insertSelective(TechnologyComment record);

    TechnologyComment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnologyComment record);

    int updateByPrimaryKey(TechnologyComment record);

    IPage<TechnologyComment> selectPageVo(Page page, @Param("detailId") int detailId);
}