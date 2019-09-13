import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean.UserInfo;
import com.acong.chaoxingcrawl.mq.Looper;
import com.acong.chaoxingcrawl.taskes.SmartTreeTask;
import org.junit.Test;

public class TSmartTreeTask {
    private ChaoXingTaskExecutor executor;

    public TSmartTreeTask() {
    }

    @Test
    public void login() {
        Looper.prepare();

        executor = ChaoXingTaskExecutor
                .getInstance();

        UserInfo loginInfo = new UserInfo();
        loginInfo.setUsername("201811404537");
        loginInfo.setPassword("wawlywsc2012");
        loginInfo.setSchool("莆田学院");
        loginInfo.setCourseName("音乐鉴赏");

        SmartTreeTask smartTreeTask = new SmartTreeTask(loginInfo);
        executor.execute(smartTreeTask);

        Looper.loop();
    }

    @Test
    public void Tdata(){
        System.out.println("A".equals('A' + ""));
    }

}
