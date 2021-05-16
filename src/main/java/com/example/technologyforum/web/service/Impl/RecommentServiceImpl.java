package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.result.StrategyDTO;
import com.example.technologyforum.web.dto.TechnologyDTO;
import com.example.technologyforum.web.mapper.TechnologyMapper;
import com.example.technologyforum.web.mapper.TechnologyRecomdMapper;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TechnologyRecomd;
import com.example.technologyforum.web.service.RecommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 郭红明
 * @version 1.0
 * @Date: 2020/4/20
 */
@Service
public class RecommentServiceImpl implements RecommentService {
    @Autowired
    private TechnologyRecomdMapper technologyRecomdMapper;

    @Autowired
    private TechnologyMapper technologyMapper;


    @Override
    public List<TechnologyRecomd> getList(int limit, int page){
        // 分页
        Page<TechnologyRecomd> pageHelper = new Page<>();
        pageHelper.setSize(limit);
        pageHelper.setCurrent(page);

        IPage<TechnologyRecomd> pageVo = null;

        pageVo = technologyRecomdMapper.selectPageVo(pageHelper);
        return pageVo.getRecords();
    }

    @Override
    public List<TechnologyDTO> top(List<Integer> recommends, boolean topTen){
        if (recommends == null || recommends.size() == 0) {
            return null;
        }
        List<TechnologyDTO> list = new ArrayList<>();
        for(Integer id:recommends){
            Technology technology = technologyMapper.selectById(id);
            if(technology != null){
                TechnologyDTO dto = new TechnologyDTO();
                BeanUtils.copyProperties(technology,dto);
                list.add(dto);
            }
        }
        return list;
    }
}
