package com.example.technologyforum.web.service;

import com.example.technologyforum.web.pojo.Technology;
import com.example.technologyforum.web.pojo.TechnologyDetail;

import java.util.List;

/**
 * 功能描述： 技术帖子服务接口
 * create by 小七 on 2021/4/14 22:42
 */
public interface ITechnologyService {

    /**
     * 后台获取所有技术帖子
     * @param limit
     * @param page
     * @param title
     * @return
     */
    List<Technology> adminGetList(int limit, int page, String title);

    public int getCount();

    /**
     * 获取帖子明细
     * @param detailId
     * @return
     */
    TechnologyDetail getDetailById(int detailId);
}
