package org.TrainTicketSystem.adminservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_admin")
public class AdminEntity {
    @TableId("admin_id")
    private String id;

    private String employeeId;

    private String username;

    @TableField(value = "password_hash")
    private String password;

    private String fullName;

    private String gender;

    private String department;

    private String email;

    private String phone;

    private String idCard;

    @TableField(value = "is_active")
    private String status;

    private String createTime;
}
