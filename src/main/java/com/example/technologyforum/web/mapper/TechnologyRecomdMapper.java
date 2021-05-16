package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.web.pojo.TechnologyRecomd;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyRecomdMapper extends BaseMapper<TechnologyRecomd> {
    int deleteByPrimaryKey(Integer id);

    int insert(TechnologyRecomd record);

    int insertSelective(TechnologyRecomd record);

    TechnologyRecomd selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnologyRecomd record);

    int updateByPrimaryKey(TechnologyRecomd record);

    /**
     * 分页搜索查询
     *
     * @param page
     * @return
     */
    IPage<TechnologyRecomd> selectPageVo(Page page);
    int delCommentByStrategyId(int id);
}