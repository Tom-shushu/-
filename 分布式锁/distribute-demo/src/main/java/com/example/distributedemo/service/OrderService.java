package com.example.distributedemo.service;

import com.example.distributedemo.dao.OrderItemMapper;
import com.example.distributedemo.dao.OrderMapper;
import com.example.distributedemo.dao.ProductMapper;
import com.example.distributedemo.model.Order;
import com.example.distributedemo.model.OrderItem;
import com.example.distributedemo.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductMapper productMapper;
    //购买商品id
    private int purchaseProductId = 100100;
    //购买商品数量
    private int purchaseProductNum = 1;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;
    private Lock lock = new ReentrantLock();
    // 注意：这边不能使用注解的方式回滚，不然会在事务提交前下一个线程会进来
//    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder() throws Exception{
        Product product = null;
        lock.lock();
        try {
            // 开启事务
            TransactionStatus transaction1 = platformTransactionManager.getTransaction(transactionDefinition);
            // 查询到所要购买的商品
            product = productMapper.selectByPrimaryKey(purchaseProductId);
            if (product==null){
                platformTransactionManager.rollback(transaction1);
                throw new Exception("购买商品："+purchaseProductId+"不存在");
            }
            // 获取商品当前库存
            Integer currentCount = product.getCount();
            System.out.println(Thread.currentThread().getName()+"库存数："+currentCount);
            // 校验库存 （购买商品数量大于库存数量，抛出异常）
            if (purchaseProductNum > currentCount){
                platformTransactionManager.rollback(transaction1);
                throw new Exception("商品"+purchaseProductId+"仅剩"+currentCount+"件，无法购买");
            }
            productMapper.updateProductCount(purchaseProductNum,"xxx",new Date(),product.getId());
            platformTransactionManager.commit(transaction1);
        }finally {
            lock.unlock();
        }
        // 创建订单
        TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);
        Order order = new Order();
        order.setOrderAmount(product.getPrice().multiply(new BigDecimal(purchaseProductNum)));
        order.setOrderStatus(1);//待处理
        order.setReceiverName("xxx");
        order.setReceiverMobile("15287653421");
        order.setCreateTime(new Date());
        order.setCreateUser("不不不不");
        order.setUpdateTime(new Date());
        order.setUpdateUser("哈哈哈哈");
        orderMapper.insertSelective(order);
        // 创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setPurchasePrice(product.getPrice());
        orderItem.setPurchaseNum(purchaseProductNum);
        orderItem.setCreateUser("不不不");
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        orderItem.setUpdateUser("哈哈哈哈");
        orderItemMapper.insertSelective(orderItem);
        // 事务提交
        platformTransactionManager.commit(transaction);
        return order.getId();
    }
}
