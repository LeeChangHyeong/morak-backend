package org.brokong.morakbackend.report.repository;

import org.brokong.morakbackend.post.entity.Post;
import org.brokong.morakbackend.report.entity.PostReport;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface
PostReportRepository extends JpaRepository<PostReport, Long> {

	boolean existsByPostAndUser(Post post, User user);
}
