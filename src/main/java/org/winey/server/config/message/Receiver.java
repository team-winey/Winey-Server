package org.winey.server.config.message;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.winey.server.domain.notification.Notification;
import org.winey.server.service.FcmService;

@Component
public class Receiver {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "time", type = ExchangeTypes.TOPIC),
            value = @Queue(name = "time-second"),
            key = "time-first")
    )
    public void receiver(String msg){
        System.out.println("안녕"+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "like", type = ExchangeTypes.TOPIC),
            value = @Queue(name = "like-notification"),
            key = "like-noti")
    )
    public void likeReceiver(Notification likeNoti){
        System.out.println("좋아요 noti receiver");

    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = "comment", type = ExchangeTypes.TOPIC),
            value = @Queue(name = "comment-notification"),
            key = "comment-noti")
    )
    public void commentReceiver(Notification newNoti){
        System.out.println("댓글 noti receiver");

    }
}
