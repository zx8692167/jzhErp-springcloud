package com.jzh.erp.dianzhan;


import java.util.List;

public interface RedisService {

        public void saveLiked2Redis(String likedUserId, String likedPostId);

        public void unlikeFromRedis(String likedUserId, String likedPostId);

        public void deleteLikedFromRedis(String likedUserId, String likedPostId);

        public void incrementLikedCount(String likedUserId);

        public void decrementLikedCount(String likedUserId);

        public List<UserLike> getLikedDataFromRedis();

        public List<LikedCountDTO> getLikedCountFromRedis();

}
