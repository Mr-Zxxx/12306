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

package org.opengoofy.index12306.biz.ticketservice;

import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Test;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.CarriageDO;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.CarriageMapper;
import org.opengoofy.index12306.framework.starter.common.enums.DelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class TrainCarriageTests {

    @Autowired
    private CarriageMapper carriageMapper;

    /**
     * 初始化 t_carriage 列车车厢数据
     * 
     * 该函数用于模拟初始化列车车厢数据，并将生成的车厢数据插入到数据库中。
     * 函数通过遍历车厢号，根据车厢号与车厢类型的映射关系，生成对应的车厢数据。
     * 
     * 主要逻辑：
     * 1. 初始化列车ID和车厢总数。
     * 2. 定义车厢类型与车厢号、座位数的映射关系。
     * 3. 遍历车厢号，生成车厢数据，并根据映射关系设置车厢类型和座位数。
     * 4. 将生成的车厢数据插入到数据库中。
     */
    @Test
    void testInitData() {
        long trainId = 3L;
        int num = 17;
        
        // 定义车厢类型与车厢号、座位数的映射关系
        Map<Integer, Map<Integer, Integer>> typeCountMap = MapUtil.of(6, MapUtil.of(5, 24));
        typeCountMap.put(7, MapUtil.of(11, 32));
        typeCountMap.put(8, MapUtil.of(17, 36));
        
        List<CarriageDO> carriageDOList = new ArrayList<>();
        
        // 遍历车厢号，生成车厢数据
        for (int i = 1; i < num; i++) {
            CarriageDO carriageDO = new CarriageDO();
            carriageDO.setTrainId(trainId);
            
            // 格式化车厢号，确保为两位数
            String carriageNumber = String.valueOf(i);
            if (i < 10) {
                carriageNumber = "0" + carriageNumber;
            }
            carriageDO.setCarriageNumber(carriageNumber);
            
            // 根据车厢号确定车厢类型和座位数
            AtomicInteger atomicInteger = new AtomicInteger(i);
            AtomicInteger carriageType = new AtomicInteger();
            AtomicInteger seatCount = new AtomicInteger();
            AtomicBoolean flag = new AtomicBoolean(Boolean.TRUE);
            
            typeCountMap.forEach((key, val) -> val.forEach((key1, val1) -> {
                if (key1 > atomicInteger.get() && flag.get()) {
                    carriageType.set(key);
                    seatCount.set(val1);
                    flag.set(Boolean.FALSE);
                }
            }));
            
            // 设置车厢类型、座位数、创建时间、更新时间等字段
            carriageDO.setCarriageType(carriageType.get());
            carriageDO.setSeatCount(seatCount.get());
            carriageDO.setCreateTime(new Date());
            carriageDO.setUpdateTime(new Date());
            carriageDO.setDelFlag(DelEnum.NORMAL.code());
            
            // 将生成的车厢数据添加到列表中
            carriageDOList.add(carriageDO);
        }
        
        // 将生成的车厢数据插入到数据库中
        carriageDOList.forEach(carriageMapper::insert);
    }
}
