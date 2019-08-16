package com.acong.chaoxingcrawl.service;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;

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
}
