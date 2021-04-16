package com.example.technologyforum.web.pojo;

import java.util.Date;

public class UserNotify {
    private Integer id;

    private Byte readflag;

    private Integer userId;

    private Integer notifyId;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getReadflag() {
        return readflag;
    }

    public void setReadflag(Byte readflag) {
        this.readflag = readflag;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(Integer notifyId) {
        this.notifyId = notifyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}