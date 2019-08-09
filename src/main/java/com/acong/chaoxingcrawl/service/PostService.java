package com.acong.chaoxingcrawl.service;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean._UserInfo;
import com.acong.chaoxingcrawl.utils.UserUtil;
import interfaces.OnLoginListener;
import interfaces.OnUploadInfoListener;

/**
 * 这个服务用于与用户进行交互。
 */
public class PostService extends Thread {

    private final ChaoXingTaskExecutor taskExecutor;

    public PostService() {
        taskExecutor = ChaoXingTaskExecutor.getInstance();
    }

    @Override
    public void run() {
        super.run();

//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
////        try {
////            System.out.println("账号:");
////            String username = reader.readLine();b
////            System.out.println("密码:");
////            String password = reader.readLine();
////            System.out.println("课程名称:");
////            String courseName = reader.readLine();
////
////            UserInfo info = new UserInfo(username, password, courseName);
////            taskExecutor.execute(new WatchChaoXingTask(info));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }

        //loginTest();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private void uploadTest(){
        UserUtil util = UserUtil.create();
        _UserInfo info = new _UserInfo();
        info.setName("刘德华");
        info.setSchool("香港大学");
        ///info.setStudent_id("19870201");
        info.setUid(2121434333L);

        util.uploadInfo(info, new OnUploadInfoListener() {
            public void onUploadSuccess() {
                System.out.println("上传成功。");
            }

            public void onUploadFailure(String causeBy) {
                System.out.println("上传失败 原因是:" +causeBy);
            }
        });
    }

    private void loginTest(){
        final UserUtil util = UserUtil.create();
        util.login("1178454070@qq.com", "wawlywsc2012", new OnLoginListener() {
            public void onLoginSuccess(Long uid) {
                System.out.println(uid);
                uploadTest();
            }

            public void onLoginFailure(String causeBy) {
                System.out.println(causeBy);
            }
        });
    }
}
