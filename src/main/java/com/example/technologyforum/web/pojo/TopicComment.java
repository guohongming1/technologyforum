package com.example.technologyforum.web.pojo;

import java.util.Date;

public class TopicComment {
    private Integer id;

    private Integer toDeId;

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

    public Integer getToDeId() {
        return toDeId;
    }

    public void setToDeId(Integer toDeId) {
        this.toDeId = toDeId;
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