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

package org.opengoofy.index12306.biz.ticketservice.toolkit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 */
@Slf4j
public final class DateUtil {

    /**
     * 计算两个日期时间之间的小时和分钟差，并以"HH:MM"格式返回。
     *
     * @param startTime 开始时间，类型为 {@link Date}，表示计算的起始时间点。
     * @param endTime 结束时间，类型为 {@link Date}，表示计算的结束时间点。
     * @return 返回一个字符串，格式为"HH:MM"，表示两个时间点之间的小时和分钟差。
     */
    public static String calculateHourDifference(Date startTime, Date endTime) {
        // 将Date对象转换为LocalDateTime对象，使用系统默认时区
        LocalDateTime startDateTime = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        // 计算两个LocalDateTime之间的时间差，返回Duration对象
        Duration duration = Duration.between(startDateTime, endDateTime);
        
        // 从Duration中提取小时和分钟部分
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        // 将小时和分钟格式化为"HH:MM"字符串并返回
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * 将给定的日期对象转换为指定格式的本地时间字符串。
     * 该函数首先将Date对象转换为LocalDateTime对象，然后使用指定的日期格式将其格式化为字符串。
     *
     * @param date    需要转换的日期对象，不能为null
     * @param pattern 目标日期格式的字符串，例如"yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串，格式由pattern参数指定
     */
    public static String convertDateToLocalTime(Date date, String pattern) {
        // 将Date对象转换为LocalDateTime对象，使用系统默认时区
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // 创建指定格式的DateTimeFormatter对象
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(pattern);
        // 使用指定的格式将LocalDateTime对象格式化为字符串并返回
        return localDateTime.format(outputFormatter);
    }


    /**
     * 主函数，程序的入口。
     * 该函数用于演示如何计算两个时间点之间的小时和分钟的差异，并将结果打印到日志中。
     * @throws Exception 如果时间解析失败或计算差异时发生错误，可能会抛出异常。
     */
    @SneakyThrows
    public static void main(String[] args) {
        // 定义起始时间和结束时间的字符串
        String startTimeStr = "2022-10-01 01:00:00";
        String endTimeStr = "2022-10-01 12:23:00";

        // 使用SimpleDateFormat解析时间字符串为Date对象
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = formatter.parse(startTimeStr);
        Date endTime = formatter.parse(endTimeStr);

        // 计算两个时间点之间的小时和分钟差异
        String calculateHourDifference = calculateHourDifference(startTime, endTime);

        // 打印计算结果的日志信息，包含起始时间、结束时间和时间差异
        log.info("开始时间：{}，结束时间：{}，两个时间相差时分：{}", startTimeStr, endTimeStr, calculateHourDifference);
    }

}
