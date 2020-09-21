package cn.udream.spring.eventdrive.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 16:59
 **/
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("createOrder")
    public String createOrder() {
        Order order = new Order(1L, LocalDateTime.now(), 100);
        orderService.saveOrder(order);
        return "SUCC!!!";
    }

}
