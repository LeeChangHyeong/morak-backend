package org.brokong.morakbackend.like.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.like.entity.CommentLike;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsByCommentAndUser(Comment comment, User user);

	List<CommentLike> findAllByCommentIdInAndUser(List<Long> commentIds, User user);

	Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

	Set<Long> findLikedCommentIdsByUser(User user);
}