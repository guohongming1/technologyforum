package com.example.technologyforum.config;

import com.example.technologyforum.web.mapper.SysParamMapper;
import com.example.technologyforum.web.pojo.SysParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述：初始化系统参数
 * create by 小七 on 2021/4/17 14:47
 */
@Component
public class SysParamCache {
    // 保存系统参数
    public static Map<String, String> SYS = new HashMap<String, String>();
    @Autowired
    private SysParamMapper sysParamMapper;
    @PostConstruct
    public void init(){
        System.out.println("系统启动中>>>>加载系统参数>>>>开始");
        List<SysParam> codeList = sysParamMapper.selectAll();
        for (SysParam code : codeList) {
            SYS.put(code.getParamName(), code.getParamValue());
        }
        System.out.println("系统启动中>>>>加载系统参数>>>>结束");
    }

    public void update(){
        System.out.println("系统启动中>>>>加载系统参数>>>>开始");
        List<SysParam> codeList = sysParamMapper.selectAll();
        for (SysParam code : codeList) {
            SYS.put(code.getParamName(), code.getParamValue());
        }
        System.out.println("系统启动中>>>>加载系统参数>>>>结束");
    }

    public static String getParam(String name){
        if(name==null){
            return null;
        }
        return SYS.get(name);
    }
}
