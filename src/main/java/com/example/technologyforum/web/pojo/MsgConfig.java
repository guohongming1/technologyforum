package com.example.technologyforum.web.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class MsgConfig {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer targetId;

    private String type;

    private Byte collect;

    private Byte comment;

    private Byte reserve1;

    private Byte reserve2;

    private Byte reserve3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Byte getCollect() {
        return collect;
    }

    public void setCollect(Byte collect) {
        this.collect = collect;
    }

    public Byte getComment() {
        return comment;
    }

    public void setComment(Byte comment) {
        this.comment = comment;
    }

    public Byte getReserve1() {
        return reserve1;
    }

    public void setReserve1(Byte reserve1) {
        this.reserve1 = reserve1;
    }

    public Byte getReserve2() {
        return reserve2;
    }

    public void setReserve2(Byte reserve2) {
        this.reserve2 = reserve2;
    }

    public Byte getReserve3() {
        return reserve3;
    }

    public void setReserve3(Byte reserve3) {
        this.reserve3 = reserve3;
    }
}