package com.example.technologyforum.web.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;


public class TopicCommentDTO implements Serializable {

    private static final long serialVersionUID = -6041512911733652484L;

    private Integer id;

    private Integer toDeId;

    private Integer userId;

    private String content;

    private String reply;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    private String userName;
    private String userImg;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }
}
