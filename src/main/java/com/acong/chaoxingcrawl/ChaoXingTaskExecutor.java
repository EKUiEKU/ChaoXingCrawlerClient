package com.acong.chaoxingcrawl;

import com.acong.chaoxingcrawl.bean.ClazzBean;
import com.acong.chaoxingcrawl.bean.Progress;
import com.acong.chaoxingcrawl.mq.Handler;
import com.acong.chaoxingcrawl.mq.Message;
import com.acong.chaoxingcrawl.taskes.WatchChaoXingTask;
import com.acong.chaoxingcrawl.values.TaskCode;
import interfaces.OnMessageQueueListener;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ChaoXingTaskExecutor extends ThreadPoolExecutor implements Handler.Callback {
    private static ChaoXingTaskExecutor executor;

    /**
     * 2GB内存的服务器有点慌
     */
    private static int initSize = 4;
    private static int maxSize = 5;
    private static long time = 10000;
    private static TimeUnit timeUnit = TimeUnit.SECONDS;

//    /**
//     * 存放正在运行的LoginChaoXingTask
//     * key:LoginChapXingTask的HashMap
//     * value:LoginChapXingTask对象
//     */
//    private Map<Integer,Object> loginChaoXingTaskMap;
    /**
     * 存放已经登陆的Cookies
     * key:username     value:cookies
     */
    private Map<String, Set<Cookie>> loginCookies;

    /**
     * 消息队列
     */
    private Handler handler;

    private ChaoXingTaskExecutor(int initSize, int maxSize, long time, TimeUnit timeUnit) {
        super(initSize, maxSize, time, timeUnit, new LinkedBlockingQueue<Runnable>());

        /**
         * 消息队列
         */
        handler = new Handler(this);

//        loginChaoXingTaskMap = new HashMap<Integer, Object>();
        loginCookies = new HashMap<String, Set<Cookie>>();
    }

    /**
     * @param initSize 初始化线程池的大小
     * @param maxSize  线程池的最大大小
     * @param time     线程空闲时何时释放
     * @param timeUnit 释放时间的单位
     */
    public static void setParame(@NotNull int initSize, @NotNull int maxSize, @NotNull long time, @NotNull TimeUnit timeUnit) {
        ChaoXingTaskExecutor.initSize = initSize;
        ChaoXingTaskExecutor.maxSize = maxSize;
        ChaoXingTaskExecutor.time = time;
        ChaoXingTaskExecutor.timeUnit = timeUnit;
    }

    public static ChaoXingTaskExecutor  getInstance() {
        synchronized (ChaoXingTaskExecutor.class) {
            if (executor == null) {
                synchronized (ChaoXingTaskExecutor.class) {
                    executor = new ChaoXingTaskExecutor(initSize, maxSize, time, timeUnit);
                }
            }
        }

        return executor;
    }

    /**
     * @return 返回消息队列对象
     */
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);

//        if (command instanceof WatchChaoXingTask){
//            loginChaoXingTaskMap.put(command.hashCode(),command);
//        }
    }

    /**
     * 消息队列处理
     *
     * @param msg
     * @return
     */
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case TaskCode
                    .HANDLER_DAMA_SUCCESS:
                callBack("消息队列:打码成功 code:" + msg.obj);
                WatchChaoXingTask o = (WatchChaoXingTask) msg.arg2;
                o.setImageCode((String) msg.obj);
                synchronized (o) {
                    o.notify();
                }
//                /**
//                 * 移除loginChaoXingTaskMap的元素
//                 */
//                loginChaoXingTaskMap.remove(msg.arg1.hashCode());
                break;
            case TaskCode
                    .HANDLER_DAMA_FAILURE:
                callBack("HANDLER:打码失败 code:" + msg.obj);
                WatchChaoXingTask o1 = (WatchChaoXingTask) msg.arg2;
                synchronized (o1) {
                    o1.notify();
                }
//                /**
//                 * 移除loginChaoXingTaskMap的元素
//                 */
//                loginChaoXingTaskMap.remove(msg.arg1.hashCode());
                break;
            case TaskCode
                    .HANDLER_LOGIN_CHAOXING_SUCCESS:
                callBack("消息队列:" + msg.arg1.getUsername() + " 登陆成功");
                //保存Cookies
                loginCookies.put(msg.arg1.toString(), (Set<Cookie>) msg.obj);
                //String url = "https://mooc1-1.chaoxing.com/mycourse/studentstudy?chapterId=121176485&courseId=200534731&clazzid=6734325&enc=1cf247869b9ac4e00f1fa8b2d2256bba";
                break;
            case TaskCode
                    .HANDLER_LOGIN_CHAOXING_FAILURE_UOP_ERROR:
                callBack("消息队列:" + msg.arg1.getUsername() + " 账号或密码有误");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onLoginFailure();
                }
                break;
            case TaskCode
                    .HANDLER_LOGIN_CHAOXING_FAILURE_CODE_ERROR:
                callBack("消息队列:" + msg.arg1.getUsername() + " 验证码有误");
                callBack("消息队列:" + msg.arg1.getUsername() + " 即将重新登陆");
                //执行重新登陆的代码
                this.execute(new WatchChaoXingTask(msg.arg1));
                break;
            case TaskCode
                    .HANDLER_COURSE_URL:
                callBack("消息队列:" + msg.arg1.getUsername() + " 课程<<" + msg.arg2 + ">> \n\tURL:" + msg.obj);
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onCourseName(msg.arg2 + "");
                }
                break;
            case TaskCode
                    .HANDLER_COURSE_URL_NO_FOUND:
                callBack("消息队列:" + msg.arg1.getUsername() + " 课程<<" + msg.arg2 + ">> \n\t课程不存在");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onCourseNoFound(msg.arg2 + "");
                }
                break;
            case TaskCode
                    .HANDLER_STUDENT_NAME:
                callBack("消息队列:" + msg.arg1.getUsername() + " 姓名:" + msg.obj);
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onLoginSuccess(msg.obj + "");
                }
                break;
            case TaskCode
                    .HANDLER_COURSE_TABLE_NO_FOUND:
                callBack("消息队列:" + msg.arg1.getUsername() + " 错误:找不到<<" + msg.arg1.getCourseName() + ">>的课程表");
                break;
            case TaskCode
                    .HANDLER_CLASS_STARTED:
                ClazzBean bean = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getCourseName() + " [开始上课]->[本节课程名称]->[" + bean.getClassName() + "]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onStartClass(bean.getClassName());
                }
                break;
            case TaskCode
                    .HANDLER_COURSE_COMPLETED:
                callBack("消息队列:" + msg.arg1.getUsername() + " [本课程已完成]");

                if (onMessageQueueListener != null)
                    onMessageQueueListener.onCourseCompleted();
                break;
            case TaskCode
                    .HANDLER_CLASS_VEDIO_STARTED:
                ClazzBean bean1 = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + " [视频播放]->[开始播放]->[" + bean1.getClassName() + "]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onPlaying("视频");
                }
                break;
            case TaskCode
                    .HANDLER_CLASS_PPT_STARTED:
                ClazzBean bean2 = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + " [PPT放映]->[开始放映]->[" + bean2.getClassName() + "]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onPlaying("PPT");
                }
                break;
            case TaskCode
                    .HANDLER_CLASS_TEST:
                ClazzBean bean3 = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + " [本章测验]->[操作]->[自动跳过]->[" + bean3.getClassName() + "]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onPlaying("测验");
                }
                break;
            case TaskCode
                    .HANDLER_CLASS_VIDEO_PROGRESS:
                Progress progress = (Progress) msg.obj;
                ClazzBean bean4 = (ClazzBean) msg.arg2;
                callBack("消息队列:" + msg.arg1.getUsername() + " [" + bean4.getClassName() + "]->[视频播放]->[播放进度]->[" + progress.getCurrentTime() + "/" + progress.getTotalTime() + "]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onPlayingVideoProgress(progress);
                }
                break;
            case TaskCode
                    .HANDLER_CLASS_COMPLETED:
                ClazzBean bean5 = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + " [" + bean5.getClassName() + "]->[视频播放]->[播放完成]");
                break;
            case TaskCode
                    .HANDLER_CLASS_PPT_PROGRESS:
                ClazzBean bean6 = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + " [" + bean6.getClassName() + "]->[PPT放映]->[放映进度]->[第" + msg.arg2 + "页]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onPlayingPPTProgress(msg.arg2 + "");
                }
                break;
            case TaskCode
                    .HANDLER_CLASS_INFO:
                ClazzBean info = (ClazzBean) msg.obj;
                callBack("消息队列:" + msg.arg1.getUsername() + "\n" + info.toString());

                if (onMessageQueueListener != null){
                    onMessageQueueListener.onClassInfo(info);
                }
                break;
            case TaskCode
                    .HANDLER_EXCEPTION:
                callBack("消息队列:[错误]->[本程序出现了异常,很有可能是浏览器中途被你关掉了。]");
                if (onMessageQueueListener != null){
                    onMessageQueueListener.onExceptiom();
                }
                break;
            case TaskCode
                    .HANDLER_COURSE_STARTED:
                if (onMessageQueueListener != null)
                    onMessageQueueListener.onCourseStart();
                break;
        }
        return false;
    }

    private OnMessageQueueListener onMessageQueueListener = null;

    public void setOnMessageQueueListener(@NotNull OnMessageQueueListener l){
        this.onMessageQueueListener = l;
    }


    /**
     * 回调到UI线程方法
     */

    private void callBack(String msg){
        if (onMessageQueueListener != null){
            onMessageQueueListener.onMessage(msg);
        }else{
            System.out.println(msg);
        }
    }
}
