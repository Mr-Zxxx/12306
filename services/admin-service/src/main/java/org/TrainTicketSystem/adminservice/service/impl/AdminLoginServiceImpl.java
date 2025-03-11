package org.TrainTicketSystem.adminservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.TrainTicketSystem.adminservice.domain.po.AdminEntity;
import org.TrainTicketSystem.adminservice.mapper.AdminMapper;
import org.TrainTicketSystem.adminservice.service.IAdminLoginService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminLoginServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements IAdminLoginService {
}
