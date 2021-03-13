package com.zhouhong.rabbit.producer.component;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class RabbbitSender {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	/**
	 * 这里是确认消息的回调监听接口，用于确认消息是否被 broker 所收到
	 */
	final ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		/**
		 * @param CorrelationData 作为一个唯一的标识
		 * @param ack broker是否落盘成功
		 * @param cause 失败的一些异常信息
		 */
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			System.err.println("消息ACK");
		}
	};

	/**
	 * 对外发送消息的方法
	 * @param massage 具体的消息内容
	 * @param properties 额外的属性
	 * @throws Exception
	 */
	public void send(Object message, Map<String, Object> properties) throws Exception{
		
		MessageHeaders mhs = new MessageHeaders(properties);
		Message<?> msg = MessageBuilder.createMessage(message, mhs);
		/**
		 * 使用的是confirms模式，所以在发消息之前需要监控
		 */
		rabbitTemplate.setConfirmCallback(confirmCallback);
		//指定业务唯一的ID
		CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
		
		MessagePostProcessor mpp = new MessagePostProcessor() {
			
			@Override
			public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message)
					throws AmqpException {
				System.err.println("===========post=========="+ message);
				return message;
			}
		};
		
		rabbitTemplate.convertAndSend("exchange-1", "rabbitmq.*", msg,
				 correlationData);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
