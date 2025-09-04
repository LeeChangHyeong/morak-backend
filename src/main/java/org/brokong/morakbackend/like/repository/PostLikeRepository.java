package org.brokong.morakbackend.like.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.brokong.morakbackend.like.entity.PostLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByPostAndUser(Post post, User user);

	// 배치 조회 메서드 추가 - N+1 해결용
	@Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user = :user AND pl.post.id IN :postIds")
	List<Long> findLikedPostIdsByUserAndPostIds(@Param("user") User user, @Param("postIds") List<Long> postIds);

	Optional<PostLike> findByPostAndUser(Post post, User user);
}
