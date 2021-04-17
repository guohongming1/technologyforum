package com.example.technologyforum.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.technologyforum.web.pojo.SysParam;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysParamMapper extends BaseMapper<SysParam> {
    int deleteByPrimaryKey(Integer id);

    int insert(SysParam record);

    int insertSelective(SysParam record);

    SysParam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysParam record);

    int updateByPrimaryKey(SysParam record);

    @Select("select * from SYS_PARAM")
    List<SysParam> selectAll();
}