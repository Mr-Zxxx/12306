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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.StationDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationDO;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.StationMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class TrainStationTests {

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private TrainStationMapper trainStationMapper;

    /**
     * 初始化 t_train_station 列车站点数据的方法。
     * 该方法模拟了为指定列车ID初始化站点数据的过程，包括站点名称、到达时间、出发时间、停靠时间等信息，
     * 并将这些数据插入到数据库中。
     * <p>
     * 主要步骤包括：
     * 1. 创建一个包含站点信息的Map，其中每个站点包含到达站点、到达时间、出发时间和停靠时间。
     * 2. 遍历Map，为每个站点创建一个TrainStationDO对象，并设置相关属性。
     * 3. 将生成的TrainStationDO对象插入到数据库中。
     * <p>
     * 注意：该方法使用了AtomicInteger来生成站点的序列号，并确保序列号的格式正确。
     */
    @Test
    void testInitData() {
        long trainId = 4L;
        Map<String, Map<String, Object>> dataMap = new LinkedHashMap<>();
//        dataMap.put("北京", buildInnerDataMap("德州", "2025-01-01 19:16:00", "2025-01-01 19:16:00", null));
//        dataMap.put("德州", buildInnerDataMap("南京", "2025-01-01 22:19:00", "2025-01-01 22:21:00", 2));
//        dataMap.put("南京", buildInnerDataMap("嘉兴", "2025-01-02 04:46:00", "2025-01-02 04:52:00", 6));
//        dataMap.put("嘉兴", buildInnerDataMap("海宁", "2025-01-02 07:56:00", "2025-01-02 07:58:00", 2));
//        dataMap.put("海宁", buildInnerDataMap("杭州", "2025-01-02 08:14:00", "2025-01-02 08:16:00", 2));
//        dataMap.put("杭州", buildInnerDataMap(null, "2025-01-02 09:00:00", "2025-01-02 09:00:00", null));


        dataMap.put("北京", buildInnerDataMap("北京南", "2025-01-01 07:40:00", "2025-01-01 07:40:00", null));
        dataMap.put("北京南", buildInnerDataMap("济南西", "2025-01-01 07:54:00", "2025-01-01 08:00:00", 6));
        dataMap.put("济南西", buildInnerDataMap("南京南", "2025-01-01 09:24:00", "2025-01-01 09:26:00", 2));
        dataMap.put("南京南", buildInnerDataMap("上海", "2025-01-01 11:23:00", "2025-01-01 11:25:00", 2));
        dataMap.put("上海", buildInnerDataMap(null, "2025-01-01 12:32:00", "2025-01-01 12:32:00", null));

        // 使用AtomicInteger生成站点序列号，确保序列号格式正确
        AtomicInteger atomicInteger = new AtomicInteger(1);
        List<TrainStationDO> trainStationDOList = new ArrayList<>();

        // 遍历站点数据Map，为每个站点创建TrainStationDO对象并设置属性
        dataMap.forEach((key, val) -> {
            TrainStationDO trainStationDO = new TrainStationDO();
            trainStationDO.setTrainId(trainId);
            String sequence = String.valueOf(atomicInteger.get());
            if (atomicInteger.getAndIncrement() > 10) {
                sequence = "0" + sequence;
            }
            trainStationDO.setSequence(sequence);
            trainStationDO.setDeparture(key);

            // 查询站点对应的区域信息并设置到TrainStationDO对象中
            StationDO startRegionDO = stationMapper.selectOne(Wrappers.lambdaQuery(StationDO.class).eq(StationDO::getName, key));
            trainStationDO.setStartRegion(startRegionDO.getRegionName());
            trainStationDO.setStationId(startRegionDO.getId());

            // 设置到达站点和到达时间
            Object arrival = val.get("arrival");
            if (arrival != null) {
                StationDO endRegionDO = stationMapper.selectOne(Wrappers.lambdaQuery(StationDO.class).eq(StationDO::getName, arrival.toString()));
                trainStationDO.setEndRegion(endRegionDO.getRegionName());
                trainStationDO.setArrival(arrival.toString());
            }
            trainStationDO.setArrivalTime(DateUtil.parse(val.get("arrival_time").toString()));
            trainStationDO.setDepartureTime(DateUtil.parse(val.get("departure_time").toString()));

            // 设置停靠时间
            Object stopoverTime = val.get("stopover_time");
            if (stopoverTime != null) {
                trainStationDO.setStopoverTime(Integer.parseInt(stopoverTime.toString()));
            }

            // 设置创建时间和更新时间
            trainStationDO.setCreateTime(new Date());
            trainStationDO.setUpdateTime(new Date());
            trainStationDO.setDelFlag(0);

            // 将TrainStationDO对象添加到列表中
            trainStationDOList.add(trainStationDO);
        });

        // 将生成的TrainStationDO对象插入到数据库中
        trainStationDOList.forEach(trainStationMapper::insert);
    }

    /**
     * 构建内部数据映射，将有效参数填充到Map中
     *
     * @param arrival       到达地点标识（可选），非空字符串时包含在结果中
     * @param arrivalTime   到达时间字符串（可选），非空时包含在结果中，格式应符合业务要求
     * @param departureTime 出发时间字符串（可选），非空时包含在结果中，格式应符合业务要求
     * @param stopoverTime  中转停留时间（可选），非null时包含在结果中，单位应符合业务约定
     * @return 包含有效参数的Map对象，键名使用下划线命名风格，可能返回空Map（当所有参数无效时）
     */
    private Map<String, Object> buildInnerDataMap(String arrival, String arrivalTime, String departureTime, Integer stopoverTime) {
        HashMap<String, Object> innerDataMap = new HashMap<>();

        // 参数有效性检查及数据填充
        // 使用StrUtil.isNotBlank确保字符串参数既不为空也不全是空白字符
        if (StrUtil.isNotBlank(arrival)) {
            innerDataMap.put("arrival", arrival);
        }
        if (StrUtil.isNotBlank(arrivalTime)) {
            innerDataMap.put("arrival_time", arrivalTime);
        }
        if (StrUtil.isNotBlank(departureTime)) {
            innerDataMap.put("departure_time", departureTime);
        }
        // 对Integer类型参数进行null检查
        if (stopoverTime != null) {
            innerDataMap.put("stopover_time", stopoverTime);
        }
        return innerDataMap;
    }

}
