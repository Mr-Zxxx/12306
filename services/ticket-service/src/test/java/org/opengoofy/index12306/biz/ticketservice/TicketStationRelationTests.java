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

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.jupiter.api.Test;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationDO;
import org.opengoofy.index12306.biz.ticketservice.dao.entity.TrainStationRelationDO;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationMapper;
import org.opengoofy.index12306.biz.ticketservice.dao.mapper.TrainStationRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class TicketStationRelationTests {

    @Autowired
    private TrainStationMapper trainStationMapper;

    @Autowired
    private TrainStationRelationMapper trainStationRelationMapper;

    /**
     * 初始化 t_train_station_relation  列车站点数据的功能。
     * 该函数通过给定的列车ID获取列车站点信息，构建站点之间的关系，并将这些关系插入到数据库中。
     * 
     * 主要步骤：
     * 1. 根据列车ID查询列车站点信息。
     * 2. 基于查询到的站点信息构建站点之间的关系。
     * 3. 将构建好的站点关系插入到数据库中。
     */
    @Test
    void testInitData() {
        // 获取指定列车ID的站点信息
        String trainId = "4";
        List<TrainStationDO> trainStations = selectTrainStations(trainId);
        
        // 构建站点之间的关系
        List<TrainStationRelationDO> trainStationRelations = buildTrainStationRelations(trainStations);
        
        // 将站点关系插入到数据库中
        trainStationRelations.forEach(each -> trainStationRelationMapper.insert(each));
    }

    /**
     * 根据列车ID查询列车站点信息
     * 
     * 该函数通过列车ID查询数据库中对应的列车站点信息，并返回站点信息列表。
     * 
     * @param trainId 列车ID，用于查询对应的列车站点信息
     * @return 返回与列车ID对应的列车站点信息列表，如果未找到则返回空列表
     */
    private List<TrainStationDO> selectTrainStations(String trainId) {
        // 创建Lambda查询条件，查询TrainStationDO表中trainId字段等于传入的trainId的记录
        LambdaQueryWrapper<TrainStationDO> queryWrapper = Wrappers.lambdaQuery(TrainStationDO.class)
                .eq(TrainStationDO::getTrainId, trainId);
        
        // 执行查询并返回结果列表
        List<TrainStationDO> trainStationDOS = trainStationMapper.selectList(queryWrapper);
        return trainStationDOS;
    }

    private List<TrainStationRelationDO> buildTrainStationRelations(List<TrainStationDO> trainStations) {
        List<TrainStationRelationDO> result = new ArrayList<>();
        // 外层循环遍历所有起始站点（排除最后一个站点）
        for (int i = 0; i < trainStations.size() - 1; i++) {
            TrainStationDO trainStationDO = trainStations.get(i);
            // 内层循环生成当前起始站点与后续所有到达站点的关系
            for (int j = i + 1; j < trainStations.size(); j++) {
                TrainStationRelationDO actual = new TrainStationRelationDO();
                // 设置基础运行信息
                actual.setTrainId(trainStationDO.getTrainId());
                actual.setDeparture(trainStations.get(i).getDeparture());
                actual.setArrival(trainStations.get(j).getDeparture());
                actual.setArrivalTime(trainStations.get(j).getArrivalTime());
                // 设置地理区域范围
                actual.setStartRegion(trainStations.get(i).getStartRegion());
                actual.setEndRegion(trainStations.get(j).getStartRegion());
                // 设置时间信息及基础字段
                actual.setDepartureTime(trainStations.get(i).getDepartureTime());
                actual.setCreateTime(new Date());
                actual.setUpdateTime(new Date());
                actual.setDelFlag(0);
                // 设置站点标记：第一个站点为发车站，到达终点站时设置到达标记
                actual.setDepartureFlag(i == 0);
                TrainStationDO last = CollUtil.getLast(trainStations);
                String departure = trainStations.get(j).getDeparture();
                actual.setArrivalFlag(Objects.equals(departure, last.getDeparture()));
                result.add(actual);
            }
        }
        return result;
    }
}
