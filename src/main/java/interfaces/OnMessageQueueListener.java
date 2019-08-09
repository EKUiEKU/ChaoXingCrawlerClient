package interfaces;

import com.acong.chaoxingcrawl.bean.ClazzBean;
import com.acong.chaoxingcrawl.bean.Progress;

/**
 * 回调消息队列信息的接口
 */
public interface OnMessageQueueListener {
    void onMessage(String msg);
    void onClassInfo(ClazzBean info);
    void onLoginFailure();
    void onLoginSuccess(String name);
    void onCourseName(String courseName);
    void onCourseNoFound(String courseName);
    void onStartClass(String className);
    void onPlaying(String type);
    void onPlayingVideoProgress(Progress p);
    void onPlayingPPTProgress(String pageAt);
    void onExceptiom();
    void onCourseStart();
    void onCourseCompleted();
}
