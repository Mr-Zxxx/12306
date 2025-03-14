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

package org.opengoofy.index12306.framework.starter.designpattern.strategy;

import org.opengoofy.index12306.framework.starter.bases.ApplicationContextHolder;
import org.opengoofy.index12306.framework.starter.bases.init.ApplicationInitializingEvent;
import org.opengoofy.index12306.framework.starter.convention.exception.ServiceException;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 策略选择器
 */
public class AbstractStrategyChoose implements ApplicationListener<ApplicationInitializingEvent> {

    /**
     * 执行策略集合
     */
    private final Map<String, AbstractExecuteStrategy> abstractExecuteStrategyMap = new HashMap<>();

    /**
     * 根据策略标识和匹配范解析标识选择具体的执行策略。
     *
     * 如果 predicateFlag 为 true，则遍历所有策略，使用正则表达式匹配策略的 patternMatchMark 和传入的 mark，
     * 返回第一个匹配的策略。如果未找到匹配的策略，则抛出 ServiceException 异常。
     *
     * 如果 predicateFlag 为 false 或 null，则直接从 abstractExecuteStrategyMap 中根据 mark 查找对应的策略。
     * 如果未找到对应的策略，则抛出 ServiceException 异常。
     *
     * @param mark          策略标识，用于查找具体的执行策略
     * @param predicateFlag 匹配范解析标识，决定是否使用正则表达式进行匹配
     * @return 实际执行的策略对象
     * @throws ServiceException 如果未找到匹配的策略，则抛出此异常
     */
    public AbstractExecuteStrategy choose(String mark, Boolean predicateFlag) {
        // 如果 predicateFlag 为 true，使用正则表达式匹配策略
        if (predicateFlag != null && predicateFlag) {
            return abstractExecuteStrategyMap.values().stream()
                    // 过滤出具有有效模式匹配标记的策略
                    .filter(each -> StringUtils.hasText(each.patternMatchMark()))
                    // 使用正则表达式匹配给定的标记与策略的模式匹配标记
                    .filter(each -> Pattern.compile(each.patternMatchMark()).matcher(mark).matches())
                    // 返回第一个匹配的策略实例
                    .findFirst()
                    // 如果没有找到匹配的策略，则抛出ServiceException异常
                    .orElseThrow(() -> new ServiceException("策略未定义"));
        }

        // 如果 predicateFlag 为 false 或 null，直接从 map 中查找策略
        return Optional.ofNullable(abstractExecuteStrategyMap.get(mark))
                .orElseThrow(() -> new ServiceException(String.format("[%s] 策略未定义", mark)));
    }


    /**
     * 根据 mark 查询具体策略并执行
     *
     * @param mark         策略标识
     * @param requestParam 执行策略入参
     * @param <REQUEST>    执行策略入参范型
     */
    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark, null);
        executeStrategy.execute(requestParam);
    }

    /**
     * 根据 mark 查询具体策略并执行
     *
     * @param mark          策略标识
     * @param requestParam  执行策略入参
     * @param predicateFlag 匹配范解析标识
     * @param <REQUEST>     执行策略入参范型
     */
    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam, Boolean predicateFlag) {
        AbstractExecuteStrategy executeStrategy = choose(mark, predicateFlag);
        executeStrategy.execute(requestParam);
    }

    /**
     * 根据指定的标记选择并执行相应的策略，返回执行结果。
     *
     * @param mark         策略标识
     * @param requestParam 执行策略入参
     * @param <REQUEST>    执行策略入参范型
     * @param <RESPONSE>   执行策略出参范型
     * @return
     */
    public <REQUEST, RESPONSE> RESPONSE chooseAndExecuteResp(String mark, REQUEST requestParam) {
        // 根据标记选择相应的执行策略
        AbstractExecuteStrategy executeStrategy = choose(mark, null);
        // 执行策略并返回结果
        return (RESPONSE) executeStrategy.executeResp(requestParam);
    }

    /**
     * 处理应用程序初始化事件，加载并注册所有实现了 {@link AbstractExecuteStrategy} 接口的 Bean。
     * 该方法会在应用程序初始化时被调用，确保所有执行策略都被正确加载并避免重复注册。
     * @param event 应用程序初始化事件，包含应用程序上下文的相关信息。
     */
    @Override
    public void onApplicationEvent(ApplicationInitializingEvent event) {
        // 从应用上下文中获取所有实现了 AbstractExecuteStrategy 接口的 Bean
        Map<String, AbstractExecuteStrategy> actual = ApplicationContextHolder.getBeansOfType(AbstractExecuteStrategy.class);

        // 遍历所有获取到的执行策略 Bean
        actual.forEach((beanName, bean) -> {
            // 检查当前策略是否已经存在于策略映射中
            AbstractExecuteStrategy beanExist = abstractExecuteStrategyMap.get(bean.mark());
            if (beanExist != null) {
                // 如果存在重复策略，抛出异常
                throw new ServiceException(String.format("[%s] Duplicate execution policy", bean.mark()));
            }
            // 将策略注册到策略映射中
            abstractExecuteStrategyMap.put(bean.mark(), bean);
        });
    }
}
