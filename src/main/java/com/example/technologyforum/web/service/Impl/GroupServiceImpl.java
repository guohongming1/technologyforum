package com.example.technologyforum.web.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.technologyforum.constants.Constants;
import com.example.technologyforum.result.CodeMsg;
import com.example.technologyforum.result.Response;
import com.example.technologyforum.web.dto.NewTopicDTO;
import com.example.technologyforum.web.mapper.*;
import com.example.technologyforum.web.pojo.*;
import com.example.technologyforum.web.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicDetailMapper topicDetailMapper;

    @Autowired
    private GroupTypeMapper groupTypeMapper;

    @Autowired
    private TopicCommentMapper topicCommentMapper;

    @Autowired
    private CommonServiceImpl commonService;

    /**
     * 创建小组
     * @param group
     * @return
     */
    @Override
    public Response<String> createGroup(Group group) {
        if(groupMapper.insertSelective(group)>0){
            return Response.success("成功");
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     * 创建话题
     * @param topicDTO
     * @return
     */
    @Override
    @Transactional
    public Response<String> createTopic(NewTopicDTO topicDTO) {
        if(this.checkGroupMById(Integer.parseInt(topicDTO.getGroupId()),topicDTO.getUserId())){
            Group travelGroup = groupMapper.selectByPrimaryKey(Integer.parseInt(topicDTO.getGroupId()));
            if(travelGroup != null){
                travelGroup.setTopicNum(travelGroup.getTopicNum()==null? 0:travelGroup.getTopicNum()+1);
                //更新小组话题数量
                groupMapper.updateByPrimaryKeySelective(Group); // TODO 最好更新指定字段
                // 话题明细
                TopicDetail detail = new TopicDetail();
                detail.setUserId(topicDTO.getUserId());
                detail.setContent(topicDTO.getContent());
                detail.setTitle(topicDTO.getTitle());
                detail.setDate(new Date());
                topicDetailMapper.insertSelective(detail);
                //话题表
                Topic topic = new Topic();
                topic.setTitle(topicDTO.getTitle());
                topic.setTags(topicDTO.getTags());
                topic.setGroupId(travelGroup.getId());
                topic.setGroupName(travelGroup.getTitle());
                topic.setUserId(topicDTO.getUserId());
                topic.setGrDeId(detail.getId());
                topicMapper.insertSelective(topic);
                return Response.success("成功");
            }
        }
        return Response.fail(CodeMsg.FAIL);
    }

    /**
     **根据userid和groupId判断成员是否在小组内
     * @param groupId
     * @param userId
     * @return
     */
    public Boolean checkGroupMById(int groupId, int userId){
        QueryWrapper<GroupMember> query = new QueryWrapper<>();
        query.eq("group_id",groupId);
        query.eq("user_id",userId);
        if(groupMemberMapper.selectCount(query)>0){
            return true;
        }
        return false;
    }
    public int updateTravelGroupById(Group group){
        return groupMapper.updateByPrimaryKeySelective(group);
    }
    @Override
    public List<Topic> selectPageVo(int limit, int page, String address) {
        // 分页
        Page<Topic> pageHelper = new Page<>();
        pageHelper.setSize(limit);
        pageHelper.setCurrent(page);
        IPage<Topic> pageVo = topicMapper.selectPageVo(pageHelper,address);
        return pageVo.getRecords();
    }
    @Override
    public List<Group> selectPageVoGroup(int limit,int page){
        // 分页
        Page<Group> pageHelper = new Page<>();
        pageHelper.setSize(limit);
        pageHelper.setCurrent(page);
        IPage<Group> pageVo = groupMapper.selectPageVo(pageHelper,null);
        return pageVo.getRecords();
    }

    /**
     * 分页查找
     * @param limit
     * @param page
     * @param flag
     * @return
     */
    public List<Group> selectPageVoGroupWithFlag(int limit,int page,Byte flag){
        // 分页
        Page<Group> pageHelper = new Page<>();
        pageHelper.setSize(limit);
        pageHelper.setCurrent(page);
        IPage<Group> pageVo = groupMapper.selectPageVo(pageHelper,flag);
        return pageVo.getRecords();
    }

    /**
     * 获取总条数
     * @param query
     * @return
     */
    public int getGroupCount(QueryWrapper<Group> query){
       return groupMapper.selectCount(query);
    }

    @Override
    public Topic queryTopicById(int id) {
        return topicMapper.selectByPrimaryKey(id);
    }

    @Override
    public TopicDetail queryTopicDetail(int id) {
        return topicDetailMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取小组内话题
     * @param id
     * @return
     */
    @Override
    public List<Topic> queryTopicByGroupId(int id){
        QueryWrapper<Topic> query = new QueryWrapper<>();
        query.eq("group_id",id);
        query.orderByDesc("date");
        return topicMapper.selectList(query);
    }

    @Override
    public List<GroupType> selectList(QueryWrapper<GroupType> query) {
        return groupTypeMapper.selectList(query);
    }

    @Override
    public List<Group> selectTravelGroup(QueryWrapper<Group> query) {
        return groupMapper.selectList(query);
    }

    /**
     * 查询用户加入的小组
     * @param userId
     * @return
     */
    @Override
    public List<Group> selectJoinGroup(int userId) {
        List<Group> result = new ArrayList<>();
        QueryWrapper<GroupMember> memberQuery = new QueryWrapper<>();
        memberQuery.eq("user_id",userId);
        List<GroupMember> list = groupMemberMapper.selectList(memberQuery);
        if(list != null && list.size() >0){
            list.forEach(item->result.add(groupMapper.selectByPrimaryKey(item.getGroupId())));
        }
        return result;
    }

    /**
     * 查询用户收藏的话题
     * @param userId
     * @return
     */
    @Override
    public List<Topic> selectColGroup(int userId) {
        List<Collect> list = commonService.getCollectList(userId,(byte)3);
        List<Topic> result = new ArrayList<>();
        list.forEach(item->result.add(topicMapper.selectByPrimaryKey(item.getProId())));
        return result;
    }

    /**
     *查询用户话题
     * @param userId
     * @return
     */
    @Override
    public List<Topic> selectUserTopic(int userId){
        QueryWrapper<Topic> query = new QueryWrapper<>();
        query.eq("user_id",userId);
        return topicMapper.selectList(query);
    }


    @Override
    public Group queryTravelGroupById(int id) {
        return groupMapper.selectByPrimaryKey(id);
    }

    @Override
    public int getTopicCount(QueryWrapper<Topic> query) {
        return topicMapper.selectCount(query);
    }

    @Override
    public int getCountMember(QueryWrapper<GroupMember> queryWrapper) {
        return groupMemberMapper.selectCount(queryWrapper);
    }

    public int joinGroup(int userId,int groupId){
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(userId);
        groupMember.setMsgFlag(Constants.ACPT);
        return groupMemberMapper.insertSelective(groupMember);
    }

    @Override
    public List<TopicComment> selectTopicCommemnt(int topicDetailId) {
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_de_id",topicDetailId);
        return topicCommentMapper.selectList(queryWrapper);
    }

    @Override
    public int createTopicComment(int topicId, int userId, String content) {
        TopicComment topicComment = new TopicComment();
        topicComment.setToDeId(topicId);
        topicComment.setUserId(userId);
        topicComment.setContent(content);
        topicComment.setDate(new Date());
        return topicCommentMapper.insertSelective(topicComment);
    }
    public int delTopicComment(int id,int userId){
        if(topicCommentMapper.selectByPrimaryKey(id) != null && topicCommentMapper.selectByPrimaryKey(id).getUserId() == userId){
            return topicCommentMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Override
    public int outGroup(int userId, int groupId) {
        QueryWrapper<GroupMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("group_id",groupId);
        GroupMember groupMember = groupMemberMapper.selectOne(queryWrapper);
        if(groupMember != null){
            return groupMemberMapper.deleteByPrimaryKey(groupMember.getId());
        }
        return 0;
    }

    @Override
    public int delColTopic(int userId, int topicId) {
        Collect collect = commonService.getCollect(userId,(byte)3,topicId);
        if(collect != null){
            return commonService.delCollect(collect.getId());
        }
        return 0;
    }
    // 解散小组 非物理删除
    public int disGroup(int userId,int groupId){
        Group travelGroup = groupMapper.selectByPrimaryKey(groupId);
        if(travelGroup != null && travelGroup.getUserId() == userId){
            Group group = new Group();
            group.setId(travelGroup.getId());
            group.setFlag((byte)3);
            return groupMapper.updateByPrimaryKeySelective(group);
        }
        return 0;
    }
    @Override
    @Transactional
    public int delTopic(int userId,int topicId){
        Topic topic = topicMapper.selectByPrimaryKey(topicId);
        if(topic != null && topic.getUserId() == userId){
            topicMapper.deleteByPrimaryKey(topicId);
            //删除评论
            QueryWrapper<TopicComment> query = new QueryWrapper<>();
            query.eq("to_de_id",topicId);
           return topicCommentMapper.delete(query);
        }
        return 0;
    }

    public Topic queryTopic(int id){
        return topicMapper.selectByPrimaryKey(id);
    }

    public List<Topic> queryListTopic(QueryWrapper<Topic> query){
        return topicMapper.selectList(query);
    }
}
