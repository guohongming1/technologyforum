package com.example.technologyforum.web.service.Impl;

import com.example.technologyforum.util.PageQuery;
import com.example.technologyforum.web.dto.SearchResultDTO;
import com.example.technologyforum.web.service.ILuceneService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2021/5/28
 */
@Service
public class LuceneServiceImpl implements ILuceneService {


    @Override
    public void synProductCreatIndex() throws IOException {

    }

    @Override
    public PageQuery<SearchResultDTO> searchProduct(PageQuery<SearchResultDTO> pageQuery) throws IOException, ParseException, InvalidTokenOffsetsException {
        return null;
    }
}
