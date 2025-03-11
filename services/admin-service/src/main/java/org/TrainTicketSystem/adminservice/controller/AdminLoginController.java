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

package org.TrainTicketSystem.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.TrainTicketSystem.adminservice.domain.dto.AdminLoginDTO;
import org.TrainTicketSystem.adminservice.domain.vo.AdminLoginVO;
import org.TrainTicketSystem.adminservice.service.IAdminLoginService;
import org.opengoofy.index12306.framework.starter.convention.result.Result;
import org.opengoofy.index12306.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

/**
 * 用户登录控制层
 */
@RestController
@RequiredArgsConstructor
public class AdminLoginController {

    private final IAdminLoginService adminLoginService;

    /**
     * 管理员登录
     */
    @PostMapping("/admin/admin-service/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO requestParam) {
//        return Results.success(adminLoginService.login(requestParam));
        return Results.success(new AdminLoginVO());
    }

    /**
     * 通过 Token 检查管理员是否登录
     */
    @GetMapping("/admin/admin-service/check-login")
    public Result<AdminLoginVO> checkLogin(@RequestParam("accessToken") String accessToken) {
//        AdminLoginVO result = adminLoginService.checkLogin(accessToken);
        AdminLoginVO result = new AdminLoginVO();
        return Results.success(result);
    }

    /**
     * 管理员退出登录
     */
    @GetMapping("/admin/admin-service/logout")
    public Result<Void> logout(@RequestParam(required = false) String accessToken) {
//        adminLoginService.logout(accessToken);
        return Results.success();
    }
}
