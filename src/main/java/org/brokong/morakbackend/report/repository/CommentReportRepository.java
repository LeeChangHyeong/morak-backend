package org.brokong.morakbackend.report.repository;

import org.brokong.morakbackend.comment.entity.Comment;
import org.brokong.morakbackend.report.entity.CommentReport;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

	boolean existsByUserAndComment(User user, Comment comment);
}
