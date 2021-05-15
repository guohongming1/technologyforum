package com.example.technologyforum.web.dto;

import java.io.Serializable;

public class TableResultDTO<T> implements Serializable {

    private static final long serialVersionUID = -8226382031145206579L;

    public TableResultDTO(int code, String msg, int count, T data){
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    /**
     * 返回代码，200：成功
     */
    private int code;
    /**
     * 服务异常返回响应
     */
    private String msg;
    /**
     * 数据长度
     */
    private int count;
    /**
     * 数据
     */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
