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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.opengoofy.index12306.biz.ticketservice.common.enums.VehicleTypeEnum;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationPriceDO;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class TicketStationPriceTests {

    @Autowired
    private TrainMapper trainMapper;

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Autowired
    private TrainStationPriceMapper trainStationPriceMapper;

    // 初始化 t_train_station_price 车票价格
    @Test
    void testInitData() {
        String trainId = "4";
        Map<String, Map<Integer, Integer>> priceMap = new HashMap<>();
        // 初始化动车数据
        // 根据图片中的票价信息构建价格映射表
        priceMap.put("北京-北京南", buildBulletPriceMap_G(1100, 1800, 3900));
        priceMap.put("北京-济南西", buildBulletPriceMap_G(22800, 36500, 79900));
        priceMap.put("北京-南京南", buildBulletPriceMap_G(53700, 85900, 187800));
        priceMap.put("北京-上海", buildBulletPriceMap_G(67300, 107600, 235400));
        priceMap.put("北京南-济南西", buildBulletPriceMap_G(21700, 34700, 76000));
        priceMap.put("北京南-南京南", buildBulletPriceMap_G(52600, 84100, 183900));
        priceMap.put("北京南-上海", buildBulletPriceMap_G(66200, 105800, 231500));
        priceMap.put("济南西-南京南", buildBulletPriceMap_G(30900, 49400, 107900));
        priceMap.put("济南西-上海", buildBulletPriceMap_G(44500, 71100, 155500));
        priceMap.put("南京南-上海", buildBulletPriceMap_G(13600, 21700, 47600));

        List<TrainStationDO> trainStations = selectTrainStations(trainId);
        List<TrainStationPriceDO> trainStationPrices = buildTrainStationPrices(trainId, priceMap, trainStations);
        trainStationPrices.forEach(each -> trainStationPriceMapper.insert(each));
    }

    /**
     * 根据列车ID查询列车点信息
     * <p>
     * 该方法通过传入的列车ID，查询数据库中与该列车相关的所有点信息。
     *
     * @param trainId 列车ID，用于查询与该列车相关的点信息
     * @return 返回一个包含所有与列车ID匹配的点信息的列表，列表中的每个元素为TrainStationDO对象
     */
    private List<TrainStationDO> selectTrainStations(String trainId) {
        // 创建Lambda查询条件，查询TrainStationDO表中与传入的trainId匹配的记录
        LambdaQueryWrapper<TrainStationDO> queryWrapper = Wrappers.lambdaQuery(TrainStationDO.class)
                .eq(TrainStationDO::getTrainId, trainId);

        // 执行查询并返回结果列表
        return trainStationMapper.selectList(queryWrapper);
    }

    /**
     * 构建列车点价格列表
     * <p>
     * 该函数根据列车ID、价格映射表和列车点列表，生成列车点之间的价格信息。
     * 价格信息包括出发、到达、座位类型和对应的价格。
     *
     * @param trainId       列车ID，用于查询列车类型
     * @param priceMap      价格映射表，键为"出发-到达"，值为座位类型到价格的映射
     * @param trainStations 列车点列表，包含所有点的信息
     * @return 返回生成的列车点价格列表，包含所有可能的点组合和座位类型的价格信息
     */
    private List<TrainStationPriceDO> buildTrainStationPrices(String trainId, Map<String, Map<Integer, Integer>> priceMap, List<TrainStationDO> trainStations) {
        // 根据列车ID查询列车信息，获取列车类型
        TrainDO trainDO = trainMapper.selectById(trainId);
        Integer trainType = trainDO.getTrainType();
        // 根据列车类型获取所有可能的座位类型
        List<Integer> seatTypes = VehicleTypeEnum.findSeatTypesByCode(trainType);
        // 初始化结果列表
        List<TrainStationPriceDO> result = new ArrayList<>();
        // 遍历所有点组合，生成价格信息
        for (int i = 0; i < trainStations.size() - 1; i++) {
            TrainStationDO trainStationDO = trainStations.get(i);
            for (int j = i + 1; j < trainStations.size(); j++) {
                // 遍历所有座位类型，为每个点组合生成价格信息
                for (Integer seatType : seatTypes) {
                    TrainStationPriceDO actual = new TrainStationPriceDO();
                    actual.setTrainId(trainStationDO.getTrainId());
                    String departure = trainStations.get(i).getDeparture();
                    actual.setDeparture(departure);
                    String arrival = trainStations.get(j).getDeparture();
                    actual.setArrival(arrival);
                    // 从价格映射表中获取对应点组合和座位类型的价格
                    Map<Integer, Integer> integerIntegerMap = priceMap.get(departure + "-" + arrival);
                    //TODO
                    Integer price = integerIntegerMap.get(seatType);
                    actual.setPrice(price);
                    actual.setSeatType(seatType);
                    actual.setCreateTime(new Date());
                    actual.setUpdateTime(new Date());
                    actual.setDelFlag(0);
                    // 将生成的价格信息添加到结果列表中
                    result.add(actual);
                }
            }
        }
        // 返回生成的列车点价格列表
        return result;
    }

    private Map<Integer, Integer> buildBulletPriceMap(Integer secondClassCabinSeat, Integer firstSleeper, Integer secondSleeper, Integer noSeatSleeper) {
        Map<Integer, Integer> priceMap = new HashMap<>();
        // priceMap.put(3, secondClassCabinSeat);
        // priceMap.put(4, firstSleeper);
        // priceMap.put(5, secondSleeper);
        // priceMap.put(10, noSeatSleeper);
        priceMap.put(6, secondClassCabinSeat);
        priceMap.put(7, firstSleeper);
        priceMap.put(8, secondSleeper);
        priceMap.put(10, noSeatSleeper);
        return priceMap;
    }

    private Map<Integer, Integer> buildBulletPriceMap_G(Integer secondClass, Integer firstClass, Integer business) {
        Map<Integer, Integer> priceMap = new HashMap<>();
        priceMap.put(0, business);
        priceMap.put(1, firstClass);
        priceMap.put(2, secondClass);
//        priceMap.put(3, secondClassCabinSeat);
//        priceMap.put(4, firstSleeper);
//        priceMap.put(5, secondSleeper);
//        priceMap.put(6, secondClassCabinSeat);
//        priceMap.put(7, firstSleeper);
//        priceMap.put(8, secondSleeper);
//        priceMap.put(13, noSeatSleeper);
        return priceMap;
    }
}
