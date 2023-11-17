package org.winey.server.common.message;

import com.google.cloud.ByteArray;
import lombok.AllArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.winey.server.domain.notification.NotiType;
import org.winey.server.domain.notification.Notification;
import org.winey.server.service.message.FcmRequestDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
public class MessageQueueSender {

    final RabbitTemplate rabbitTemplate;


    public void pushSender(FcmRequestDto notification){
        System.out.println("여기는 오는건가?");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(notification);
            out.flush();
            byte[] data = bos.toByteArray();
            if (notification.getType() == NotiType.COMMENTNOTI) {
                rabbitTemplate.convertAndSend("comment", "comment-noti", data);
            } else if (notification.getType() == NotiType.LIKENOTI) {
                rabbitTemplate.convertAndSend("like", "like-noti", data);
            }
        }catch (AmqpException e){
            System.out.println("메시지 전송 중 오류가 발생했습니다." + e.getMessage());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
