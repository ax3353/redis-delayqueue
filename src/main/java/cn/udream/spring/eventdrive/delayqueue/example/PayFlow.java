package cn.udream.spring.eventdrive.delayqueue.example;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PayFlow implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 来源订单id */
    private Long orderId;

    /** 外部交易号 */
    private Long outTradeNo;

    /** 实付总金额 */
    private BigDecimal actualAmount;

    /** 支付状态：0=待支付 1=支付成功 2=已退款 3=部分退款 */
    private Integer payStatus;

    /** 更新时间 */
    private LocalDateTime updateTime;

    public PayFlow(Long orderId, Long outTradeNo, BigDecimal actualAmount, Integer payStatus, LocalDateTime updateTime) {
        this.orderId = orderId;
        this.outTradeNo = outTradeNo;
        this.actualAmount = actualAmount;
        this.payStatus = payStatus;
        this.updateTime = updateTime;
    }

    private Abcd abcd;

    private List<Abcd> abcds;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class Abcd {
        private String s;

        private Long h;

        private Integer p;
    }
}