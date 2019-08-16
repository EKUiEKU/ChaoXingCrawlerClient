package com.acong.chaoxingcrawl.taskes;

import com.acong.chaoxingcrawl.taskes.base.BaseTask;
import com.acong.chaoxingcrawl.utils.net.DamagouUtil;
import com.acong.chaoxingcrawl.values.TaskCode;
import org.jetbrains.annotations.NotNull;

public class DamaTask extends BaseTask implements DamagouUtil.OnDamaListener{
    private DamagouUtil damagouUtil;
    private byte[] imgCode;
    private Object loginTask;
    private String type;

    /**
     *
     * @param imgCode          Base64的图片
     * @param loginTask        调用DamaTask的LoginChaoXingTask
     */
    public DamaTask(@NotNull byte[] imgCode,String type,Object loginTask){
        damagouUtil = DamagouUtil.create();
        this.imgCode = imgCode;
        this.loginTask = loginTask;
        this.type = type;
    }

    @Override
    public void run() {
        damagouUtil.dama(imgCode,type,this);
    }

    public void OnDamaSuccess(String code) {
        sendMessage(TaskCode.HANDLER_DAMA_SUCCESS,code,null,loginTask);
    }

    public void OnDamaFailure(Exception e) {
        sendMessage(TaskCode.HANDLER_DAMA_FAILURE,e,null,loginTask);
    }
}
