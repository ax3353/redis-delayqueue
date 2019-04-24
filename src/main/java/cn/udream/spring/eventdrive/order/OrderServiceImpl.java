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
	public void saveOrder() {
		System.out.println("订单创建成功");

		// 创建订单事件
		OrderCreateEvent orderCreateEvent = new OrderCreateEvent("order create is success");
		// 利用applicationContext将事件发布
		applicationContext.publishEvent(orderCreateEvent);
	}
}
