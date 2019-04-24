package cn.udream.spring.eventdrive.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 16:59
 **/
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("createOrder")
	@ResponseBody
	public String createOrder() {
		Order order = new Order(1L, LocalDateTime.now(), 100);
		orderService.saveOrder(order);
		return "SUCC";
	}
}
