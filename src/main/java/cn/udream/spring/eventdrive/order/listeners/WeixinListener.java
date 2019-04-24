package cn.udream.spring.eventdrive.order.listeners;

import cn.udream.spring.eventdrive.order.events.OrderCreateEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @description: 微信通知
 * @author: kun.zhu
 * @create: 2019-04-24 16:56
 **/
@Component
public class WeixinListener implements ApplicationListener<OrderCreateEvent> {

	@Async
	@Override
	public void onApplicationEvent(OrderCreateEvent orderCreateEvent) {
		System.out.println(orderCreateEvent.getSource() + ", weixin is sending");
	}
}
