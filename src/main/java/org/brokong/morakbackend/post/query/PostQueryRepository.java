package org.brokong.morakbackend.post.query;

import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.post.entity.QPost;
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
			case "likeCount" -> query.orderBy(post.likeCount.desc());
			case "viewCount" -> query.orderBy(post.viewCount.desc());
			default -> query.orderBy(post.createdAt.desc());
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
