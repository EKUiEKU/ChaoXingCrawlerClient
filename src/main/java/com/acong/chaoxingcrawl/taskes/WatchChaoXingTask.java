package com.acong.chaoxingcrawl.taskes;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean.*;
import com.acong.chaoxingcrawl.taskes.base.BaseTask;
import com.acong.chaoxingcrawl.utils.net.DamagouUtil;
import com.acong.chaoxingcrawl.values.TaskCode;
import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WatchChaoXingTask extends BaseTask {

    private ChromeDriver driver;

    private List<ClazzBean> clazzBeanList;

    private UserInfo userInfo;

    public WatchChaoXingTask(@NotNull UserInfo info) {
        userInfo = info;

        File f = new File("");
        String cf = null;
        try {
            cf = f.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("webdriver.chrome.driver",cf + "/chromedriver.exe");
        System.out.println(cf);
        driver = new ChromeDriver();

        clazzBeanList = new ArrayList<ClazzBean>();

        driver.manage().window().maximize();
    }

    @Override
    public void run() {
        super.run();
        try {
            String schoolURL = findURLBySchool(userInfo.getSchool());

            if (schoolURL.equals("")){
                driver.close();
                return;
            }

            driver.get(schoolURL);

            driver.findElement(By.xpath("//*[@id=\"unameId\"]")).sendKeys(userInfo.getUsername());
            driver.findElement(By.xpath("//*[@id=\"passwordId\"]")).sendKeys(userInfo.getPassword());

            final WebElement InputCode = driver.findElement(By.xpath("//*[@id=\"numcode\"]"));


            //截取验证码
            WebElement img_code = driver.findElement(By.xpath("//*[@id=\"numVerCode\"]"));

            byte[] img = screenshot(img_code);

            final WebElement click = driver.findElement(By.xpath("//*[@id=\"form\"]/table/tbody/tr[7]/td[2]/label/input"));

            ChaoXingTaskExecutor.getInstance().execute(new com.acong.chaoxingcrawl.taskes.DamaTask(img, DamagouUtil.TYPE.TYPE_ONLY_NUMBSERS, this));

            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 判断是否打码成功
             */
            if (imgCode != null) {
                //打码成功
                InputCode.sendKeys(this.imgCode);
                //单击登陆
                click.click();

                //判断是否登陆成功
                if (!driver.getTitle().equals("用户登录")) {
                    //登陆成功
                    //保存登陆的Cookies
                    Set<Cookie> cookies = driver.manage().getCookies();
                    sendMessage(TaskCode.HANDLER_LOGIN_CHAOXING_SUCCESS, cookies, userInfo);
                } else {
                    //登陆失败
                    System.out.println(userInfo.getUsername() + ":登陆失败！");
                    //查找登陆失败的原因
                    String cause = driver.findElementByClassName("err-tip").getText().trim();
                    /**
                     * 1.用户名或密码错误 2.密码错误  3.验证码错误
                     */
                    if (cause.equals("用户名或密码错误") || cause.equals("密码错误")) {
                        sendMessage(TaskCode.HANDLER_LOGIN_CHAOXING_FAILURE_UOP_ERROR, null, userInfo);
                    } else if (cause.equals("验证码错误")) {
                        sendMessage(TaskCode.HANDLER_LOGIN_CHAOXING_FAILURE_CODE_ERROR, null, userInfo);
                        //来执行一次登陆。

                    }

                    driver.close();


                    return;
                }

                //获取用户的名字
                randomDelay(1000,1500);
                String name = driver.findElement(By.className("personalName")).getAttribute("title");
                sendMessage(TaskCode.HANDLER_STUDENT_NAME, name, userInfo);

                /**
                 * 开始刷网课
                 */
                findCourseURL(userInfo.getCourseName());


                /**
                 * 开始执行未完成的课程
                 */

                /**
                 * 开始刷网课
                 */
                sendMessage(TaskCode.HANDLER_COURSE_STARTED);

                for (int i = 0; i < clazzBeanList.size(); i++) {
                    ClazzBean bean = clazzBeanList.get(i);
                    if (bean.getComplete())
                        continue;

                    sendMessage(TaskCode.HANDLER_CLASS_STARTED, bean, userInfo);
                    //跳转至指定URL
                    driver.get(bean.getClassURL());

                    randomDelay(1000, 5000);
                    playVedio(bean);
                }
                sendMessage(TaskCode.HANDLER_COURSE_COMPLETED, null, userInfo);
                driver.close();
            } else {
                //打码失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("close");
            sendMessage(TaskCode.HANDLER_EXCEPTION);
        } finally {

        }
    }


    /**
     * 存放验证码的地方
     */
    private String imgCode = null;

    /**
     * 对外开放的方法 赋值imgCode
     */
    public void setImageCode(String code) {
        this.imgCode = code;
    }

    private void playVedio(ClazzBean clazzInfo) {
        /**
         * 获取Tabs
         */
        List<WebElement> tabs = driver.findElement(By.className("tabtags")).findElements(By.tagName("span"));
        if (tabs.size() == 0) {
//            driver.switchTo().defaultContent();
//            /**
//             * 第一层iframe是必然存在的
//             */
//            driver.switchTo().frame("iframe");
//            //判断一下页面有没有视频播放器
//            List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"ext-gen1039\"]/iframe"));
//            /**
//             * 遍历一个Tab之下的所有播放器。
//             */
//            for (int i = 0; i < elements.size(); i++) {
//                driver.switchTo().defaultContent();
//                driver.switchTo().frame("iframe");
//                driver.switchTo().frame(elements.get(i));
//                autoPlay(clazzInfo);
//            }
//            //说明没有Tabs,可以直接播放视频
//            //autoPlay(clazzInfo);
            findPlayFrame(clazzInfo);
        } else {
            List<TabBean> tabs_list = new ArrayList<TabBean>();

            for (WebElement _tab : tabs) {
                String jump = _tab.getAttribute("onclick");

                tabs_list.add(new TabBean(jump, _tab.getText()));

                //System.out.println(userInfo.getUsername() + ":[" + clazzInfo.getClassName() + "]->[" + _tab.getText() + "]->[URL]->[" + jump + "]");
            }

            for (int i = 0; i < tabs.size(); i++) {
                //判断一下每一个tab中有没有视频
                //有则播放无则遍历
                WebElement tab = tabs.get(i);
                TabBean tabBean = tabs_list.get(i);
                //跳转指定tab
                driver.switchTo().defaultContent();
                driver.executeScript("javascript:" + tabBean.getTabURL(), driver.findElement(By.xpath("//*[@id=\"mainid\"]")));

                randomDelay(1000, 2000);

//                /**
//                 * 每一个tab的内容 都包含class="ans-cc"
//                 * 只要每一个tab中含有class="vjs-control-bar" 则代表这个tab之下有视频可以播放。
//                 */
////                WebElement tab_content = driver.findElement(By.className("ans-cc"));
//                /**
//                 * 第一层iframe是必然存在的
//                 */
//                driver.switchTo().frame("iframe");
//                /**
//                 * 第二次iframe不是必然的。 要判断一下第二次iframe是否存在
//                 */
//                List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"ext-gen1039\"]/iframe"));
//                if (elements.size() > 0) {
//                    /**
//                     * 遍历一个Tab之下的所有播放器。
//                     */
//                    for (int j = 0; j < elements.size(); j++) {
//                        driver.switchTo().defaultContent();
//                        driver.switchTo().frame("iframe");
//                        driver.switchTo().frame(elements.get(j));
//                        autoPlay(clazzInfo);
//                    }
//                } else {
//                    System.out.println(userInfo.getUsername() + ":[" + clazzInfo.getClassName() + "]->[" + tabBean.getTabName() + "]->[该页面没有视频。]");
//                    continue;
//                }

                findPlayFrame(clazzInfo);
            }
        }
//        /**
//         * 寻找有没有PPT播放器
//         */
//        driver.switchTo().defaultContent();
//        driver.switchTo().frame("iframe");
//        List<WebElement> elements_ppt = driver.findElements(By.xpath("//*[@id=\"ext-gen1038\"]/div/div/p/div/iframe"));
//        for (int q = 0;q < elements_ppt.size();q++) {
//            driver.switchTo().frame(elements_ppt.get(q));
//            try {
//                WebElement btn_next = driver.findElement(By.xpath("//*[@id=\"ext-gen1040\"]"));
//                String str_total = driver.findElement(By.xpath("//*[@id=\"navigation\"]")).findElement(By.className("all")).getText();
//                int num_total = Integer.valueOf(str_total);
//
//                for (int z = 0; z < num_total - 1; z++) {
//                    btn_next.click();
//                    System.out.println(userInfo.getUsername() + ":[" + clazzInfo.getClassName() + "]->[PPT]-[第" + (z + 1) + "页]");
//                    randomDelay(2000,3000);
//                }
//            }catch (Exception e){
//                return;
//            }finally {
//                driver.switchTo().parentFrame();
//            }
//        }
    }

    private void findPlayFrame(ClazzBean clazzInfo) {
        driver.switchTo().defaultContent();
        driver.switchTo().frame("iframe");
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

        List<String> iframes_class = new ArrayList<String>();
        for (WebElement iframe : iframes) {
            iframes_class.add(iframe.getAttribute("class"));
        }
        for (int i = 0; i < iframes_class.size(); i++) {
            String clazzName = iframes_class.get(i);

            driver.switchTo().defaultContent();
            driver.switchTo().frame("iframe");
            driver.switchTo().frame(iframes.get(i));
            if (clazzName.equals("ans-attach-online ans-insertvideo-online")) {
                /**
                 * 播放视频
                 */
                sendMessage(TaskCode.HANDLER_CLASS_VEDIO_STARTED, clazzInfo, userInfo);
                autoPlay(clazzInfo);
            } else if (clazzName.equals("ans-attach-online insertdoc-online-pdf") || clazzName.equals("ans-attach-online insertdoc-online-ppt")) {
                /**
                 * 播放ppt/pdf
                 */
                sendMessage(TaskCode.HANDLER_CLASS_PPT_STARTED, clazzInfo, userInfo);
                playPPT(clazzInfo);
            } else if (clazzName.equals("")) {
                /**
                 * 本章测验
                 */
                sendMessage(TaskCode.HANDLER_CLASS_TEST, clazzInfo, userInfo);
            }
        }
    }

    private void playPPT(ClazzBean clazzInfo) {
        try {
            WebElement btn_next = driver.findElement(By.className("imglook")).findElement(By.className("mkeRbtn"));
            String str_total = driver.findElement(By.xpath("//*[@id=\"navigation\"]")).findElement(By.className("all")).getText();
            int num_total = Integer.valueOf(str_total);

            for (int z = 0; z < num_total - 1; z++) {
                btn_next.click();
                sendMessage(TaskCode.HANDLER_CLASS_PPT_PROGRESS, clazzInfo, userInfo, z + 2);
                randomDelay(2000, 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void autoPlay(ClazzBean clazzInfo) {
        randomDelay(1000, 3000);
        /**
         * 点播放视频的时候才会加载视频源码。
         */
        //driver.switchTo().parentFrame();
        //driver.navigate().refresh();
//        driver.switchTo().defaultContent();
//        driver.switchTo().frame("iframe");
        //driver.findElement(By.xpath("//*[@id=\"ext-gen1039\"]/iframe")).click();

        //driver.navigate(t).refresh();
//        try {
//            driver.switchTo().frame(findElement(driver,By.xpath("//*[@id=\"ext-gen1039\"]/iframe")));
//        } catch (ElementException e) {
//            /**
//             * 跳过本次播放
//             */
//            System.out.println(userInfo.getUsername() + ":[视频播放]->[ " + clazzInfo.getClassName() + "]->[错误]->[无法找到视频框架]");
//            return;
//        }

        /**
         * 判断一下视频可否播放。
         * 视频因格式不支持或者服务器或网络的问题无法加载。
         */
//        try {
//            WebElement error = findElement(driver, By.id("ext-gen1044"));
//            int i = error.findElements(By.tagName("div")).size();
//            /**
//             * 视频正常播放的视频的时候div标签是不存在的。
//             */
//            if (i > 0){
//                System.out.println(username + ":[视频播放]->[ " + clazzInfo.getClassName() + "]->[错误]->[视频无法正常播放]");
//                return;
//            }
//        } catch (ElementException e) {
//            System.out.println(username + ":[视频播放]->[ " + clazzInfo.getClassName() + "]->[错误]->[id:ext-gen1044 元素不存在]");
//            return;
//        }

        /**
         * 获取视频播放器控件
         */
        //当前播放时间标签
        WebElement lable_currentTime = driver.findElement(By.xpath("//*[@id=\"video\"]/div[4]/div[2]/span[2]"));
        //视频播放的总时间
        WebElement lable_totalTime = driver.findElement(By.xpath("//*[@id=\"video\"]/div[4]/div[4]/span[2]"));

        /**
         * 获取控制视频的API
         */
        WebElement api = driver.findElement(By.id("video_html5_api"));

        driver.executeScript("arguments[0].play()", api);
        //设置静音
        driver.executeScript("arguments[0].muted=true", api);

        //获取总时间
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Double totalTime = (Double) driver.executeScript("return arguments[0].duration", api);

        //加速5倍数播放
        driver.executeScript("arguments[0].playbackRate=5", api);

        Boolean isEnded = false;

        try {
            while (!isEnded) {
                /**
                 * 监听视频播放情况
                 */
                //获取是否播放完成
                isEnded = (Boolean) driver.executeScript("return arguments[0].ended", api);

                /**
                 * 防止失去焦点暂停
                 */
                //判断是否暂停
                Boolean isPaused = (Boolean) driver.executeScript("return arguments[0].paused", api);
                if (isPaused) {
                    try {
                        driver.executeScript("arguments[0].play()", api);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //获取当前播放进度
                Object o = driver.executeScript("return arguments[0].currentTime", api);
                Double currentTime;
                if (o instanceof Long)
                    continue;

                currentTime = (Double) o;
                if (o == null) {
                    continue;
                }

                if (totalTime == null) {
                    totalTime = (Double) driver.executeScript("return arguments[0].duration", api);
                    continue;
                }

                sendMessage(TaskCode.HANDLER_CLASS_VIDEO_PROGRESS, new Progress(durationFormat(Math.round(currentTime.floatValue())), durationFormat(Math.round(totalTime.floatValue()))), userInfo, clazzInfo);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            sendMessage(TaskCode.HANDLER_EXCEPTION);
        }

        /**
         * 一节课视频播放完成
         */
        sendMessage(TaskCode.HANDLER_CLASS_COMPLETED, clazzInfo, userInfo);

        /**
         *
         */
        driver.switchTo().parentFrame().switchTo().parentFrame();
    }

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

    private byte[] screenshot(WebElement element) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = null;
        byte[] img = null;
        try {
            fullImg = ImageIO.read(screenshot);
            org.openqa.selenium.Point point = element.getLocation();
            int eleWidth = element.getSize().getWidth();
            int eleHeight = element.getSize().getHeight();
            BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            ImageIO.write(eleScreenshot, "png", screenshot);

            FileInputStream stream = new FileInputStream(screenshot);

            img = new byte[stream.available()];

            stream.read(img);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

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

    public void findCourseURL(@NotNull String courseName) {
        //进入学习空间
        driver.get("http://i.mooc.chaoxing.com/space/index.shtml");
        randomDelay(3000,5000);
        driver.switchTo().frame(driver.findElement(By.id("frame_content")));
        List<WebElement> courses = driver.findElement(By.className("ulDiv")).findElement(By.tagName("ul")).findElements(By.tagName("li"));
        //找对相应的课程名称
        //最后一个不遍历 原因是因为最后一个是添加课程
        for (int i = 0; i < courses.size() - 1; i++) {
            WebElement course = courses.get(i);
            WebElement tag_a = course.findElement(By.className("clearfix")).findElement(By.tagName("a"));
            String url = tag_a.getAttribute("href");
            String clazzName = tag_a.getText().trim();
            if (clazzName.equals(courseName.trim())) {
                driver.get(url);

//                WebElement unit_0 = driver.findElement(By.className("timeline")).findElements(By.className("units")).get(0);
//                String attribute = unit_0.findElements(By.className("leveltwo")).get(0).findElement(By.className("articlename")).findElement(By.tagName("a")).getAttribute("href");
//                sendMessage(TaskCode.HANDLER_COURSE_URL, attribute, userInfo,courseName);
                List<WebElement> clearfixs = driver.findElements(By.className("clearfix"));
                try {
                    for (WebElement clearfix : clearfixs) {
                        String chapterNumber = clearfix.findElement(By.className("chapterNumber")).getText();
                        String className = clearfix.findElement(By.className("articlename")).getText();
                        String urlCourse = clearfix.findElement(By.tagName("a")).getAttribute("href");
                        String em = clearfix.findElement(By.className("icon")).findElement(By.tagName("em")).getText();
                        ClazzBean bean = new ClazzBean();
                        bean.setClassURL(urlCourse);
                        bean.setClassName(chapterNumber + " " + className);
                        bean.setComplete(em.equals("") ? true : false);

                        sendMessage(TaskCode.HANDLER_CLASS_INFO, bean, userInfo);
                        clazzBeanList.add(bean);
                    }
                }catch (Exception e){

                }

                return;
            }
        }
        sendMessage(TaskCode.HANDLER_COURSE_URL_NO_FOUND, this, userInfo, courseName);
    }


    /**
     * 通过学校名查找该学校的登陆页面的URL。
     * @param schoolName 学校或单位的名字
     * @return           学校登陆页面的URL
     */
    public String findURLBySchool(String schoolName){
        String SEARCH_URL = "http://passport2.chaoxing.com/org/searchforms";
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("pid", "-1")
                .add("filter", schoolName)
                .build();

        Request request = new Request.Builder()
                .url(SEARCH_URL)
                .post(formBody)
                .build();

        try {
            /**
             * 同步请求 懒得搞异步了。
             */
            Response respo = client.newCall(request).execute();
            String json = respo.body().string();
            SchoolBean schoolBean;
            try{
                schoolBean = new Gson().fromJson(json, SchoolBean.class);
            }catch (Exception e){
                sendMessage(TaskCode.HANDLER_SCHOOL_URL_NOT_FOUND,"返回的数据有错误");
                return "";
            }

            if (schoolBean.isResult()){
                for(SchoolBean.FromsBean form : schoolBean.getFroms()){
                    if (form.getName().trim().equals(schoolName.trim())){
                        String result = "http://passport2.chaoxing.com/login?loginType=3&fid=" + form.getId();
                        sendMessage(TaskCode.HANDLER_SCHOOL_URL_FOUND,result);
                        return result;
                    }
                }
            }
            sendMessage(TaskCode.HANDLER_SCHOOL_URL_NOT_FOUND,"该单位/学校不存在。");
        } catch (IOException e) {
            sendMessage(TaskCode.HANDLER_SCHOOL_URL_NOT_FOUND,"网络错误");
        }
        return "";
    }
}
