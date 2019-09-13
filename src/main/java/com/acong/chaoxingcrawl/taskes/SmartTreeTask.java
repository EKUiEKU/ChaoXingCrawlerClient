package com.acong.chaoxingcrawl.taskes;

import com.acong.chaoxingcrawl.bean.ClazzBean;
import com.acong.chaoxingcrawl.bean.Progress;
import com.acong.chaoxingcrawl.bean.UserInfo;
import com.acong.chaoxingcrawl.taskes.base.BaseTask;
import com.acong.chaoxingcrawl.values.TaskCode;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 智慧树刷课任务
 * 创建时间:2019-9-10 14:10:25
 *
 * @author WuShaoCong
 */
public class SmartTreeTask extends BaseTask {

    //存储登陆信息集合类.
    private UserInfo loginInfo;
    //用户的真实名字.
    private String realName;
    //尝试登陆的次数.
    private int tryCount = 0;
    //Chrome浏览器驱动类.
    private ChromeDriver mDriver;
    //存储课程表信息.
    List<ClazzBean> courseTable;

    private static final String login_url
            = "https://passport.zhihuishu.com/login?service=https://onlineservice.zhihuishu.com/login/gologin#studentID";

    /**
     * @param loginInfo 用户的登陆信息
     */
    public SmartTreeTask(UserInfo loginInfo) {
        this();
        this.loginInfo = loginInfo;
    }

    public SmartTreeTask() {

        //指定Chrome驱动.
        File f = new File("");
        String cf = null;
        try {
            cf = f.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("webdriver.chrome.driver", cf + "/chromedriver.exe");

        //实例化课程表容器.
        courseTable = new ArrayList<ClazzBean>();
    }

    @Override
    public void run() {
        super.run();
        loginSmartTree();
    }

    /**
     * 校验数据
     */
    public void loginSmartTree() {
        if (loginInfo != null
                && loginInfo.getUsername() != null
                && !loginInfo.getUsername().equals("")
                && loginInfo.getPassword() != null
                && !loginInfo.getPassword().equals("")
                && loginInfo.getSchool() != null
                && !loginInfo.getSchool().equals("")
                && loginInfo.getCourseName() != null
                && !loginInfo.getCourseName().equals("")) {
            //启动浏览器.
            this.mDriver = new ChromeDriver();
            startLogin();
        }
    }

    /**
     * 开始登陆.
     */
    private void startLogin() {
        mDriver.get(login_url);
        //填入登陆信息.
        WebElement input_School = mDriver.findElement(By.xpath("//*[@id=\"quickSearch\"]"));
        WebElement input_StudentID = mDriver.findElement(By.xpath("//*[@id=\"clCode\"]"));
        WebElement input_Password = mDriver.findElement(By.xpath("//*[@id=\"clPassword\"]"));

        //收集数据.
        //查询学校的ID.
        //存储学校ID.
        String schoolID = null;
        //是否查询到学校标识符.
        boolean flag = false;
        Object school = mDriver.executeScript("return userindex.schoolNameRetrieval('" + loginInfo.getSchool() + "');");
        System.out.println(school);
        List<Map<String, Object>> list = (List<Map<String, Object>>) school;
        for (Map<String, Object> map : list) {
            if (loginInfo.getSchool()
                    .equals(map.get("name"))) {
                flag = true;
                schoolID = map.get("schoolId") + "";
                break;
            }
        }

        //学校名称错误,没有找到.
        if (!flag) {
            System.out.println("学校没有找到.");
            //添加到消息队列.
            sendMessage(TaskCode.HANDLER_SCHOOL_URL_NOT_FOUND, "该单位/学校不存在。");
            return;
        }

        input_School.sendKeys(loginInfo.getSchool());
        //隐藏下拉菜单.
        mDriver.executeScript("$('#schoolSearchHolderCode').hide();");
        input_StudentID.sendKeys(loginInfo.getUsername());
        input_Password.sendKeys(loginInfo.getPassword());
        //设置学校的ID.
        mDriver.executeScript("$('#clSchoolId').val('" + schoolID + "');");
        mDriver.executeScript("$('#quickSearch').focus();");

        //登陆.
        mDriver.executeScript("formSignUp();");

        //等待加载完成.
        randomDelay(1000, 2000);

        //判断一个是否登陆成功.
        String currentUrl = mDriver.getCurrentUrl();
        if (currentUrl
                .contains("https://onlineh5.zhihuishu.com/onlineWeb.html")) {
            //登陆成功.
            //System.out.println("登陆成功.");
            //添加到消息队列.
            sendMessage(TaskCode.HANDLER_LOGIN_CHAOXING_SUCCESS, null, loginInfo);

            //正常延时1~2s.
            randomDelay(1000, 2000);

            //获取用户的真实名字.
            String realName = mDriver.findElement(By.xpath("//*[@id=\"app\"]/div/div[3]/div[2]/div[2]/div[1]/div[1]/div/p"))
                    .getAttribute("title");
            //添加到消息队列.
            sendMessage(TaskCode.HANDLER_STUDENT_NAME, realName, loginInfo);

            //获取所有的课程信息,并找到相应的课程.
            findCourseByName(loginInfo.getCourseName());
        } else if (currentUrl.contains(login_url)) {
            //登陆失败.
            //账号或密码错误.
            //System.out.println("账号或密码错误.");
            //添加到消息队列.
            sendMessage(TaskCode.HANDLER_LOGIN_CHAOXING_FAILURE_UOP_ERROR, null, loginInfo);
            return;
        }
    }

    /**
     * 通过课程名称找到相应的课程信息.
     *
     * @param courseName 课程的名称.
     */
    private void findCourseByName(String courseName) {
        randomDelay(2000,500);
        //查找课程名称.
        List<WebElement> courses = mDriver.findElement(By.id("sharingClassed"))
                .findElements(By.className("datalist"));

        for (WebElement course : courses) {
            String _courseName = course.findElement(By.className("courseName"))
                    .getText();
            //比较课程名称.
            if (_courseName != null
                    && _courseName.equals(courseName)) {
                //找到课程名称.

                //正常延时1~2s.
                randomDelay(1000, 2000);
                //模拟点击进课程.
                course.findElement(By.className("hoverList"))
                        .click();

                //正常延时1~2s.
                randomDelay(1000, 2000);

                //获取课程表.
                getCourseTable();
                return;
            }
        }

        //没有找到相应课程.
        //添加到消息队列.
        sendMessage(TaskCode.HANDLER_COURSE_URL_NO_FOUND, this, loginInfo, courseName);
    }

    /**
     * 通过JavaScript获取课程表
     */
    private void getCourseTable() {
        /**
         *   //获取课程表
         *   $('.time_ico1.fl,.isStudiedLesson').parent().parent().map(function () {
         *     return '{"className":"' + $(this).attr('_name').trim() + '","classURL":"' + $(this).attr('id') + '","isComplete":"' + ($(this).find('div b[class="fl time_icofinish"]').attr('style') == 'display:none' ? false : true) + '"}';
         *   }).toArray();
         *
         *   在这里之所以把lessonid换成classURL是因为为了兼容超星学习通的版本.复用ClazzBean对象.
         *   这里找到课程元素,并获取到其_name,_lessonid,watchstate的属性
         *   @_lessonid 是本节视频的唯一标识符,将来方便快速查找到此元素并给予点击.
         *   @watchstate 状态为1表示播放完成,其他状态开发者不需知道
         *   Created time:2019-9-12 00:08:09
         *   Created by:Wu Shaocong
         */
        List<String> courseList = (List<String>) mDriver.executeScript("return $('.time_ico1.fl,.isStudiedLesson').parent().parent().map(function () {return '{\"className\":\"' + $(this).attr('_name').trim() + '\",\"classURL\":\"' + $(this).attr('id') + '\",\"isComplete\":\"' + ($(this).find('div b[class=\"fl time_icofinish\"]').attr('style') == 'display:none' ? false : true) + '\"}';}).toArray();\n");
        for (String json : courseList) {
            System.out.println(json);
            ClazzBean clazzBean = new Gson()
                    .fromJson(json, ClazzBean.class);

            //添加到消息队列.
            sendMessage(TaskCode.HANDLER_CLASS_INFO, clazzBean, loginInfo);
            //将课程表的信息添加到容器里,刷课的时候会复用.
            courseTable.add(clazzBean);
        }

        //正常延时2~5s.
        randomDelay(2000,5000);

        //关闭警告窗口
        closeAlert();

        //添加到消息队列.
        sendMessage(TaskCode.HANDLER_COURSE_STARTED);
        //刷课具备的基本信息已经收集完成,开始刷课.
        start();

    }

    /**
     * 关闭进入学习通的警告窗口.
     *
     */
    private void closeAlert() {
        //关闭智慧树的警告.

        //使用原生的js,关闭学习进度.
        //mDriver.executeScript("document.getElementsByClassName(\"popboxes_close tmui_txt_hidd\")[0].click();");
    }

    /**
     * 刷网课的核心逻辑.
     */
    private void start() {
        //开始遍历课程表.
        for (ClazzBean course : courseTable) {
            //执行没有完成的网课.
            if (!course.getComplete()){
                //添加到消息队列
                sendMessage(TaskCode.HANDLER_CLASS_STARTED, course, loginInfo);
                //添加到消息队列.
                sendMessage(TaskCode.HANDLER_CLASS_VEDIO_STARTED, course, loginInfo);
                System.out.println("开始播放 " + course.getClassName());

                //标识符:题目是 否出现
                boolean flag = false;
                //跳转至未播放的网页.
                mDriver.executeScript("$('#chapterList li[id=" + course.getClassURL() +"]').click();");
                //获取播放信息.
                randomDelay(2000,5000);
                while (!((Boolean) mDriver.executeScript("return $('#vjs_mediaplayer_html5_api')[0].ended"))){
                    //一秒钟更新一次.
                    randomDelay(1000,1000);

                    //判断一下是否出现题目.
                    try {
                        //System.out.println("iframe出现了.");
                        WebElement iframe = mDriver.findElement(By.id("tmDialog_iframe"));
                        //出现了题目了.
                        flag = true;
                        //只要是没有出现异常就说明测试已经出现了,反正正常模仿.
                        mDriver.switchTo().frame(
                                iframe
                        );
                        String type = null;
                        //判断是单选题还是多选题.
                        if ((Boolean) mDriver.executeScript("return $('div[class^=answerOption] input[type=radio]').length != 0;")){
                            //单选题.
                            System.out.println("这是单选题.");
                            type = "radio";
                        } else if((Boolean) mDriver.executeScript("return $('div[class^=answerOption] input[type=checkbox]').length != 0;")){
                            //多选题.
                            System.out.println("这是多选题");
                            type = "checkbox";
                        }
                        /**
                         * //获取题目
                         * $('div[class^=answerOption] input[type=radio]').map(function(){
                         *     return '{"answer":"' + $(this).parent().text().trim() + '","value":"' + $(this).attr('value') + '"}';
                         * });
                         */
                        List<String> questions = (List<String>) mDriver.executeScript("return $('div[class^=answerOption] input[type=" +  type +"]').map(function(){return '{\"answer\":\"' + $(this).parent().text().trim() + '\",\"value\":\"' + $(this).attr('value') + '\"}';});");
                        if (questions!= null && questions.size() != 0){
                            //添加到消息队列.
                            sendMessage(TaskCode.HANDLER_EXAM_START,null,null,loginInfo);
                            //找到了题目.
                            System.out.println("找到了" + questions.size() + "个题目.");
                            //寻找正确答案.
                            String correct = (String) mDriver.executeScript("return $('.correctAnswer')[0].textContent;");
                            System.out.println("CORRECT:" + correct);
                            if (correct != null && !"".equals(correct)){
                                //添加到消息队列.
                                sendMessage(TaskCode.HANDLER_EXAM_CORRECT_ANSWER,correct,null,loginInfo);
                            }

                            for (String question : questions) {
                                Question q = new Gson()
                                        .fromJson(question, Question.class);

                                if (correct == null || "".equals(correct)){
                                    //还没有选择答案 答案没有加载出来.
                                    //随便选一个答案等待答案加载出来.
                                    mDriver.executeScript("$('input[type="  + type + "][value=" + q.getValue() + "]').click();");

                                    System.out.println("随便选择一个答案.");
                                    System.out.println(q.getValue() );
                                    //退出遍历题目.
                                    break;
                                }

                                String sign = q.getAnswer().substring(0,
                                        q.getAnswer().indexOf("."));

                                System.out.println("SING:" + sign);
                                for (int i = 0;i < correct.length();i++){
                                    char c = correct.charAt(i);
                                    System.out.println(c + " -- " + sign);
                                    if (sign.equals(c + "")){
                                        System.out.println("选择了选项" + c);

                                        //添加到消息队列.
                                        sendMessage(TaskCode.HANDLER_EXAM_DO,sign,null,loginInfo);

                                        //选择正确选项
                                        mDriver.executeScript("$('input[type=" + type + "][value=" + q.getValue() + "]').click();");
                                    }
                                }

                                //关闭弹窗
                                mDriver.switchTo().defaultContent();
                                mDriver.executeScript("$('div[class=popboxes_btn] a')[0].click();");

                                flag = false;

                                //添加到消息队列.
                                sendMessage(TaskCode.HANDLER_EXAM_CLOSE,null,null,loginInfo);
                            }

                        }
                    }catch (Exception e){
                        //没有出现.
                    }finally {
                        mDriver.switchTo().defaultContent();
                    }

                    if (flag){
                        continue;
                    }

                    //获取时间进度.
                    Double duration = (Double) mDriver.executeScript("return $('#vjs_mediaplayer_html5_api')[0].duration");
                    Double currentTime = (Double) mDriver.executeScript("return $('#vjs_mediaplayer_html5_api')[0].currentTime");
                    System.out.println(durationFormat(Math.round(currentTime.floatValue()))
                            + "/"
                            + durationFormat(Math.round(duration.floatValue()))
                            + " "
                            + Math.round(currentTime / duration * 100.0) + "%");

                    //添加到消息队列.
                    sendMessage(TaskCode.HANDLER_CLASS_VIDEO_PROGRESS, new Progress(durationFormat(Math.round(currentTime.floatValue())), durationFormat(Math.round(duration.floatValue()))), loginInfo, course);


                    //设置静音播放.
                    mDriver.executeScript("$('#vjs_mediaplayer_html5_api')[0].muted=true");
                    //设置1.0倍速播放.
                    //之所以1.20倍速播放是因为一个课题要播放超过80%的时间才算上完课程.
                    mDriver.executeScript("$('#vjs_mediaplayer_html5_api')[0].playbackRate=1.20");
                }

                //播放完成.
                //正常延时2~5s,进入下一个视频.
                randomDelay(2000,5000);
            }
        }

        //所有未完成的课程已经遍历完成,该课程刷课完成.
        //添加到消息队列.
        sendMessage(TaskCode.HANDLER_COURSE_COMPLETED, null, loginInfo);
        //关闭浏览器.
        mDriver.close();
    }

    //选择题类
    private class Question{
        //选项答案.
        public String answer;
        //选择题的唯一标识符.
        public String value;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Question{" +
                    "answer='" + answer + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /**
     * 延时函数,为了躲避系统的追查,模拟人类正常的速度,用此延时函数.
     * 给定函数一个范围,延时的时间将在[minTime,maxTime]区间.
     *
     * @param minTime 最小的延时时间.
     * @param maxTime 最大的延时时间.
     */
    public void randomDelay(@NotNull int minTime, @NotNull int maxTime) {
        if (maxTime < minTime) {
            int t = maxTime;
            maxTime = minTime;
            minTime = maxTime;
        }

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int i = Math.abs(random.nextInt(maxTime));
        i = i < minTime ? minTime : i;
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 格式化时间
     * @param totalSeconds
     * @return 返回格式化的时间
     * Crated time:2019-9-12 13:41:22
     * Created by:Wu Shaocong
     */
    private String durationFormat(Integer totalSeconds) {
        if (totalSeconds == null || totalSeconds < 1) {
            return "00:01";
        }
        //将秒格式化成HH:mm:ss
        //这里应该用Duration更合理，但它不能格式化成字符串
        //而使用LocalTime，在时间超过24小时后格式化也会有问题（！）
        int hours = totalSeconds / 3600;

        int rem = totalSeconds % 3600;
        int minutes = rem / 60;
        int seconds = rem % 60;
        if (hours <= 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
