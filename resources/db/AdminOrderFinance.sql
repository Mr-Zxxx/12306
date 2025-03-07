CREATE TABLE t_admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录账号',
    password_hash VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    full_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    gender  COMMENT '性别',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱号码',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    id_card VARCHAR(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证件号码',
    is_active BOOLEAN DEFAULT TRUE COMMENT '账户状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';