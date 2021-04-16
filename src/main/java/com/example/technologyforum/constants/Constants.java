package com.example.technologyforum.constants;

/**
 *
 * 常量存储
**/
public class Constants {

    /**
     * 公告
     */
    public static final int MSG_TYPE_ANNOUNCE = 1 ;
    /**
     * 私信
     */
    public static final int MSG_TYPE_PER = 2 ;
    /**
     * 提醒
     */
    public static final int MSG_TYPE_MESS = 3 ;
    /**
     * 攻略消息
     */
    public static final int STRATEGY_MSG = 1 ;
    /**
     * 话题消息
     */
    public static final int TOPIC_MSG = 2 ;
    /**
     * 问答消息
     */
    public static final int QUESTION_MSG = 3 ;
    /**
     * 小组消息
     */
    public static final int GROUP_MSG = 4 ;
    /**
     * 评论消息
     */
    public static final int COM_MSG = 1 ;
    /**
     * 收藏消息
     */
    public static final int COLLECT_MSG = 2 ;
    /**
     * 问题解决消息
     */
    public static final int QUESTION_FIN_MSG = 3 ;
    /**
     * 问题采纳
     */
    public static final int QUESTION_ACP_MSG = 4 ;
    /**
     * 其它消息
     */
    public static final int OTHER_MSG = 0 ;
    /**
     * 接收 / 未读
     */
    public static final Byte ACPT = 1 ;
    /**
     * 拒绝 / 已读
     */
    public static final Byte REJECT = 2 ;
    /**\
     * 是否发表
     */
    public static final Byte PUSH_YES = 1 ;
    /**\
     * 是否发表
     */
    public static final Byte PUSH_NO = 2 ;

    /**\
     * 问题解决
     */
    public static final Byte QUESTION_YES = 1 ;
    /**\
     * 问题未结
     */
    public static final Byte QUESTION_NO = 2 ;
    /**\
     * 审核状态
     */
    public static final Byte PASS_NO = 1 ;
    /**\
     * 审核通过状态
     */
    public static final Byte PASS_YES = 2 ;
    /**\
     * 审核驳回状态
     */
    public static final Byte PASS_REJECT = 4 ;
    /**\
     * 最佳答案
     */
    public static final Byte BEST_ANSWER = 2 ;
    /**\
     * 正常评论（非最佳）
     */
    public static final Byte NOM_ANSWER = 1 ;

    public static final String ESSAY_HOT_NAME = "strahot";
    public static final String QUESTION_HOT_NAME = "queshot";
    public static final String TOPIC_HOT_NAME = "topichot";
    public static final String USER_QUESTION_COM_NUM = "userquecomnum";
}
