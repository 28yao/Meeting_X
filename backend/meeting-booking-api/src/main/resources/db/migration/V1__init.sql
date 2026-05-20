-- 会议室预约系统初始表结构（Flyway V1）
-- 禁止 DROP TABLE / DROP DATABASE

CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username        VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password_hash   VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码哈希',
    display_name    VARCHAR(128) NOT NULL COMMENT '显示名称',
    role            VARCHAR(32)  NOT NULL COMMENT '角色：EMPLOYEE / ADMIN',
    enabled         TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用：1启用 0禁用',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

CREATE TABLE IF NOT EXISTS meeting_room (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name            VARCHAR(128) NOT NULL COMMENT '会议室名称',
    capacity        INT          NOT NULL COMMENT '容纳人数',
    floor           VARCHAR(64)  NULL COMMENT '楼层/位置',
    room_type       VARCHAR(64)  NULL COMMENT '类型',
    equipment       VARCHAR(512) NULL COMMENT '设备描述',
    status          VARCHAR(32)  NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL / MAINTENANCE',
    deleted         TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_meeting_room_status (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会议室';

CREATE TABLE IF NOT EXISTS booking (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    room_id         BIGINT       NOT NULL COMMENT '会议室ID',
    organizer_id    BIGINT       NOT NULL COMMENT '组织者用户ID',
    title           VARCHAR(256) NOT NULL COMMENT '会议主题',
    start_time      DATETIME     NOT NULL COMMENT '开始时间',
    end_time        DATETIME     NOT NULL COMMENT '结束时间',
    status          VARCHAR(32)  NOT NULL DEFAULT 'CONFIRMED' COMMENT '状态：CONFIRMED / CANCELLED',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_booking_room_time (room_id, start_time, end_time),
    KEY idx_booking_organizer_start (organizer_id, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录';

CREATE TABLE IF NOT EXISTS notification (
    id                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id             BIGINT       NOT NULL COMMENT '接收用户ID',
    type                VARCHAR(64)  NOT NULL COMMENT '通知类型',
    title               VARCHAR(256) NOT NULL COMMENT '标题',
    content             VARCHAR(1024) NULL COMMENT '内容',
    related_booking_id  BIGINT       NULL COMMENT '关联预约ID',
    read_flag           TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '已读：0未读 1已读',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_notification_user_read (user_id, read_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知';
