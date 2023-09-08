package org.winey.server.config.message;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
public class SenderModule {

    final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 1000)
    public void sender1(){
        LocalDateTime nowDateTime = LocalDateTime.now();
        String time = nowDateTime.toString();
        System.out.println("<==" + time);
        rabbitTemplate.convertAndSend("time","time-first",time);
    }
}
