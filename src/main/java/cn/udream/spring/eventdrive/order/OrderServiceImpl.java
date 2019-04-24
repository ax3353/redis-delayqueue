package cn.udream.spring.eventdrive.order;

import cn.udream.spring.eventdrive.order.events.OrderCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 16:37
 **/
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void saveOrder(Order order) {
		System.out.println("准备创建订单");

		// 创建订单事件
		OrderCreateEvent orderCreateEvent = new OrderCreateEvent(order);
		// 利用applicationContext将事件发布
		applicationContext.publishEvent(orderCreateEvent);

		System.out.println("订单创建成功");
	}
}
