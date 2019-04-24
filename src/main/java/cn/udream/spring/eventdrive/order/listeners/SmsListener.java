package cn.udream.spring.eventdrive.order.listeners;

import cn.udream.spring.eventdrive.order.Order;
import cn.udream.spring.eventdrive.order.events.OrderCreateEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @description: 短信通知
 * @author: kun.zhu
 * @create: 2019-04-24 16:56
 **/
@Component
public class SmsListener implements SmartApplicationListener {

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
		return aClass == OrderCreateEvent.class;
	}

	@Async
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		// 模拟耗时的操作
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(applicationEvent.getSource() + ", sms is sending");
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		return sourceType == Order.class;
	}

	/**
	 * 以指定优先级 数值越小 优先级越高
	 **/
	@Override
	public int getOrder() {
		return 1;
	}
}
