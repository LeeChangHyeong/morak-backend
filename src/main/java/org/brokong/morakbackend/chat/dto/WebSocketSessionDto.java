package org.brokong.morakbackend.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketSessionDto {
	private String userId;
	private String nickname;
	private LocalDateTime connectedAt;
}
