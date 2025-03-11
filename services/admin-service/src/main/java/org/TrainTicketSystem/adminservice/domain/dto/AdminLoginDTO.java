package org.TrainTicketSystem.adminservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginDTO {
    /**
     * 用户名
     */
    private String adminCount;

    /**
     * 密码
     */
    private String password;

}
