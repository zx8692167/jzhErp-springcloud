package com.jzh.erp.dianzhan;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;

/**
 * 点赞的定时任务
 */
public class LikeTask extends QuartzJobBean {

    @Autowired
    private LikedService likedService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("-----------quartz------------");
        //将 Redis 里的点赞信息同步到数据库里
   //     likedService.transLikedFromRedis2DB();
    //    likedService.transLikedCountFromRedis2DB();
    }
}
