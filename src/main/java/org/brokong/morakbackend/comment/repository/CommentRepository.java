package org.brokong.morakbackend.comment.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Set;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
		    SELECT c
		    FROM Comment c
		    JOIN FETCH c.user
		    LEFT JOIN FETCH c.parentComment
		    WHERE c.post = :post
		    ORDER BY c.createdAt
		""")
	List<Comment> findAllByPostWithUserAndParent(@Param("post") Post post);

	boolean existsByParentComment(Comment parentComment);

	// 배치 조회 메서드 추가 - N+1 해결용
	@Query("SELECT DISTINCT c.parentComment.id FROM Comment c WHERE c.parentComment.id IN :parentIds")
	Set<Long> findParentCommentIdsWithChildren(@Param("parentIds") List<Long> parentIds);
}
