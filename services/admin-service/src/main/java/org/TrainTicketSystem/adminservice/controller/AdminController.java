package org.TrainTicketSystem.adminservice.controller;

import lombok.RequiredArgsConstructor;
import org.TrainTicketSystem.adminservice.service.AdminService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
}
