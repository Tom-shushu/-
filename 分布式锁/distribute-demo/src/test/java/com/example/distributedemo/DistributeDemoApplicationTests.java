package com.example.distributedemo;

import com.example.distributedemo.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistributeDemoApplicationTests {
    @Autowired
    private OrderService orderService;
    @Test
    public void concurrentOrder() throws InterruptedException {
        Thread.sleep(60000);
        CountDownLatch cdl = new CountDownLatch(5);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        // 创建5个线程执行下订单操作
        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i =0;i<5;i++){
            es.execute(()->{
                try {
                    // 等5个线程同时达到 await()时再执行创建订单服务，这时候5个线程会堆积到同一时间执行
                    cyclicBarrier.await();
                    Integer orderId = orderService.createOrder();
                    System.out.println("订单id："+orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    // 每个线程执行完成之后会减一
                    cdl.countDown();
                }
            });
        }
        cdl.await();
        es.shutdown();
    }
}
