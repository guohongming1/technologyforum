package com.example.technologyforum.web.dto;

import java.io.Serializable;

public class NewTopicDTO implements Serializable {
    private static final long serialVersionUID = -3838808626385508118L;
    private String title;
    private String tags;
    private String content;
    private int userId;
    private String GroupId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
