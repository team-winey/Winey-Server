package org.winey.server.controller.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateFcmTokenDto {
	@NotNull
	private String token;

	public UpdateFcmTokenDto of(String token) {
		return new UpdateFcmTokenDto(token);
	}
}
