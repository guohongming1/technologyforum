package com.example.technologyforum.web.dto;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 小七
 * @version 1.0
 * @Date: 2021/4/24
 */
public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 4656353395488083566L;
    private Integer id;

    private String title;

    private String tags;

    private Integer userId;

    private String userName;

    private String address;

    private String content;

    private String imgUrl;

    private Integer commentNum;

    private Integer hotNum;

    private Byte flag;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;


    public Integer getHotNum() {
        return hotNum;
    }

    public void setHotNum(Integer hotNum) {
        this.hotNum = hotNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
