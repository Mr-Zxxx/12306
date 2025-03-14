package org.opengoofy.index12306.biz.orderservice.dto.req;

import lombok.Data;

import java.time.LocalDate;

//  管理端订单查询请求
@Data
public class TicketOrderQueryListDTO {

    // 订单编号
    private String orderSn;
    // 乘客姓名
    private String name;
    // 身份证号码
    private String idCard;
    // 订单日期
    private LocalDate orderDate;

}
