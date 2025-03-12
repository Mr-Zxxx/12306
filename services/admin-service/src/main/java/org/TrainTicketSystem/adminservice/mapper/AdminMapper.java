package org.TrainTicketSystem.adminservice.mapper;

import org.TrainTicketSystem.adminservice.domain.po.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author MrZ
* @description 针对表【t_admins(员工表)】的数据库操作Mapper
* @createDate 2025-03-12 00:52:45
* @Entity org.TrainTicketSystem.adminservice.domain.po.Admin
*/
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

}




