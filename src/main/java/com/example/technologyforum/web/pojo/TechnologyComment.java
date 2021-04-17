package com.example.technologyforum.web.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.Date;

public class TechnologyComment {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer techDeId;

    private Integer userId;

    private String content;

    private String reply;

    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTechDeId() {
        return techDeId;
    }

    public void setTechDeId(Integer techDeId) {
        this.techDeId = techDeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}