package org.winey.server.config.message;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.winey.server.domain.notification.Notification;
import org.winey.server.service.FcmService;
import org.winey.server.service.message.FcmRequestDto;

import java.io.*;

@Component
@AllArgsConstructor
public class MessageQueueReceiver {

    private final FcmService fcmService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "like", type = ExchangeTypes.TOPIC),
            value = @Queue(name = "like-notification"),
            key = "like-noti")
    )
    public void likeReceiver(byte[] likeNoti){
        System.out.println("좋아요 noti receiver");
        ByteArrayInputStream bis = new ByteArrayInputStream(likeNoti);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object obj = in.readObject();
            System.out.println("여기까진 오는가");
            if (obj instanceof FcmRequestDto){
                FcmRequestDto notification = (FcmRequestDto) obj;
                fcmService.sendByToken(notification);
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            try{
                bis.close();
                if (in != null){
                    in.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "comment", type = ExchangeTypes.TOPIC),
            value = @Queue(name = "comment-notification"),
            key = "comment-noti")
    )
    public void commentReceiver(byte[] commentNoti){
        System.out.println("댓글 noti receiver");
        ByteArrayInputStream bis = new ByteArrayInputStream(commentNoti);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object obj = in.readObject();
            if (obj instanceof Notification){
                FcmRequestDto notification = (FcmRequestDto) obj;
                fcmService.sendByToken(notification);
            }
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            try{
                bis.close();
                if (in != null){
                    in.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
