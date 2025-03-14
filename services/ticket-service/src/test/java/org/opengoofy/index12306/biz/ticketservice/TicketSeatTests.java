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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.CarriageDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.SeatDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationPriceDO;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.CarriageMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.SeatMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationPriceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class TicketSeatTests {

    @Autowired
    private TrainMapper trainMapper;
    @Autowired
    private TrainStationPriceMapper trainStationPriceMapper;
    @Autowired
    private SeatMapper seatMapper;
    @Autowired
    private CarriageMapper carriageMapper;

    //初始化  t_seat 列车座位数据
    @Test
    void testInitData() {
        // 测试数据准备：固定测试列车ID
        String trainId = "2";
        // 获取基础数据：车站票价、车厢配置、列车信息
        List<TrainStationPriceDO> trainStationPrices = selectTrainStationPrices(trainId);
        List<CarriageDO> carriages = selectCarriages(trainId);
        TrainDO trainDO = trainMapper.selectById(trainId);
        // 根据列车类型执行不同初始化策略
        if (Objects.equals(trainDO.getTrainType(), 0)) {
            // 高铁类型：构建并插入三类座位数据
            List<SeatDO> businessClass = buildBusinessClass(trainStationPrices, carriages);
            businessClass.forEach(each -> seatMapper.insert(each));
            List<SeatDO> firstClass = buildFirstClass(trainStationPrices, carriages);
            firstClass.forEach(each -> seatMapper.insert(each));
            List<SeatDO> secondClass = buildSecondClass(trainStationPrices, carriages);
            secondClass.forEach(each -> seatMapper.insert(each));
        } else if (Objects.equals(trainDO.getTrainType(), 1)) {
            // 动车类型：构建并插入座位及卧铺数据
            List<SeatDO> secondClassCabinSeats = buildSecondClassCabinSeat(trainStationPrices, carriages);
            secondClassCabinSeats.forEach(each -> seatMapper.insert(each));
            List<SeatDO> firstSleepers = buildFirstSleeper(trainStationPrices, carriages);
            firstSleepers.forEach(each -> seatMapper.insert(each));
            List<SeatDO> secondSleepers = buildSecondSleeper(trainStationPrices, carriages);
            secondSleepers.forEach(each -> seatMapper.insert(each));
        } else if (Objects.equals(trainDO.getTrainType(), 2)) {
            // TODO 普通火车逻辑待完善
            List<SeatDO> secondClassCabinSeats = buildSecondClassCabinSeat(trainStationPrices, carriages);
            secondClassCabinSeats.forEach(each -> seatMapper.insert(each));
            List<SeatDO> firstSleepers = buildFirstSleeper(trainStationPrices, carriages);
            firstSleepers.forEach(each -> seatMapper.insert(each));
            List<SeatDO> secondSleepers = buildSecondSleeper(trainStationPrices, carriages);
            secondSleepers.forEach(each -> seatMapper.insert(each));
        }
    }

    /**
     * 根据列车ID查询对应的车站价格信息列表
     * 
     * @param trainId 列车ID，用于指定要查询的列车编号
     * @return 包含指定列车所有车站价格信息的数据对象列表，当无数据时返回空列表
     * 
     * 函数通过构建Lambda查询条件，使用MyBatis-Plus框架的LambdaQueryWrapper
     * 根据列车ID进行精确匹配查询，最终返回符合条件的所有车站价格记录
     */
    public List<TrainStationPriceDO> selectTrainStationPrices(String trainId) {
        // 构建Lambda条件查询构造器，设置train_id字段等于参数值
        LambdaQueryWrapper<TrainStationPriceDO> queryWrapper = Wrappers.lambdaQuery(TrainStationPriceDO.class)
                .eq(TrainStationPriceDO::getTrainId, trainId);
        
        // 执行数据库查询并返回结果列表
        return trainStationPriceMapper.selectList(queryWrapper);
    }


    /**
     * 根据列车ID查询车厢信息列表
     *
     * @param trainId 列车ID，用于指定要查询的车厢所属的列车
     * @return 返回与指定列车ID匹配的车厢信息列表，如果未找到匹配的车厢，则返回空列表
     */
    public List<CarriageDO> selectCarriages(String trainId) {
        // 构建查询条件，筛选出与指定列车ID匹配的车厢
        LambdaQueryWrapper<CarriageDO> queryWrapper = Wrappers.lambdaQuery(CarriageDO.class)
                .eq(CarriageDO::getTrainId, trainId);
        // 执行查询并返回结果
        return carriageMapper.selectList(queryWrapper);
    }

    /**
     * 构建商务座座位信息列表。
     * 
     * 该方法根据提供的列车站点价格信息和车厢信息，生成商务座座位列表。商务座座位通常位于特定类型的车厢中，
     * 并且座位编号遵循特定的规则。该方法会过滤出符合条件的车厢和座位类型，并根据价格信息生成座位对象。
     * 
     * @param trainStationPrices 列车站点价格信息列表，包含不同站点之间的价格信息
     * @param carriages 车厢信息列表，包含列车的所有车厢信息
     * @return 生成的商务座座位信息列表
     */
    private List<SeatDO> buildBusinessClass(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();
        
        // 过滤出商务座车厢，商务座车厢的类型为0
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 0))
                .toList();
        
        // 定义商务座座位的编号规则
        List<String> carriageNums = ListUtil.of("A", "C", "F");
        List<Integer> rows = ListUtil.of(1, 2);
        
        // 过滤出商务座类型的站点价格信息
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream()
                .filter(each -> Objects.equals(each.getSeatType(), 0))
                .toList();
        
        // 遍历每个商务座类型的站点价格信息，生成对应的座位信息
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }
            
            // 遍历每个商务座车厢，生成座位信息
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());
                        
                        // 跳过不符合规则的座位编号
                        if (integer == 1 && Objects.equals(num, "C")) {
                            continue;
                        }
                        
                        // 设置座位编号、类型、起始站、终点站等信息
                        seatDO.setSeatNumber("0" + integer + num);
                        seatDO.setSeatType(0);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);
                        
                        // 将生成的座位信息添加到列表中
                        seats.add(seatDO);
                    }
                }
            }
        }
        
        return seats;
    }

    /**
     * 构建一等座座位信息列表。
     * 
     * 该函数根据给定的车站价格信息和车厢信息，生成一等座的座位列表。座位信息包括车厢号、座位号、起始站、终点站、价格等。
     * 
     * @param trainStationPrices 车站价格信息列表，包含不同车站之间的价格信息
     * @param carriages 车厢信息列表，包含所有车厢的详细信息
     * @return 返回生成的一等座座位信息列表
     */
    private List<SeatDO> buildFirstClass(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();
        
        // 过滤出类型为1（一等座）的车厢
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 1))
                .toList();
        
        // 定义座位号和行号的列表
        List<String> carriageNums = ListUtil.of("A", "C", "D", "F");
        List<Integer> rows = ListUtil.of(1, 2, 3, 4, 5, 6, 7);
        
        // 过滤出座位类型为1（一等座）的车站价格信息
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream()
                .filter(each -> Objects.equals(each.getSeatType(), 1))
                .toList();
        
        // 遍历每个车站价格信息，生成对应的座位信息
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }
            
            // 遍历每个一等座车厢，生成座位信息
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());
                        seatDO.setSeatNumber("0" + integer + num);
                        seatDO.setSeatType(1);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);
                        seats.add(seatDO);
                    }
                }
            }
        }
        
        return seats;
    }

    /**
     * 构建二等座座位信息列表。
     * 
     * 该方法根据给定的车站价格信息和车厢信息，生成二等座的座位列表。座位信息包括车厢号、座位号、座位类型、起始站、终点站、价格等。
     * 
     * @param trainStationPrices 车站价格信息列表，包含不同车站之间的价格信息
     * @param carriages 车厢信息列表，包含车厢的类型、编号等信息
     * @return 返回生成的二等座座位信息列表
     */
    private List<SeatDO> buildSecondClass(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();
        
        // 过滤出类型为2（二等座）的车厢
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 2))
                .toList();
        
        // 定义车厢座位号的字母部分
        List<String> carriageNums = ListUtil.of("A", "B", "C", "D", "F");
        
        // 定义车厢座位号的行号部分
        List<Integer> rows = ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18);
        
        // 过滤出座位类型为2（二等座）的车站价格信息
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream()
                .filter(each -> Objects.equals(each.getSeatType(), 2))
                .toList();
        
        // 遍历每个车站价格信息，生成对应的座位信息
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }
            
            // 遍历每个二等座车厢，生成座位信息
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());
                        
                        // 设置座位号，行号小于10时前面补0
                        if (integer < 10) {
                            seatDO.setSeatNumber("0" + integer + num);
                        } else {
                            seatDO.setSeatNumber(integer + num);
                        }
                        
                        seatDO.setSeatType(2);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);
                        seats.add(seatDO);
                    }
                }
            }
        }
        
        return seats;
    }

    /**
     * 构建二等车厢座位信息列表。
     *
     * 该函数根据给定的车站价格信息和车厢信息，生成二等车厢的座位列表。座位信息包括车厢号、座位号、座位类型、起始站、终点站、价格等。
     *
     * @param trainStationPrices 车站价格信息列表，包含不同车站之间的价格信息
     * @param carriages 车厢信息列表，包含所有车厢的详细信息
     * @return 返回生成的二等车厢座位信息列表
     */
    private List<SeatDO> buildSecondClassCabinSeat(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();

        // 过滤出类型为3的车厢（假设3代表二等车厢）
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 3))
                .toList();

        // 定义座位号中的字母部分
        List<String> carriageNums = ListUtil.of("A", "C", "D", "F");

        // 定义座位号中的行号部分
        List<Integer> rows = ListUtil.of(1, 2, 3, 4, 5, 6);

        // 过滤出座位类型为3的车站价格信息（假设3代表二等座）
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream()
                .filter(each -> Objects.equals(each.getSeatType(), 3))
                .toList();

        // 遍历每个车站价格信息，生成对应的座位信息
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }

            // 遍历每个车厢，生成座位信息
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());

                        // 跳过特定的座位（例如第一排的C座）
                        if (integer == 1 && Objects.equals(num, "C")) {
                            continue;
                        }

                        // 设置座位号、座位类型、起始站、终点站等信息
                        seatDO.setSeatNumber("0" + integer + num);
                        seatDO.setSeatType(0);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);

                        // 将生成的座位信息添加到列表中
                        seats.add(seatDO);
                    }
                }
            }
        }

        return seats;
    }

    /**
     * 构建一等卧铺座位列表。
     * 
     * 该函数根据提供的列车站点价格信息和车厢信息，生成一等卧铺座位的列表。
     * 函数会过滤出类型为4的车厢，并根据指定的车厢编号、行号和座位号生成座位信息。
     * 每个座位的详细信息包括列车ID、车厢编号、座位号、座位类型、起始站、终点站、座位状态、价格等。
     * 
     * @param trainStationPrices 列车站点价格信息列表，包含不同站点的价格信息
     * @param carriages 车厢信息列表，包含不同车厢的详细信息
     * @return 生成的一等卧铺座位列表
     */
    private List<SeatDO> buildFirstSleeper(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();
        
        // 过滤出类型为4的车厢（一等卧铺车厢）
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 4))
                .toList();
        
        // 定义车厢内的座位编号和行号
        List<String> carriageNums = ListUtil.of("A", "C", "D", "F");
        List<Integer> rows = ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8);
        
        // 过滤出座位类型为4的列车站点价格信息
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream()
                .filter(each -> Objects.equals(each.getSeatType(), 4))
                .toList();
        
        // 遍历每个站点价格信息，生成对应的座位信息
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }
            
            // 遍历每个车厢、行号和座位编号，生成座位信息
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());
                        seatDO.setSeatNumber("0" + integer + num);
                        seatDO.setSeatType(1);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);
                        seats.add(seatDO);
                    }
                }
            }
        }
        
        return seats;
    }

    private List<SeatDO> buildSecondSleeper(List<TrainStationPriceDO> trainStationPrices, List<CarriageDO> carriages) {
        List<SeatDO> seats = new ArrayList<>();
        List<CarriageDO> actualCarriages = carriages.stream()
                .filter(each -> Objects.equals(each.getCarriageType(), 5))
                .toList();
        List<String> carriageNums = ListUtil.of("A", "C", "D", "F");
        List<Integer> rows = ListUtil.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<TrainStationPriceDO> trainStationPriceDOList = trainStationPrices.stream().filter(each -> Objects.equals(each.getSeatType(), 5)).toList();
        for (TrainStationPriceDO each : trainStationPriceDOList) {
            if (StrUtil.isEmpty(each.getArrival())) {
                continue;
            }
            for (CarriageDO carriageDO : actualCarriages) {
                for (Integer integer : rows) {
                    for (String num : carriageNums) {
                        SeatDO seatDO = new SeatDO();
                        seatDO.setTrainId(carriageDO.getTrainId());
                        seatDO.setCarriageNumber(carriageDO.getCarriageNumber());
                        if (integer < 10) {
                            seatDO.setSeatNumber("0" + integer + num);
                        } else {
                            seatDO.setSeatNumber(integer + num);
                        }
                        seatDO.setSeatType(1);
                        seatDO.setStartStation(each.getDeparture());
                        seatDO.setEndStation(each.getArrival());
                        seatDO.setSeatStatus(0);
                        seatDO.setPrice(each.getPrice());
                        seatDO.setCreateTime(new Date());
                        seatDO.setUpdateTime(new Date());
                        seatDO.setDelFlag(0);
                        seats.add(seatDO);
                    }
                }
            }
        }
        return seats;
    }
}
