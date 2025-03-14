/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengoofy.index12306.biz.orderservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.opengoofy.index12306.biz.orderservice.dao.entity.OrderDO;
import org.opengoofy.index12306.biz.orderservice.dto.req.TicketOrderQueryListDTO;
import org.opengoofy.index12306.biz.orderservice.dto.resp.TicketOrderAndUserDetailRespDTO;

import java.util.Date;
import java.util.List;

/**
 * 订单持久层
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {

    //根据身份证查询订单
    @Select("SELECT t_order.* FROM t_order ,t_order_item WHERE t_order_item.order_sn = t_order.order_sn AND t_order_item.id_card = #{idCard} ")
    List<OrderDO> selectByIDCard(String idCard);

    List<TicketOrderAndUserDetailRespDTO> selectOrderAndUserInfoListByCondition(TicketOrderQueryListDTO requestParam);

}
