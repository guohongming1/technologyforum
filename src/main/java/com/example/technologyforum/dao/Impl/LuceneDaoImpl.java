package com.example.technologyforum.dao.Impl;

import com.example.technologyforum.dao.ILuceneDao;
import com.example.technologyforum.util.PageInfo;
import com.example.technologyforum.util.PageQuery;
import com.example.technologyforum.web.dto.SearchResultDTO;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2020/5/28
 */
@Repository(value = "luceneDao")
public class LuceneDaoImpl implements ILuceneDao {

    @Autowired(required = false)
    private IndexWriter indexWriter;

    @Autowired
    private Analyzer analyzer;

    @Autowired
    private SearcherManager searcherManager;

    @Override
    public void createProductIndex(List<SearchResultDTO> productList) throws IOException {
        List<Document> docs = new ArrayList<Document>();
        for (SearchResultDTO p : productList) {
            Document doc = new Document();
            doc.add(new StringField("id", p.getId()+"", Field.Store.YES));
            doc.add(new StringField("type", p.getType()+"", Field.Store.YES));
            doc.add(new TextField("title", p.getTitle(), Field.Store.YES));
            if(p.getTags() == null){
                doc.add(new StringField("tags", "NULL", Field.Store.YES));
            }else{
                doc.add(new StringField("tags", p.getTags(), Field.Store.YES));
            }
            if(p.getAddress() == null){
                doc.add(new TextField("address","NULL", Field.Store.YES));
            }else{
                doc.add(new TextField("address", p.getAddress(), Field.Store.YES));
            }
            docs.add(doc);
        }
        indexWriter.addDocuments(docs);
        indexWriter.commit();
    }

    @Override
    public PageQuery<SearchResultDTO> searchProduct(PageQuery<SearchResultDTO> pageQuery) throws IOException, ParseException, InvalidTokenOffsetsException {
        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();
        SearchResultDTO params = pageQuery.getParams();
        Map<String, String> queryParam = pageQuery.getQueryParam();
        Builder builder = new Builder();
        Sort sort = new Sort();
        // 排序规则
        com.example.technologyforum.web.pojo.Sort sort1 = pageQuery.getSort();
        if (sort1 != null && sort1.getOrder() != null) {
            if ("ASC".equals((sort1.getOrder()).toUpperCase())) {
                sort.setSort(new SortField(sort1.getField(), SortField.Type.FLOAT, false));
            } else if ("DESC".equals((sort1.getOrder()).toUpperCase())) {
                sort.setSort(new SortField(sort1.getField(), SortField.Type.FLOAT, true));
            }
        }
        // 模糊匹配,匹配词
        String keyStr = queryParam.get("searchKeyStr");
        if (keyStr != null) {
            // 输入空格,不进行模糊查询
            if (!"".equals(keyStr.replaceAll(" ", ""))) {
                builder.add(new QueryParser("title", analyzer).parse(keyStr), Occur.SHOULD);
                builder.add(new QueryParser("tags", analyzer).parse(keyStr), Occur.SHOULD);
                builder.add(new QueryParser("address", analyzer).parse(keyStr), Occur.SHOULD);
            }
        }

        // 精确查询
        if (params.getAddress() != null) {
            builder.add(new TermQuery(new Term("address", params.getAddress())), Occur.MUST);
        }
        PageInfo pageInfo = pageQuery.getPageInfo();
        TopDocs topDocs = indexSearcher.search(builder.build(), pageInfo.getPageNum() * pageInfo.getPageSize(), sort);
        QueryParser queryParser = new QueryParser("title", analyzer);
        queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
        Query query = queryParser.parse(keyStr);
        // 做高亮处理
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        int n = (pageInfo.getPageNum()-1) * pageInfo.getPageSize();
        pageInfo.setTotal(topDocs.totalHits);
        ScoreDoc[] hits = topDocs.scoreDocs;
        if(n>=hits.length){
            n=0;
        }
        List<SearchResultDTO> pList = new ArrayList<SearchResultDTO>();
        for (int i = n; i < hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            SearchResultDTO product = new SearchResultDTO();
            product.setId(Integer.parseInt(doc.get("id")));
            //做高亮处理
            StringReader reader1 = new StringReader(doc.get("title"));
            TokenStream ts = analyzer.tokenStream("title", reader1);
            String str1 = highlighter.getBestFragment(ts,doc.get("title"));
            reader1.close();
            ts.reset();
            if(null != str1){
                product.setTitle(str1);
            }else{
                product.setTitle(doc.get("title"));
            }
            if(doc.get("address")!=null && !"NULL".equals(doc.get("address"))){
                StringReader reader2 = new StringReader(doc.get("address"));
                TokenStream ts2 = analyzer.tokenStream("address",reader2);
                String str2 = highlighter.getBestFragment(ts2,doc.get("address"));
                reader1.close();
                ts2.reset();
                if(null != str2 ){
                    product.setAddress(str2);
                }else{
                    product.setAddress(doc.get("address"));
                }
            }
            product.setTags(doc.get("tags"));
            product.setType(Integer.parseInt(doc.get("type")));
            pList.add(product);
        }
        pageQuery.setResults(pList);
        return pageQuery;
    }

    @Override
    public void addProductIndex(SearchResultDTO product) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("id", product.getId()+"", Field.Store.YES));
        doc.add(new StringField("type", product.getType()+"", Field.Store.YES));
        doc.add(new TextField("title", product.getTitle(), Field.Store.YES));
        doc.add(new StringField("tags", product.getTags(), Field.Store.YES));;

        doc.add(new TextField("address", product.getAddress(), Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.commit();
    }

    @Override
    public void deleteProductIndexById(String id) throws IOException {
        indexWriter.deleteDocuments(new Term("id",id));
        indexWriter.commit();
    }


}
