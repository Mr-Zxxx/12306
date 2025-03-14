package org.TrainTicketSystem.adminservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.TrainTicketSystem.adminservice.domain.po.Admin;
import org.TrainTicketSystem.adminservice.service.AdminService;
import org.TrainTicketSystem.adminservice.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author MrZ
* @description 针对表【t_admin(管理员表)】的数据库操作Service实现
* @createDate 2025-03-13 15:12:23
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




