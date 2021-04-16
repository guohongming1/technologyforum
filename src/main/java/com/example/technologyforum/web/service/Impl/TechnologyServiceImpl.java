package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.web.mapper.TechnologyDetailMapper;
import com.example.technologyforum.web.mapper.TechnologyMapper;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TechnologyDetail;
import com.example.technologyforum.web.service.ITechnologyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 功能描述：
 * create by 小七 on 2021/4/14 22:45
 */
public class TechnologyServiceImpl implements ITechnologyService {

    @Autowired
    private TechnologyMapper technologyMapper;

    @Autowired
    private TechnologyDetailMapper technologyDetailMapper;
    @Override
    public List<Technology> adminGetList(int limit, int page, String title) {
        Page<Technology> pageHelper = new Page<>();
        pageHelper.setCurrent(page);
        pageHelper.setSize(limit);
        IPage<Technology> pageVo = technologyMapper.admidSelectPageVo(pageHelper,title);
        return pageVo.getRecords();
    }

    /**
     * 获取帖子数量
     * @return
     */
    public int getCount(){
        return technologyMapper.selectCount(new QueryWrapper<Technology>());
    }

    public TechnologyDetail getDetailById(int detailId){
        return technologyDetailMapper.selectByPrimaryKey(detailId);
    }
}
