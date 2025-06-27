package org.brokong.morakbackend.like.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsByCommentAndUser(Comment comment, User user);

	// 개선: ID만 조회하여 N+1 해결
	@Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.comment.id IN :commentIds AND cl.user = :user")
	Set<Long> findLikedCommentIdsByCommentIdsAndUser(@Param("commentIds") List<Long> commentIds, @Param("user") User user);

	Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

	@Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user = :user")
	Set<Long> findLikedCommentIdsByUser(User user);
}