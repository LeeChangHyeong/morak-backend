package org.brokong.morakbackend.post.query;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.brokong.morakbackend.global.enums.SortType;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.entity.QPost;
import org.brokong.morakbackend.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Page<Post> findAllWithSorting(Pageable pageable, SortType sortBy) {
		QPost post = QPost.post;

		JPAQuery<Post> query = jpaQueryFactory
			.selectFrom(post)
			.leftJoin(post.user).fetchJoin();

		applySorting(query, post, sortBy);

		List<Post> posts = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 게시글 수 조회 - 조건 없음
		Long total = jpaQueryFactory
			.select(post.count())
			.from(post)
			.fetchOne();

		return new PageImpl<>(posts, pageable, total != null ? total : 0L);
	}

	public Page<Post> findAllByUserWithSorting(Pageable pageable, SortType sortBy, Long userId) {
		QPost post = QPost.post;
		QUser user = QUser.user;

		JPAQuery<Post> query = jpaQueryFactory
			.selectFrom(post)
			.leftJoin(post.user, user).fetchJoin()
			.where(user.id.eq(userId));

		applySorting(query, post, sortBy);

		List<Post> posts = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 특정 유저의 게시글 수만 조회 - WHERE 조건 포함!
		Long total = jpaQueryFactory
			.select(post.count())
			.from(post)
			.where(post.user.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(posts, pageable, total != null ? total : 0L);
	}

	private void applySorting(JPAQuery<Post> query, QPost post, SortType sortBy) {
		switch (sortBy) {
			case LIKE_COUNT:
				// likeCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.likeCount.desc(), post.createdAt.desc());
				break;
			case VIEW_COUNT:
				// viewCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.viewCount.desc(), post.createdAt.desc());
				break;
			case CREATED_AT_ASC:
				// 오래된 순 정렬
				query.orderBy(post.createdAt.asc());
				break;
			case CREATED_AT_DESC: // 최순 순
			default:
				// 기본적으로 createdAt으로 정렬
				query.orderBy(post.createdAt.desc());
				break;
		}
	}
}
