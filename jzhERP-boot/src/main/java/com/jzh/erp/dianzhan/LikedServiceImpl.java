package com.jzh.erp.dianzhan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LikedServiceImpl implements LikedService {

    @Autowired
    private RedisService redisService;
/*    @Autowired
    private UserLikeMapper userLikeMapper;
    @Autowired
    private BlogArticleMapper blogArticleMapper;*/

    @Override
    public int save(UserLike userLike) {
        /*return userLikeMapper.saveLike(userLike);*/
        return 0;
    }

    @Override
    public void saveAll(List<UserLike> list) {
        /*for (UserLike userLike : list) {
            userLikeMapper.saveLike(userLike);
        }*/
    }

    @Override
    public Page<UserLike> getLikedListByLikedUserId(String likedUserId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<UserLike> getLikedListByLikedPostId(String likedPostId, Pageable pageable) {
        return null;
    }

    @Override
    public int getByLikedUserIdAndLikedPostId(String likedUserId, String likedPostId) {
        UserLike userLike = new UserLike();
        userLike.setLikedPostId(likedPostId);
        userLike.setLikedUserId(likedUserId);
        //return userLikeMapper.searchLike(userLike);
        return 0;
    }

    @Override
    public void transLikedFromRedis2DB() {
        List<UserLike> userLikeList = redisService.getLikedDataFromRedis();
        for (UserLike like : userLikeList) {
            /*Integer userLikeExist = userLikeMapper.searchLike(like);
            if (userLikeExist > 0){
                userLikeMapper.updateLike(like);
            }else {
                like.setLikeId(IdUtil.nextId() + "");
                userLikeMapper.saveLike(like);
            }*/
        }
        System.out.println("写入数据库成功");
    }

    @Override
    public void transLikedCountFromRedis2DB() {
        List<LikedCountDTO> likedCountDTOs = redisService.getLikedCountFromRedis();

        for (LikedCountDTO dto : likedCountDTOs) {
            /*JSONObject blogArticle = blogArticleMapper.getArticleById(dto.getUserId());
            if (null != blogArticle){
                BlogArticle article = new BlogArticle();
                article.setUpdateTime(new Date());
                article.setArticleId(blogArticle.getString("articleId"));
                article.setArticleLike(blogArticle.getInteger("articleLike") + dto.getLikedNum());
                blogArticleMapper.updateArticle(article);
            }else {
                return;
            }*/
        }
        System.out.println("更新数据库成功");
    }
}

