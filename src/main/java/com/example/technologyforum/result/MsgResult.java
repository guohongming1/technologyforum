package com.example.technologyforum.result;

import java.io.Serializable;
import java.util.Date;

public class MsgResult implements Serializable {
    private static final long serialVersionUID = 5950257893872100115L;
    private int userNotifyId;
    private int id; // notify的id
    private String content;

    private Integer type;

    private Integer target;

    private Integer targetType;

    private Integer sender;

    private String action;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserNotifyId() {
        return userNotifyId;
    }

    public void setUserNotifyId(int userNotifyId) {
        this.userNotifyId = userNotifyId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
