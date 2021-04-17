package com.example.technologyforum.web.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class SysParam {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String paramValue;

    private String paramName;

    private String paramText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamText() {
        return paramText;
    }

    public void setParamText(String paramText) {
        this.paramText = paramText;
    }
}