package org.brokong.morakbackend.report.repository;

import org.brokong.morakbackend.report.entity.UserReport;
import org.brokong.morakbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

	boolean existsByUserAndTargetUser(User user, User targetUser);
}
