package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.TechnologyDTO;
import com.example.technologyforum.web.mapper.TechnologyDetailMapper;
import com.example.technologyforum.web.mapper.TechnologyMapper;
import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TechnologyDetail;
import com.example.technologyforum.web.service.ITechnologyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 功能描述：
 * create by 小七 on 2021/4/14 22:45
 */
@Service
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

    @Override
    @Transactional
    public Response<String> createStrategy(Technology strategy, String content) {
        TechnologyDetail strategyDetail = new TechnologyDetail();
        strategyDetail.setContent(content);
        technologyDetailMapper.insertSelective(strategyDetail);
        strategy.setDetailId(strategyDetail.getId());
        strategy.setDelFlag((byte)0);
        return Response.success("发表成功");
    }

    @Override
    public Response<String> updateStrategy(Technology strategy, String content) {
        // 查询用户是否有未发表的攻略
        QueryWrapper<Technology> query = new QueryWrapper<>();
        query.eq("user_id",strategy.getUserId());
        query.eq("push_flag", Constants.PUSH_NO);
        Technology recod = technologyMapper.selectOne(query);
        if(recod != null){
            strategy.setDetailId(recod.getDetailId());
            strategy.setId(recod.getId());
            technologyMapper.updateByPrimaryKeySelective(strategy);
            TechnologyDetail detail = technologyDetailMapper.selectByPrimaryKey(recod.getDetailId());
            detail.setContent(content);
            technologyDetailMapper.updateByPrimaryKeySelective(detail);
            return Response.success("成功");
        }else{
            TechnologyDetail strategyDetail = new TechnologyDetail();
            strategyDetail.setContent(content);
            technologyDetailMapper.insertSelective(strategyDetail);
            strategy.setDetailId(strategyDetail.getId());
            strategy.setDelFlag((byte)0);
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.ESSAY_PUSH_FAIL);
    }

    /**
     * 根据id获取攻略
     * @param id
     * @return
     */
    @Override
    public Technology selectStrategyById(int id) {
        QueryWrapper<Technology> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("del_flag",(byte)0);
        List<Technology> list = technologyMapper.selectList(queryWrapper);
        return (list==null||list.size()==0) ? null:list.get(0);
    }

    /**
     * 获取未发表攻略
     * @param userId
     * @return
     */
    public Response<TechnologyDTO> pullStrategy(int userId){
        QueryWrapper<Technology> query = new QueryWrapper<>();
        query.eq("user_id",userId);
        query.eq("push_flag", Constants.PUSH_NO);
        query.eq("del_flag",(byte)0);
        Technology strategy = technologyMapper.selectOne(query);
        if(null == strategy){
            return Response.fail(CodeMsg.FAIL);
        }
        TechnologyDetail strategyDetail = null;
        if(strategy.getDetailId() != null){
            strategyDetail = technologyDetailMapper.selectByPrimaryKey(strategy.getDetailId());
        }
        TechnologyDTO result = new TechnologyDTO();
        BeanUtils.copyProperties(strategy,result,"date");
        result.setContent(strategyDetail.getContent());
        return Response.success(result);
    }
}
