package com.zhouhong.rabbit.producer.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zhouhong.rabbit.producer.component.RabbbitSender;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

	@Autowired
	private RabbbitSender rabbbitSender;
	
	@Test
	public void testSender() throws Exception{
		Map<String , Object> properties = new HashMap<String, Object>();
		properties.put("key1", "你好呀，RabbitMQ!!");
		properties.put("key2", "你好呀，Kafka!!");
		rabbbitSender.send("rabbitmq-test", properties);
	
		Thread.sleep(10000);
	}
}
