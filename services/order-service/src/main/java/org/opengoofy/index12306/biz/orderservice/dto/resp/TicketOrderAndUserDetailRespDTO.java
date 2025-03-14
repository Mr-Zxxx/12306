package org.opengoofy.index12306.biz.orderservice.dto.resp;

import lombok.Data;

@Data
public class TicketOrderAndUserDetailRespDTO {

    // 订单号
    private String orderSn;

    // 用户名
    private String username;

    // 订单乘客姓名 order-item表
    private String realName;

    // 乘客身份证 order-item表
    private String idCard;

    // 车次
    private String trainNumber;

    // 始发站
    private String departure;

    // 目的地
    private String arrival;

    // 出发时间
    private String departureTime;

    // 到达时间
    private String arrivalTime;

    // 下单时间
    private String orderTime;

    // 支付时间
    private String payTime;

    // 订单状态 0-待支付 10-已支付 20-已进站 30-已取消 40-已退票 50-已改签
    private Integer status;

    // 订单金额 order-item表
    private String amount;

}
