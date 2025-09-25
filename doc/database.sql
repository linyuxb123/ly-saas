-- 数据库创建
CREATE DATABASE IF NOT EXISTS ly_saas DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE ly_saas;

-- 用户表
CREATE TABLE IF NOT EXISTS saas_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 部门表
CREATE TABLE IF NOT EXISTS saas_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    ancestors VARCHAR(100) DEFAULT '' COMMENT '祖级列表',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 初始化数据
INSERT INTO saas_dept(id, dept_name, parent_id, ancestors, order_num, leader, status)
VALUES (1, '总公司', 0, '0', 0, 'admin', 1);

INSERT INTO saas_user(id, username, password, real_name, email, phone, dept_id, status)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 'admin@example.com', '13800138000', 1, 1);