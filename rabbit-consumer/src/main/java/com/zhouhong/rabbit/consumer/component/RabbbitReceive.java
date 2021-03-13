package com.zhouhong.rabbit.consumer.component;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
@Component
public class RabbbitReceive {

	/**
	 * 组合使用监听
	 * @param message
	 * @param channel
	 * @throws Exception
	 */
	@RabbitListener(bindings = @QueueBinding(
				value = @Queue(value = "queue-1", durable = "true"),
				exchange = @Exchange(name = "exchange-1", 
				durable = "true", 
				type = "topic",
				ignoreDeclarationExceptions = "true"),
				key = "rabbitmq.*"
			)
			
			)
	@RabbitHandler
	public void onMessage(Message message, Channel channel) throws Exception {
		//1、收到消息以后进行业务端消费处理
		System.err.println("======================");
		System.err.println("消息消费：" + message.getPayload());
		
		//2、处理成功之后获取deliveryTay 并且进行手工的ACK操作，因为我们配置文件里面配置的是手工签收
		Long deliveryTay = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		channel.basicAck(deliveryTay, false);
	}
}














