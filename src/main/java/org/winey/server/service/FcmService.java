package org.winey.server.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.service.message.FcmRequestDto;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    public void sendByToken(FcmRequestDto wineyNotification) {
        // 메시지 만들기
        Message message = Message.builder()
                .putData("feedId", String.valueOf(wineyNotification.getFeedId()))
                .putData("notiType", String.valueOf(wineyNotification.getType()))
                .putData("title", "위니 제국의 편지가 도착했어요.")
                .putData("message" ,wineyNotification.getMessage())
                .setToken(wineyNotification.getToken())
                .build();

        // 요청에 대한 응답을 받을 response
        String response;
        try {
            // 알림 발송
            response = FirebaseMessaging.getInstance().send(message);
            System.out.println(response);
        } catch (FirebaseMessagingException e) {
            log.error("푸시 알림을 보내다가 오류가 발생했습니다. error info : {}", e.getMessage());
        }
    }

}

