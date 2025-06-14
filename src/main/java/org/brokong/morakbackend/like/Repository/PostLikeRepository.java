package org.brokong.morakbackend.like.Repository;

import org.brokong.morakbackend.like.entity.PostLike;
import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);
}
