package org.opengoofy.index12306.biz.userservice.dto.req;

import lombok.Data;

/*
* 用户查询请求参数
*/
@Data
public class UsersQueryReqDTO {
    // 真实姓名
    private String name;
    // 身份证号
    private String idCard;
    // 手机号
    private String phone;
}
