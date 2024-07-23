package com.mmendes.orderms.listener;

import com.mmendes.orderms.listener.dto.OrderCreateEventDto;
import com.mmendes.orderms.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static com.mmendes.orderms.config.RabbitmqConfig.ORDER_CREATE_QUEUE;

@Component
public class CreateOrderListener {

    private final Logger logger = LoggerFactory.getLogger(CreateOrderListener.class);

    private final OrderService service;

    public CreateOrderListener(OrderService service) {
        this.service = service;
    }

    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void listen(Message<OrderCreateEventDto> message){
        logger.info("Message:" + message);

        service.save(message.getPayload());
    }
}
