package com.example.technologyforum.web.pojo;

public class GroupMember {
    private Integer id;

    private Integer groupId;

    private Integer userId;

    private Byte msgFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(Byte msgFlag) {
        this.msgFlag = msgFlag;
    }
}