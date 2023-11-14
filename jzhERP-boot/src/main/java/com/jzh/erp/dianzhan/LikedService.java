package com.jzh.erp.dianzhan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LikedService {
    public int save(UserLike userLike);
    public void saveAll(List<UserLike> list);

    public Page<UserLike> getLikedListByLikedUserId(String likedUserId, Pageable pageable);

    public Page<UserLike> getLikedListByLikedPostId(String likedPostId, Pageable pageable);

    public int getByLikedUserIdAndLikedPostId(String likedUserId, String likedPostId);

    public void transLikedFromRedis2DB();

    public void transLikedCountFromRedis2DB();

}
