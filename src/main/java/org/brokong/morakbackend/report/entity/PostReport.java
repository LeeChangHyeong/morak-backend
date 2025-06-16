package org.brokong.morakbackend.report.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.brokong.morakbackend.global.entity.Report;
import org.brokong.morakbackend.post.entity.Post;

@Getter
@NoArgsConstructor
@SuperBuilder
@Entity(name = "post_reports")
public class PostReport extends Report {

	@Id
	@GeneratedValue
	@Column(name = "post_report_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;
}
