package cn.udream.spring.eventdrive.order.events;

import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 16:22
 **/
public class OrderCreateEvent extends ApplicationEvent {

	public OrderCreateEvent(Object source) {
		super(source);
	}
}
