package org.brokong.morakbackend.post.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.brokong.morakbackend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@Query("""
    SELECT p FROM posts p
    JOIN FETCH p.user
    WHERE p.id = :id
""")
	Optional<Post> findByIdWithUser(@Param("id") Long id);


}
