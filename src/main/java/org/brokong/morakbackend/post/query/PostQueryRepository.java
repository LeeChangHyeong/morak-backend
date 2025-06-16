package org.brokong.morakbackend.post.query;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.entity.QPost;
import org.brokong.morakbackend.user.entity.QUser;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	public Page<Post> findAllWithSorting(Pageable pageable, String sortBy) {
		QPost post = QPost.post;

		JPAQuery<Post> query = jpaQueryFactory
			.selectFrom(post)
			.leftJoin(post.user).fetchJoin();

		// 동적 정렬 처리
		switch (sortBy) {
			case "likeCount":
				// likeCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.likeCount.desc(), post.createdAt.desc());
				break;
			case "viewCount":
				// viewCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.viewCount.desc(), post.createdAt.desc());
				break;
			default:
				// 기본적으로 createdAt으로 정렬
				query.orderBy(post.createdAt.desc());
				break;
		}

		List<Post> posts = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(post.count())
			.from(post)
			.fetchOne();

		return new PageImpl<>(posts, pageable, total != null ? total : 0L);
	}

	public Page<Post> findAllByUserWithSorting(Pageable pageable, String sortBy, Long userId) {
		QPost post = QPost.post;
		QUser user = QUser.user;

		JPAQuery<Post> query = jpaQueryFactory
			.selectFrom(post)
			.leftJoin(post.user, user).fetchJoin()
			.where(user.id.eq(userId));

		// 동적 정렬 처리
		switch (sortBy) {
			case "likeCount":
				// likeCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.likeCount.desc(), post.createdAt.desc());
				break;
			case "viewCount":
				// viewCount로 정렬 후 같으면 createdAt으로 정렬
				query.orderBy(post.viewCount.desc(), post.createdAt.desc());
				break;
			default:
				// 기본적으로 createdAt으로 정렬
				query.orderBy(post.createdAt.desc());
				break;
		}

		List<Post> posts = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(post.count())
			.from(post)
			.fetchOne();

		return new PageImpl<>(posts, pageable, total != null ? total : 0L);
	}
}
