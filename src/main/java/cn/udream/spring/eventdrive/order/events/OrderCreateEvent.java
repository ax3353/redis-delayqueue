package cn.udream.spring.eventdrive.order.events;

import cn.udream.spring.eventdrive.order.Order;
import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 16:22
 **/
public class OrderCreateEvent extends ApplicationEvent {

	public OrderCreateEvent(Order order) {
		super(order);
	}
}
