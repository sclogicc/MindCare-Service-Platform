USE mindcare_service_platform;

-- =========================================================
-- 心理咨询服务预约平台 - 中文演示数据初始化脚本
-- 说明：
-- 1. 该脚本用于替换原有英文测试数据
-- 2. 保留 admin / counselor01 / user01 等账号命名，便于沿用现有登录说明
-- 3. 所有姓名、擅长方向、预约备注、咨询记录、反馈评价均改为中文
-- 4. 数据量较之前更多，便于演示分页、状态流转、报表统计等功能
-- =========================================================

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM feedback;
DELETE FROM consultation_record;
DELETE FROM appointment;
DELETE FROM counselor_schedule;
DELETE FROM counselor;
DELETE FROM upload_file;
DELETE FROM sys_user;

ALTER TABLE feedback AUTO_INCREMENT = 1;
ALTER TABLE consultation_record AUTO_INCREMENT = 1;
ALTER TABLE appointment AUTO_INCREMENT = 1;
ALTER TABLE counselor_schedule AUTO_INCREMENT = 1;
ALTER TABLE counselor AUTO_INCREMENT = 1;
ALTER TABLE upload_file AUTO_INCREMENT = 1;
ALTER TABLE sys_user AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- 一、系统用户
-- role：1-管理员 2-咨询师 3-普通用户
-- gender：1-男 2-女
-- =========================================================
INSERT INTO sys_user (id, username, password, name, phone, gender, role, status, create_time, update_time) VALUES
(1,  'admin',       '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '沈砚书', '13810000001', 1, 1, 1, NOW(), NOW()),

(2,  'counselor01', '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '周岚',   '13810000002', 2, 2, 1, NOW(), NOW()),
(3,  'counselor02', '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '许书宁', '13810000003', 2, 2, 1, NOW(), NOW()),
(4,  'counselor03', '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '顾清禾', '13810000004', 1, 2, 1, NOW(), NOW()),
(5,  'counselor04', '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '韩知夏', '13810000005', 2, 2, 1, NOW(), NOW()),
(6,  'counselor05', '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '苏晚澄', '13810000006', 2, 2, 1, NOW(), NOW()),

(7,  'user01',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '赵安宁', '13810000007', 2, 3, 1, NOW(), NOW()),
(8,  'user02',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '林知远', '13810000008', 1, 3, 1, NOW(), NOW()),
(9,  'user03',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '何沐晨', '13810000009', 1, 3, 1, NOW(), NOW()),
(10, 'user04',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '宋语彤', '13810000010', 2, 3, 1, NOW(), NOW()),
(11, 'user05',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '唐嘉禾', '13810000011', 1, 3, 1, NOW(), NOW()),
(12, 'user06',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '陆清越', '13810000012', 1, 3, 1, NOW(), NOW()),
(13, 'user07',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '程以宁', '13810000013', 2, 3, 1, NOW(), NOW()),
(14, 'user08',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '谢知遥', '13810000014', 2, 3, 1, NOW(), NOW()),
(15, 'user09',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '温书瑶', '13810000015', 2, 3, 1, NOW(), NOW()),
(16, 'user10',      '$2a$10$2.v/jPYPMU0sReHmpsQQP.KMlZXZJUOwsHRZOv8iw0Z4HauyQWLDq', '乔景川', '13810000016', 1, 3, 1, NOW(), NOW());

-- =========================================================
-- 二、咨询师业务信息
-- =========================================================
INSERT INTO counselor (id, user_id, specialty, title, years_of_experience, introduction, avatar_file_id, status, create_time, update_time) VALUES
(1, 2, '情绪疏导、职场压力调适', '国家二级心理咨询师', 6, '擅长帮助来访者识别压力来源，建立稳定的情绪调节习惯。', NULL, 1, NOW(), NOW()),
(2, 3, '亲密关系、家庭沟通',     '婚姻家庭咨询师',     8, '长期关注伴侣沟通、家庭边界和亲子互动中的关系修复问题。', NULL, 1, NOW(), NOW()),
(3, 4, '自我认同、成长困惑',     '心理咨询师',         5, '善于陪伴青年群体梳理成长阶段的迷茫、自我评价和方向焦虑。', NULL, 1, NOW(), NOW()),
(4, 5, '睡眠困扰、焦虑管理',     '精神卫生辅导师',     7, '聚焦睡眠质量改善、焦虑应对和生活节律重建。', NULL, 1, NOW(), NOW()),
(5, 6, '校园适应、人际边界',     '青少年心理辅导师',   4, '面向学生与初入社会群体，帮助处理同伴关系和自我边界问题。', NULL, 1, NOW(), NOW());

-- =========================================================
-- 三、附件数据
-- 主要服务于咨询记录附件场景
-- =========================================================
INSERT INTO upload_file (id, original_name, file_name, file_url, file_type, business_type, uploader_id, create_time, update_time) VALUES
(1, '首次沟通纪要.pdf',     'consult-record-001.pdf', 'https://mindcare-demo.oss-cn-beijing.aliyuncs.com/consult-record-001.pdf', 'pdf',  'CONSULTATION_ATTACHMENT', 3, NOW(), NOW()),
(2, '情绪观察表.docx',      'consult-record-002.docx', 'https://mindcare-demo.oss-cn-beijing.aliyuncs.com/consult-record-002.docx', 'docx', 'CONSULTATION_ATTACHMENT', 5, NOW(), NOW()),
(3, '家庭沟通摘要.pdf',     'consult-record-003.pdf', 'https://mindcare-demo.oss-cn-beijing.aliyuncs.com/consult-record-003.pdf', 'pdf',  'CONSULTATION_ATTACHMENT', 2, NOW(), NOW()),
(4, '睡眠作息建议单.docx', 'consult-record-004.docx', 'https://mindcare-demo.oss-cn-beijing.aliyuncs.com/consult-record-004.docx', 'docx', 'CONSULTATION_ATTACHMENT', 6, NOW(), NOW());

-- =========================================================
-- 四、咨询师可预约时间段
-- 说明：
-- 1. 过去日期用于生成“已完成 / 已取消”演示数据
-- 2. 未来日期用于生成“待确认 / 已确认 / 已取消”演示数据
-- =========================================================
INSERT INTO counselor_schedule (id, counselor_id, schedule_date, start_time, end_time, status, remark, create_time, update_time) VALUES
-- 周岚
(1,  1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '10:00:00', 1, '职场压力主题专场', NOW(), NOW()),
(2,  1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:30:00', '11:30:00', 1, '情绪调节常规咨询', NOW(), NOW()),
(3,  1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', '15:00:00', 1, '工作倦怠疏导时段', NOW(), NOW()),
(4,  1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '19:00:00', '20:00:00', 1, '晚间减压咨询', NOW(), NOW()),
(5,  1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '19:00:00', '20:00:00', 1, '复盘型咨询时段', NOW(), NOW()),

-- 许书宁
(6,  2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '15:00:00', '16:00:00', 1, '关系修复回访时段', NOW(), NOW()),
(7,  2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '15:00:00', '16:00:00', 1, '伴侣沟通预约时段', NOW(), NOW()),
(8,  2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00:00', '10:00:00', 1, '家庭边界主题咨询', NOW(), NOW()),
(9,  2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '20:00:00', '21:00:00', 1, '晚间关系议题咨询', NOW(), NOW()),
(10, 2, DATE_SUB(CURDATE(), INTERVAL 4 DAY), '10:00:00', '11:00:00', 1, '家庭矛盾梳理时段', NOW(), NOW()),

-- 顾清禾
(11, 3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '10:00:00', '11:00:00', 1, '成长困惑回顾咨询', NOW(), NOW()),
(12, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '13:00:00', '14:00:00', 1, '自我认同主题咨询', NOW(), NOW()),
(13, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '16:00:00', '17:00:00', 1, '学习与发展方向咨询', NOW(), NOW()),
(14, 3, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '09:00:00', '10:00:00', 1, '周末成长议题咨询', NOW(), NOW()),
(15, 3, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '15:00:00', '16:00:00', 1, '阶段性复盘咨询', NOW(), NOW()),

-- 韩知夏
(16, 4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '19:00:00', '20:00:00', 1, '睡眠困扰评估时段', NOW(), NOW()),
(17, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '08:30:00', '09:30:00', 1, '晨间焦虑管理时段', NOW(), NOW()),
(18, 4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '18:00:00', '19:00:00', 1, '压力释放训练咨询', NOW(), NOW()),
(19, 4, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '14:00:00', '15:00:00', 1, '睡眠习惯调整咨询', NOW(), NOW()),
(20, 4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), '09:00:00', '10:00:00', 1, '焦虑状态评估时段', NOW(), NOW()),

-- 苏晚澄
(21, 5, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '11:00:00', '12:00:00', 1, '校园适应复盘咨询', NOW(), NOW()),
(22, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00:00', '10:00:00', 1, '人际边界建立咨询', NOW(), NOW()),
(23, 5, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '15:00:00', '16:00:00', 1, '同伴关系议题咨询', NOW(), NOW()),
(24, 5, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '20:00:00', '21:00:00', 1, '晚间情绪支持时段', NOW(), NOW()),
(25, 5, DATE_SUB(CURDATE(), INTERVAL 6 DAY), '09:00:00', '10:00:00', 1, '入学适应跟进时段', NOW(), NOW());

-- =========================================================
-- 五、预约数据
-- status：1-待确认 2-已确认 3-已完成 4-已取消
-- 为了保证逻辑自然：
-- 1. 过去时间段主要对应已完成
-- 2. 未来时间段主要对应待确认 / 已确认 / 已取消
-- 3. 同一 schedule_id 只对应一条预约，避免冲突
-- =========================================================
INSERT INTO appointment (id, appointment_no, user_id, counselor_id, schedule_id, status, contact_phone, remark, cancel_reason, create_time, update_time) VALUES
(1,  'MC202605030001',  7, 2,  6, 3, '13810000007', '最近和伴侣沟通时容易情绪激动，想梳理相处方式。', NULL, NOW(), NOW()),
(2,  'MC202605030002',  8, 3, 11, 3, '13810000008', '对未来方向感到迷茫，希望有人陪我梳理思路。', NULL, NOW(), NOW()),
(3,  'MC202605030003',  9, 4, 16, 3, '13810000009', '连续几周睡眠质量不好，白天注意力也下降。', NULL, NOW(), NOW()),
(4,  'MC202605030004', 10, 5, 21, 3, '13810000010', '最近在人际关系里总担心说错话，想缓解紧张。', NULL, NOW(), NOW()),
(5,  'MC202605030005', 11, 1,  5, 3, '13810000011', '工作节奏太快，常常下班后还处在高压状态。', NULL, NOW(), NOW()),
(6,  'MC202605030006', 12, 2, 10, 3, '13810000012', '家庭沟通压力比较大，希望先理清自己的表达方式。', NULL, NOW(), NOW()),
(7,  'MC202605030007', 13, 3, 15, 3, '13810000013', '阶段性自我怀疑较多，容易否定自己的选择。', NULL, NOW(), NOW()),
(8,  'MC202605030008', 14, 4, 20, 3, '13810000014', '最近焦虑反复出现，身体也有明显紧绷感。', NULL, NOW(), NOW()),
(9,  'MC202605030009', 15, 5, 25, 3, '13810000015', '刚换环境后很不适应，担心自己难以融入。', NULL, NOW(), NOW()),

(10, 'MC202605030010', 16, 1,  1, 1, '13810000016', '希望咨询职场压力与自我节奏平衡的问题。', NULL, NOW(), NOW()),
(11, 'MC202605030011',  7, 1,  2, 2, '13810000007', '情绪波动比较明显，想学习更稳定的调节方法。', NULL, NOW(), NOW()),
(12, 'MC202605030012',  8, 1,  3, 1, '13810000008', '最近长期疲惫，怀疑自己有轻度工作倦怠。', NULL, NOW(), NOW()),
(13, 'MC202605030013',  9, 2,  7, 2, '13810000009', '想提前准备一次关于家庭沟通的咨询。', NULL, NOW(), NOW()),
(14, 'MC202605030014', 10, 2,  8, 1, '13810000010', '与家人讨论问题时很容易僵住，想改善表达。', NULL, NOW(), NOW()),
(15, 'MC202605030015', 11, 3, 12, 2, '13810000011', '想更清楚地认识自己的长处和发展方向。', NULL, NOW(), NOW()),
(16, 'MC202605030016', 12, 3, 13, 1, '13810000012', '希望有人帮我梳理最近的学习压力和目标感。', NULL, NOW(), NOW()),
(17, 'MC202605030017', 13, 4, 17, 2, '13810000013', '想系统了解缓解焦虑和改善睡眠的方式。', NULL, NOW(), NOW()),
(18, 'MC202605030018', 14, 4, 18, 1, '13810000014', '最近夜间易醒，希望获得稳定睡眠节律的建议。', NULL, NOW(), NOW()),
(19, 'MC202605030019', 15, 5, 22, 2, '13810000015', '进入新班级后很难建立边界，容易被情绪影响。', NULL, NOW(), NOW()),
(20, 'MC202605030020', 16, 5, 23, 4, '13810000016', '原本想咨询人际关系问题，但临时改期。', '临时出差，无法按时参加咨询。', NOW(), NOW()),
(21, 'MC202605030021',  8, 1,  4, 4, '13810000008', '晚间时间原本更方便，但家里临时有事。', '家中临时有事，需要取消预约。', NOW(), NOW()),
(22, 'MC202605030022',  9, 2,  9, 4, '13810000009', '本来想约关系议题咨询，后续准备重新选择时间。', '后续需要重新安排时间。', NOW(), NOW()),
(23, 'MC202605030023', 10, 3, 14, 4, '13810000010', '希望周末咨询，但周内安排已经调整。', '学习安排变化，暂时取消。', NOW(), NOW()),
(24, 'MC202605030024', 11, 4, 19, 4, '13810000011', '原计划进行睡眠习惯调整咨询。', '近期状态稳定，先暂缓咨询。', NOW(), NOW()),
(25, 'MC202605030025', 12, 5, 24, 1, '13810000012', '近期人际压力明显，希望先做一次晚间咨询。', NULL, NOW(), NOW());

-- =========================================================
-- 六、咨询记录
-- 仅为已完成预约生成记录，体现业务闭环
-- =========================================================
INSERT INTO consultation_record (id, appointment_id, counselor_id, summary, suggestion, attachment_file_id, create_time, update_time) VALUES
(1, 1, 2, '来访者能够较清楚地描述伴侣沟通中的冲突触发点，主要问题集中在表达方式和情绪升级速度。', '建议在下一次冲突前先记录触发情境，并练习使用更具体的需求表达。', 1, NOW(), NOW()),
(2, 2, 3, '本次主要围绕求职阶段的自我评价波动展开，来访者对“做选择”存在明显焦虑。', '建议把近期最在意的三个发展方向列出来，分别写出担忧和期待。', NULL, NOW(), NOW()),
(3, 3, 4, '来访者的睡眠问题与工作后持续紧绷有关，已经形成“躺下后大脑停不下来”的循环。', '建议连续一周记录入睡时间、夜醒次数和睡前活动，观察影响因素。', 4, NOW(), NOW()),
(4, 4, 5, '来访者在人际互动中过度担心评价，容易提前设想负面反馈，导致回避表达。', '建议从低压力场景开始练习表达不同意见，逐步建立安全感。', NULL, NOW(), NOW()),
(5, 5, 1, '来访者长期把工作任务延伸到下班后，情绪松弛时间明显不足。', '建议先做一周“下班后不处理工作消息”的边界实验，并记录感受变化。', NULL, NOW(), NOW()),
(6, 6, 2, '本次咨询聚焦原生家庭沟通中的委屈感与防御模式，来访者开始意识到自己常用沉默回避冲突。', '建议整理三次最典型的家庭沟通事件，识别其中的重复模式。', 3, NOW(), NOW()),
(7, 7, 3, '来访者对近期学习与生活选择反复自我否定，内在标准较高。', '建议区分“我真正想要的”与“我担心被评价的”，减少外部评价对决策的影响。', NULL, NOW(), NOW()),
(8, 8, 4, '来访者焦虑时会出现身体紧绷和睡前思绪加速，本次已开始识别身体信号。', '建议练习固定时长的呼吸放松，并在睡前保持节律一致。', 2, NOW(), NOW()),
(9, 9, 5, '来访者在新环境中存在明显适应压力，对同伴关系高度敏感。', '建议先从一对一的低压力社交开始，逐步建立熟悉感和可控边界。', NULL, NOW(), NOW());

-- =========================================================
-- 七、反馈评价
-- 仅为部分已完成预约生成反馈，便于演示“已评价 / 待评价”并存
-- =========================================================
INSERT INTO feedback (id, appointment_id, user_id, counselor_id, score, content, is_anonymous, create_time, update_time) VALUES
(1, 1,  7, 2, 5, '老师很耐心，帮我把情绪和沟通问题分开来看，咨询后思路清晰很多。', 0, NOW(), NOW()),
(2, 2,  8, 3, 4, '整体交流比较顺畅，适合我这种需要慢慢梳理想法的人。', 1, NOW(), NOW()),
(3, 3,  9, 4, 5, '对睡眠问题的分析很具体，也给了可以马上尝试的建议。', 0, NOW(), NOW()),
(4, 4, 10, 5, 4, '咨询氛围比较轻松，能让我慢慢说出自己的担心。', 1, NOW(), NOW()),
(5, 5, 11, 1, 5, '关于工作和生活边界的建议很有帮助，我已经准备开始尝试。', 0, NOW(), NOW()),
(6, 6, 12, 2, 4, '老师会引导我去看问题背后的模式，不只是停留在情绪本身。', 0, NOW(), NOW()),
(7, 8, 14, 4, 5, '对焦虑和睡眠的关系解释得很清楚，能感觉到对方很专业。', 1, NOW(), NOW());
