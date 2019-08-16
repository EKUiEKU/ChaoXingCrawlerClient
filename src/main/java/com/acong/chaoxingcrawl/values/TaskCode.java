package com.acong.chaoxingcrawl.values;

/**
 * 消息队列的Code
 */
public class TaskCode {
    /**
     * 打码成功
     */
    public static final int HANDLER_DAMA_SUCCESS = 0;
    /**
     * 打码失败
     */
    public static final int HANDLER_DAMA_FAILURE = 1;
    /**
     * 登陆成功
     */
    public static final int HANDLER_LOGIN_CHAOXING_SUCCESS = 2;
    /**
     * 登陆失败:账号或密码错误
     */
    public static final int HANDLER_LOGIN_CHAOXING_FAILURE_UOP_ERROR = 3;
    /**
     * 登陆失败:验证码错误
     */
    public static final int HANDLER_LOGIN_CHAOXING_FAILURE_CODE_ERROR = 4;
    /**
     * 本课程刷课完成
     */
    public static final int HANDLER_COURSE_COMPLETED = 5;
    /**
     * 刷课出现了异常
     */
    public static final int HANDLER_EXCEPTION = 6;
    /**
     * 开始刷网课
     */
    public static final int HANDLER_COURSE_STARTED = 7;
    /**
     * 一节课的进度
     */
    public static final int HANDLER_CLASS_VIDEO_PROGRESS = 8;
    /**
     * 一节课开始播放视频
     */
    public static final int HANDLER_CLASS_STARTED = 9;
    /**
     * 一节课完成刷课
     */
    public static final int HANDLER_CLASS_COMPLETED = 10;
    /**
     * 视频无法播放
     */
    public static final int HANDLER_CLASS_VEDIO_ERROR = 11;
    /**
     * 一节科的Tabs
     */
    public static final int HANDLER_CLASS_TABS = 12;
    /**
     * 获取到课程播放的URL
     */
    public static final int HANDLER_COURSE_URL = 13;
    /**
     * 无法获取到播放的URL(可能是输入的课程名有误或者课程不存在)
     */
    public static final int HANDLER_COURSE_URL_NO_FOUND = 14;
    /**
     * 学生的姓名
     */
    public static final int HANDLER_STUDENT_NAME = 15;
    /**
     * 找不到课程表
     */
    public static final int HANDLER_COURSE_TABLE_NO_FOUND = 16;
    /**
     * 开始播放视频
     */
    public static final int HANDLER_CLASS_VEDIO_STARTED = 17;
    /**
     * 开始放映PPT
     */
    public static final int HANDLER_CLASS_PPT_STARTED = 18;
    /**
     * 本章测验
     */
    public static final int HANDLER_CLASS_TEST = 19;
    /**
     * PPT的播放进度
     */
    public static final int HANDLER_CLASS_PPT_PROGRESS = 20;
    /**
     * 课堂信息
     */
    public static final int HANDLER_CLASS_INFO = 21;
    /**
     * 找到了单位的登陆页面
     */
    public static final int HANDLER_SCHOOL_URL_FOUND = 22;
    /**
     * 没有找到单位的登陆页面
     */
    public static final int HANDLER_SCHOOL_URL_NOT_FOUND = 23;
}
