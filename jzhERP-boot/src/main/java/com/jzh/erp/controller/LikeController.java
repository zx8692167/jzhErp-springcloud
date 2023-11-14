package com.jzh.erp.controller;

import com.jzh.erp.dianzhan.RedisService;
import com.jzh.erp.dianzhan.UserLike;
import com.jzh.erp.utils.BaseResponseInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "redis点赞")
public class LikeController {


    @Autowired
    private RedisService redisService;

    /**
     * 这个方式是先将点赞数据写入缓存，再从缓存写入数据库。实际上应该先写入数据库（写入时用锁的方式保证并发问题），再写入缓存。查询从缓存直接查。
     * 其它场景大批量数据一次调用接口进入处理流程的时候（或者在service层）----使用线程池分批量多线程处理并发问题
     * @param userLike
     * @param request
     */
    @PostMapping(value = "/like")
    @Operation(summary = "点赞或取消点赞")
    public BaseResponseInfo doLike(@RequestBody UserLike userLike, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            System.out.println("点赞");
            res.code = 200;
            if(userLike.getStatus().equals("0")) {
                redisService.unlikeFromRedis(userLike.getLikedUserId(), userLike.getLikedPostId());
                redisService.decrementLikedCount(userLike.getLikedPostId());
                res.data = "取消点赞成功";
            }else if(userLike.getStatus().equals("1")){
                redisService.saveLiked2Redis(userLike.getLikedUserId(), userLike.getLikedPostId());
                redisService.incrementLikedCount(userLike.getLikedPostId());
                res.data = "点赞成功";
            }else{
                res.data = "点赞状态码有误";
            }
        }catch (Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;

    }
}