package org.winey.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.winey.server.service.message.FcmMessage;
import org.winey.server.service.message.FcmRequestDto;
import org.winey.server.service.message.SendAllFcmDto;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FcmService{

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    //
    // 메시징만 권한 설정
    @Value("${fcm.key.scope}")
    private String fireBaseScope;

    @Value("${fcm.api.url}")
    private String FCM_API_URL;

    // fcm 기본 설정 진행
    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .createScoped(List.of(fireBaseScope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            // spring 뜰때 알림 서버가 잘 동작하지 않는 것이므로 바로 죽임
            throw new RuntimeException(e.getMessage());
        }
    }



    // 알림 보내기
    public void sendByTokenList(List<String> tokenList) {

        // 메시지 만들기
        List<Message> messages = tokenList.stream().map(token -> Message.builder()
                .putData("time", LocalDateTime.now().toString())
                .setNotification(new Notification("제목", "알림 내용"))
                .setToken(token)
                .build()).collect(Collectors.toList());

        // 요청에 대한 응답을 받을 response
        BatchResponse response;
        try {

            // 알림 발송
            response = FirebaseMessaging.getInstance().sendAll(messages);

            // 요청에 대한 응답 처리
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error("List of tokens are not valid FCM token : " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("cannot send to memberList push message. error info : {}", e.getMessage());
        }
    }
    // 좋아요나 댓글 관련 알림 로직 작성
    @Async
    public CompletableFuture<Response> sendByToken(FcmRequestDto wineyNotification) throws JsonProcessingException {
        // 메시지 만들기
        String jsonMessage = makeSingleMessage(wineyNotification);
        // 요청에 대한 응답을 받을 response
        Response response;
        // 알림 발송
        response = sendPushMessage(jsonMessage);
        return CompletableFuture.completedFuture(response);
    }

    // 실제 파이어베이스 서버로 푸시 메시지를 전송하는 메서드
    private Response sendPushMessage(String message) {

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request httpRequest = new Request.Builder()
                .url(FCM_API_URL)  // 요청을 보낼 위치 (to 파이어베이스 서버)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

            Response response = client.newCall(httpRequest).execute();

            log.info("단일 기기 알림 전송 성공 ! successCount: 1 messages were sent successfully");
            log.info("알림 전송: {}", response.body().string());
            return response;
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 데 실패했습니다.");
        }
    }

    // Firebase에서 Access Token 가져오기
    private String getAccessToken() {

        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
            log.info("getAccessToken() - googleCredentials: {} ", googleCredentials.getAccessToken().getTokenValue());

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는 데 실패했습니다.");
        }
    }
    private String makeSingleMessage(FcmRequestDto wineyNotification) throws JsonProcessingException {

        try {
            FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                    .token(wineyNotification.getToken())   // 1:1 전송 시 반드시 필요한 대상 토큰 설정
                    .data(FcmMessage.Data.builder()
                        .title("위니 제국의 편지가 도착했어요.")
                        .message(wineyNotification.getMessage())
                        .feedId(String.valueOf(wineyNotification.getFeedId()))
                        .notiType(String.valueOf(wineyNotification.getType()))
                        .build())
                    .notification(FcmMessage.Notification.builder()
                        .title("위니 제국의 편지가 도착했어요.")
                        .body(wineyNotification.getMessage())
                        .build())
                    .build()
                ).validateOnly(false)
                .build();
            return new ObjectMapper().writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 처리 도중에 예외가 발생했습니다.");
        }
    }

    // private List<FcmMessage> makeCustomMessages(SendAllFcmDto wineyNotification) throws JsonProcessingException {
    //     try {
    //         List<FcmMessage> messages = wineyNotification.getTokenList()
    //                 .stream()
    //                 .map(token -> FcmMessage.builder()
    //                 .message(FcmMessage.Message.builder()
    //                         .token(token)   // 1:1 전송 시 반드시 필요한 대상 토큰 설정
    //                         .data(FcmMessage.Data.builder()
    //                                 .title("위니 제국의 편지가 도착했어요.")
    //                                 .message(wineyNotification.getMessage())
    //                                 .feedId(null)
    //                                 .notiType(null)
    //                                 .build())
    //                         .notification(FcmMessage.Notification.builder()
    //                                 .title("위니 제국의 편지가 도착했어요.")
    //                                 .body(wineyNotification.getMessage())
    //                                 .build())
    //                         .build()
    //                 ).validateOnly(false)
    //                 .build()).collect(Collectors.toList());
    //         return messages;
    //     } catch (Exception e) {
    //         throw new IllegalArgumentException("JSON 처리 도중에 예외가 발생했습니다.");
    //     }
    // }

    public CompletableFuture<BatchResponse> sendAllByTokenList(SendAllFcmDto wineyNotification) throws JsonProcessingException, FirebaseMessagingException {
        // These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = wineyNotification.getTokenList();
        try {
            MulticastMessage message = MulticastMessage.builder()
                .putData("title", wineyNotification.getTitle())
                .putData("message", wineyNotification.getMessage())
                .setNotification(new Notification(wineyNotification.getTitle(), wineyNotification.getMessage()))
                .addAllTokens(registrationTokens)
                .build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The order of responses corresponds to the order of the registration tokens.
                        failedTokens.add(registrationTokens.get(i));
                    }
                }

                System.out.println("List of tokens that caused failures: " + failedTokens);
            }
            return CompletableFuture.completedFuture(response);
        } catch (Exception e){
            log.info(e.getMessage());
        }
		return null;
	}
    private Notification convertToFirebaseNotification(FcmMessage.Notification customNotification) {
        return new Notification(customNotification.getTitle(), customNotification.getBody());
    }

}

