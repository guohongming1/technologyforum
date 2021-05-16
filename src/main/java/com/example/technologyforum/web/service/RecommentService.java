package com.example.technologyforum.web.service;

import com.example.technologyforum.web.dto.TechnologyDTO;
import com.example.technologyforum.web.pojo.TechnologyRecomd;

import java.util.List;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2021/4/20
 */
public interface RecommentService {

   public List<TechnologyRecomd> getList(int limit, int page);

   List<TechnologyDTO> top(List<Integer> recommends, boolean topTen);
}
