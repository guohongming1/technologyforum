package com.example.technologyforum.web.service;

import com.example.technologyforum.util.PageQuery;
import com.example.technologyforum.web.dto.SearchResultDTO;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.io.IOException;


public interface ILuceneService {
    /**
     * 启动后将同步Product表,并创建index
     * @throws IOException
     */
    public void synProductCreatIndex() throws IOException;

    public PageQuery<SearchResultDTO> searchProduct(PageQuery<SearchResultDTO> pageQuery) throws IOException, ParseException, InvalidTokenOffsetsException;
}
