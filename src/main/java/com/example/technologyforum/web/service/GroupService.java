package com.example.technologyforum.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.NewTopicDTO;
import com.example.technologyforum.web.pojo.*;

import java.util.List;


public interface GroupService {
    Response<String> createGroup(Group group);

    Response<String> createTopic(NewTopicDTO topicDTO);

    Boolean checkGroupMById(int groupId, int userId);

    int updateTravelGroupById(Group group);

    List<Topic> selectPageVo(int limit, int page, String address);

    List<Group> selectPageVoGroup(int limit, int page);

    List<Group> selectPageVoGroupWithFlag(int limit, int page, Byte flag);

    int getGroupCount(QueryWrapper<Group> query);

    Topic queryTopicById(int id);

    TopicDetail queryTopicDetail(int id);

    List<GroupType> selectList(QueryWrapper<GroupType> query);

    List<Group> selectTravelGroup(QueryWrapper<Group> query);

    List<Group> selectJoinGroup(int userId);

    List<Topic> selectColGroup(int userId);

    List<Topic> selectUserTopic(int userId);

    List<Topic> queryListTopic(QueryWrapper<Topic> query);

    Group queryTravelGroupById(int id);

    List<Topic> queryTopicByGroupId(int id);

    int getTopicCount(QueryWrapper<Topic> query);

    int getCountMember(QueryWrapper<GroupMember> queryWrapper);
    int joinGroup(int userId, int groupId);
    List<TopicComment> selectTopicCommemnt(int topicDetailId);

    int createTopicComment(int topicId, int userId, String content);

    int delTopicComment(int id, int userId);

    int outGroup(int userId, int groupId);

    int delColTopic(int userId, int topicId);

    int disGroup(int userId, int groupId);

    int delTopic(int userId, int topicId);

    Topic queryTopic(int id);
}
