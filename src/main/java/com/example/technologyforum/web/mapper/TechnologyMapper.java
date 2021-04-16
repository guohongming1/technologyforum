package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.web.pojo.Technology;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnologyMapper extends BaseMapper<Technology> {
    int deleteByPrimaryKey(Integer id);

    int insert(Technology record);

    int insertSelective(Technology record);

    Technology selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Technology record);

    int updateByPrimaryKey(Technology record);

    IPage<Technology> selectPageVo(Page page, @Param("address")String address);

    IPage<Technology> admidSelectPageVo(Page page,@Param("title")String title);
}