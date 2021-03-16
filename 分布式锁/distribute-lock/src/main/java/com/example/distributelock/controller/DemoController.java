package com.example.distributelock.controller;

import com.example.distributelock.dao.DistributeLockMapper;
import com.example.distributelock.model.DistributeLock;
import com.example.distributelock.model.DistributeLockExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class DemoController {
    @Resource
    private DistributeLockMapper distributeLockMapper;

    @RequestMapping("singleLock")
    /**
     * 没有添加 Transactional 注解前，查询分布式锁和sleep二者不是原子操作，在获取到分布式锁后自动提交事务，
     * 故不会阻止第二个请求获取锁。添加了注解后，在sleep结束前，事务一直未提交，故会等待sleep结束后再行提交事务，
     * 此时第二个请求才能从数据库中获取分布式锁
     */
    @Transactional(rollbackFor = Exception.class)
    public String singleLock() throws Exception {
        log.info("我进入了方法！");
        DistributeLock distributeLock = distributeLockMapper.selectDistributeLock("demo");
        if (distributeLock==null) throw new Exception("分布式锁找不到");
        log.info("我进入了锁！");
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "我已经执行完成！";
    }
}
