-- 修复 admin 测试账号密码哈希（确保为 admin123）
-- 若 V2 种子曾写入错误哈希，本脚本会强制更正

UPDATE sys_user
SET password_hash = '$2a$10$xj.416xSOWqeg26cUR/xouxQv9aw60jfIRK9vxfsSPIU9bQVYRYka'
WHERE username = 'admin';
