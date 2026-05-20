-- 种子数据：管理员账号与示例会议室
-- 默认管理员密码：admin123（BCrypt 哈希，仅用于开发/测试环境）

INSERT INTO sys_user (username, password_hash, display_name, role, enabled)
SELECT 'admin',
       '$2a$10$Jtze9UvqIYqlTMrCG1OJQ.4rzTNIqkFD4uDsPfgtBNNdXpuOs4bPS',
       '系统管理员',
       'ADMIN',
       1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

INSERT INTO meeting_room (name, capacity, floor, room_type, equipment, status, deleted)
SELECT '一号会议室',
       10,
       '3F',
       '普通会议室',
       '投影仪,白板',
       'NORMAL',
       0
WHERE NOT EXISTS (SELECT 1 FROM meeting_room WHERE name = '一号会议室' AND deleted = 0);
