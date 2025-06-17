package org.brokong.morakbackend.comment.query;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.comment.entity.QComment;
import org.brokong.morakbackend.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Page<Comment> findRootCommentsByPostWithSorting(Long postId, Pageable pageable, String sortBy) {
		QComment comment = QComment.comment;
		QUser user = QUser.user;

		JPAQuery<Comment> query = jpaQueryFactory
			.selectFrom(comment)
			.leftJoin(comment.user, user).fetchJoin()
			.where(
				comment.post.id.eq(postId),
				comment.parentComment.isNull() // 루트 댓글만 조회
			);

		switch (sortBy) {
			case "likeCount":
				query.orderBy(comment.likeCount.desc(), comment.createdAt.asc());
				break;
			case "createdAt": // 최신순
				query.orderBy(comment.createdAt.desc());
				break;
			default:
				// 기본적으로 오래된 순 정렬
				query.orderBy(comment.createdAt.asc());
				break;
		}

		List<Comment> comments = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(comment.count())
			.from(comment)
			.fetchOne();

		return new PageImpl<>(comments, pageable, total != null ? total : 0L);
	}

	public Page<Comment> findRepliesByParentComment(Long parentId, Pageable pageable) {
		QComment comment = QComment.comment;
		QUser user = QUser.user;

		List<Comment> replies = jpaQueryFactory
			.selectFrom(comment)
			.leftJoin(comment.user, user).fetchJoin()
			.where(comment.parentComment.id.eq(parentId))
			.orderBy(comment.createdAt.asc()) // 오래된순 고정
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(comment.count())
			.from(comment)
			.where(comment.parentComment.id.eq(parentId))
			.fetchOne();

		return new PageImpl<>(replies, pageable, total != null ? total : 0L);
	}
}
