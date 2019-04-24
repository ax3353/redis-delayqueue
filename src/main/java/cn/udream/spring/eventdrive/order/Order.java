package cn.udream.spring.eventdrive.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2019-04-24 17:41
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Order {

	private long id;

	private LocalDateTime createTime;

	private int amount;
}
