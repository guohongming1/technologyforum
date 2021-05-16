package com.example.technologyforum.common.init;

import com.example.technologyforum.web.service.ILuceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 郭红明
 * @version 1.0
 * @Date: 2020/5/28
 */
@Component
@Order(value = 1)
public class ProductRunner implements ApplicationRunner {

    @Autowired
    private ILuceneService service;

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        /**
         * 启动后将同步索引,并创建index
         */
        service.synProductCreatIndex();
    }
}
