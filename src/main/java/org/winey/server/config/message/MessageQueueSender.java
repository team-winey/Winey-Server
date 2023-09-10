package org.winey.server.config.message;

import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;

import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
public class MessageQueueSender {

    final RabbitTemplate rabbitTemplate;


    @Transactional
    public void pushSender(Notification notification){
        try {
            if (notification.getNotiType() == NotiType.COMMENTNOTI) {
                rabbitTemplate.convertAndSend("comment", "comment-noti", notification);
            } else if (notification.getNotiType() == NotiType.LIKENOTI) {
                rabbitTemplate.convertAndSend("like", "like-noti", notification);
            }
        }catch (AmqpException e){
            System.out.println("메시지 전송 중 오류가 발생했습니다." + e.getMessage());
        }
    }
}
