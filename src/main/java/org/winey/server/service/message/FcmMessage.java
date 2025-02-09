package org.winey.server.service.message;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmMessage {

	private boolean validateOnly;
	private Message message;

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Message{
		private Data data;
		private String token;
		private Notification notification;
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Data{
		private String title;
		private String message;
		private String feedId;
		private String notiType;
	}

	@Builder
	@AllArgsConstructor
	@Getter
	public static class Notification{
		private String title;
		private String body;
	}


}
