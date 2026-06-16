-- ============================================================
-- 茶文·茶文化品鉴购销服务平台 —— 完整DDL建表SQL
-- 数据库：MySQL 8.0+
-- 数据库名：tea_platform
-- 字符集：utf8mb4
-- 时区：Asia/Shanghai
-- 编写日期：2026-06-02
-- 纯净的建表与初始化脚本，可直接执行;用于生产部署
-- ============================================================

-- -----------------------------------------------------------
-- 第一步：创建数据库
-- -----------------------------------------------------------
DROP DATABASE IF EXISTS `tea_platform`;
CREATE DATABASE `tea_platform`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE `tea_platform`;

-- ============================================================
-- 第二步：建表
-- ============================================================

-- -----------------------------------------------------------
-- 1. 用户表 (tea_user)
-- 存储平台所有注册用户信息，包括普通用户和管理员
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_user`;
CREATE TABLE `tea_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '用户ID，主键自增',
    `username`    VARCHAR(50)  NOT NULL                 COMMENT '用户名，唯一标识，用于登录',
    `password`    VARCHAR(255) NOT NULL                 COMMENT '密码，BCrypt加密存储',
    `nickname`    VARCHAR(50)  DEFAULT NULL             COMMENT '用户昵称/显示名',
    `phone`       VARCHAR(20)  DEFAULT NULL             COMMENT '手机号码',
    `email`       VARCHAR(100) DEFAULT NULL             COMMENT '电子邮箱',
    `avatar`      VARCHAR(500) DEFAULT NULL             COMMENT '头像URL（MinIO完整访问地址）',
    `role`        TINYINT      NOT NULL DEFAULT 0       COMMENT '角色：0=普通用户，1=管理员',
    `status`      TINYINT      NOT NULL DEFAULT 1       COMMENT '账号状态：0=禁用，1=启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台用户表';

-- -----------------------------------------------------------
-- 2. 收货地址表 (tea_address)
-- 存储用户的收货地址，支持多地址和默认地址设置
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_address`;
CREATE TABLE `tea_address` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '地址ID，主键自增',
    `user_id`       BIGINT       NOT NULL                 COMMENT '关联用户ID',
    `receiver_name` VARCHAR(50)  NOT NULL                 COMMENT '收货人姓名',
    `phone`         VARCHAR(20)  NOT NULL                 COMMENT '收货人联系电话',
    `province`      VARCHAR(50)  DEFAULT NULL             COMMENT '省份',
    `city`          VARCHAR(50)  DEFAULT NULL             COMMENT '城市',
    `district`      VARCHAR(50)  DEFAULT NULL             COMMENT '区/县',
    `detail`        VARCHAR(255) NOT NULL                 COMMENT '详细地址（街道、门牌号等）',
    `is_default`    TINYINT      NOT NULL DEFAULT 0       COMMENT '是否默认地址：0=否，1=是',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_default` (`user_id`, `is_default`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

-- -----------------------------------------------------------
-- 3. 文章分类表 (tea_article_category)
-- 存储茶文化文章的分类信息（如：茶道、茶艺、茶史等）
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_article_category`;
CREATE TABLE `tea_article_category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT  COMMENT '分类ID，主键自增',
    `name`        VARCHAR(50) NOT NULL                 COMMENT '分类名称（如：茶道、茶艺、茶史、茶器、茶礼）',
    `sort`        INT         NOT NULL DEFAULT 0       COMMENT '排序序号，数值越小越靠前',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- -----------------------------------------------------------
-- 4. 文章表 (tea_article)
-- 存储茶文化文章/资讯内容，支持管理员审核
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_article`;
CREATE TABLE `tea_article` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '文章ID，主键自增',
    `category_id` BIGINT        NOT NULL                 COMMENT '分类ID，关联tea_article_category.id',
    `title`       VARCHAR(200)  NOT NULL                 COMMENT '文章标题',
    `summary`     VARCHAR(500)  DEFAULT NULL             COMMENT '文章摘要/简介',
    `cover`       VARCHAR(500)  DEFAULT NULL             COMMENT '封面图URL（MinIO完整访问地址）',
    `content`     LONGTEXT      DEFAULT NULL             COMMENT '文章正文内容（支持HTML富文本）',
    `author`      VARCHAR(100)  NOT NULL                 COMMENT '文章作者',
    `view_count`  INT           NOT NULL DEFAULT 0       COMMENT '阅读量/浏览计数',
    `status`      TINYINT       NOT NULL DEFAULT 1       COMMENT '审核状态：0=待审核，1=审核通过（已发布），2=审核驳回',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建/发布时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_category_status` (`category_id`, `status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶文化文章表';

-- -----------------------------------------------------------
-- 5. 展览/茶博会表 (tea_exhibition)
-- 存储茶事展览、茶博会等活动的信息
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_exhibition`;
CREATE TABLE `tea_exhibition` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '展览ID，主键自增',
    `title`       VARCHAR(200)  NOT NULL                 COMMENT '展览标题/名称',
    `cover`       VARCHAR(500)  DEFAULT NULL             COMMENT '展览封面图URL（MinIO完整访问地址）',
    `location`    VARCHAR(200)  NOT NULL                 COMMENT '展览举办地点',
    `start_time`  DATETIME      NOT NULL                 COMMENT '展览开始时间',
    `end_time`    DATETIME      NOT NULL                 COMMENT '展览结束时间',
    `description` TEXT          DEFAULT NULL             COMMENT '展览详细介绍/描述',
    `status`      TINYINT       NOT NULL DEFAULT 0       COMMENT '展览状态：0=未开始，1=进行中，2=已结束，3=已取消',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶事展览/茶博会表';

-- -----------------------------------------------------------
-- 6. 展览报名记录表 (tea_exhibition_signup)
-- 存储用户报名展览的记录
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_exhibition_signup`;
CREATE TABLE `tea_exhibition_signup` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '报名记录ID，主键自增',
    `exhibition_id` BIGINT       NOT NULL                 COMMENT '关联展览ID',
    `user_id`       BIGINT       NOT NULL                 COMMENT '报名用户ID',
    `username`      VARCHAR(50)  NOT NULL                 COMMENT '报名用户名（冗余存储，避免联表查询）',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_exhibition` (`user_id`, `exhibition_id`),
    KEY `idx_exhibition_id` (`exhibition_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='展览报名记录表';

-- -----------------------------------------------------------
-- 7. 商品表 (tea_product)
-- 存储平台销售的茶叶/茶器商品信息
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_product`;
CREATE TABLE `tea_product` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '商品ID，主键自增',
    `title`       VARCHAR(200)   NOT NULL                 COMMENT '商品名称/标题',
    `cover`       VARCHAR(500)   DEFAULT NULL             COMMENT '商品封面图URL（MinIO完整访问地址）',
    `description` TEXT           DEFAULT NULL             COMMENT '商品详细描述',
    `price`       DECIMAL(10,2)  NOT NULL DEFAULT 0.00    COMMENT '商品价格（单位：元）',
    `stock`       INT            NOT NULL DEFAULT 0       COMMENT '库存数量',
    `status`      TINYINT        DEFAULT 1                COMMENT '商品状态：0=下架，1=上架',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_title` (`title`(50))
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='茶叶/茶器商品表';

-- -----------------------------------------------------------
-- 8. 购物车表 (tea_cart)
-- 存储用户购物车中的商品，同一用户同一商品只存一条记录
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_cart`;
CREATE TABLE `tea_cart` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT  COMMENT '购物车记录ID，主键自增',
    `user_id`     BIGINT   NOT NULL                 COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL                 COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1       COMMENT '商品数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户购物车表';

-- -----------------------------------------------------------
-- 9. 订单表 (tea_order)
-- 存储用户订单的主记录
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_order`;
CREATE TABLE `tea_order` (
    `id`           BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '订单ID，主键自增',
    `order_no`     VARCHAR(32)    NOT NULL                 COMMENT '订单号（16位唯一标识）',
    `user_id`      BIGINT         NOT NULL                 COMMENT '下单用户ID',
    `username`     VARCHAR(50)    NOT NULL                 COMMENT '下单用户名（冗余存储）',
    `total_amount` DECIMAL(12,2)  NOT NULL DEFAULT 0.00    COMMENT '订单总金额（单位：元）',
    `status`       TINYINT        NOT NULL DEFAULT 1       COMMENT '订单状态：0=待支付，1=已支付，2=已发货，3=已完成，4=已取消',
    `create_time`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户订单主表';

-- -----------------------------------------------------------
-- 10. 订单商品明细表 (tea_order_item)
-- 存储订单中每个商品的明细记录
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `tea_order_item`;
CREATE TABLE `tea_order_item` (
    `id`            BIGINT         NOT NULL AUTO_INCREMENT  COMMENT '明细ID，主键自增',
    `order_id`      BIGINT         NOT NULL                 COMMENT '关联订单ID',
    `product_id`    BIGINT         NOT NULL                 COMMENT '关联商品ID',
    `product_name`  VARCHAR(200)   NOT NULL                 COMMENT '下单时的商品名称（冗余存储）',
    `product_cover` VARCHAR(500)   DEFAULT NULL             COMMENT '下单时的商品封面图URL（冗余存储）',
    `price`         DECIMAL(10,2)  NOT NULL                 COMMENT '下单时的商品单价（单位：元）',
    `quantity`      INT            NOT NULL DEFAULT 1       COMMENT '购买数量',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品明细表';

-- ============================================================
-- 第三步：初始化数据
-- ============================================================

-- -----------------------------------------------------------
-- 初始化管理员账户
-- 用户名：admin  密码：admin123（BCrypt加密）
-- -----------------------------------------------------------
-- 注意：管理员密码 "admin123" 的BCrypt哈希值需在应用启动后通过以下方式生成：
-- 方式一（推荐）：项目启动后，调用 UserController 中注释掉的 /user/password 接口生成哈希值，
--              然后替换下面的 password 字段值。
-- 方式二：运行以下Java代码生成（需要引入 spring-security-crypto 依赖）：
--         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
--         String hash = encoder.encode("admin123");
--         System.out.println(hash);
-- 方式三：直接通过注册接口注册管理员账号，然后手动修改数据库 role=1。
--
-- 以下为 BCrypt(10轮) 加密的 "admin123" 的哈希值（供开发环境直接使用）：
INSERT INTO `tea_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES (
    'admin',
    '$2b$10$5lJi6ca/nl6iZ7m7gwOhzOTrpIlOQeANJuxz3Atb4zf9eAULVc/b6',
    '系统管理员',
    1,
    1
);

-- -----------------------------------------------------------
-- 初始化文章分类
-- -----------------------------------------------------------
INSERT INTO `tea_article_category` (`name`, `sort`) VALUES
('茶道文化', 1),
('茶艺茶礼', 2),
('茶叶品鉴', 3),
('茶器鉴赏', 4),
('茶史典故', 5),
('茶与健康', 6);

-- -----------------------------------------------------------
-- 初始化示例文章
-- -----------------------------------------------------------
INSERT INTO `tea_article` (`category_id`, `title`, `summary`, `author`, `view_count`, `status`) VALUES
(1, '茶道精神：和敬清寂的东方智慧', '探讨日本茶道"和敬清寂"精神与中华茶文化的内在联系，解读茶道修行中蕴含的人生哲理。', '茶人老李', 256, 1),
(2, '工夫茶二十一式：潮汕茶艺全解析', '详解潮汕工夫茶从备器到品饮的完整二十一式流程，每一步都是对茶文化的敬重与传承。', '潮汕茶客', 189, 1),
(3, '西湖龙井的品鉴艺术：色香味形四绝', '龙井茶以"色绿、香郁、味甘、形美"四绝著称，本文从专业角度解析品鉴西湖龙井的要领。', '龙井茶人', 412, 1),
(4, '紫砂壶鉴赏入门：泥料、器型与工艺', '从宜兴紫砂泥料分类到经典器型赏析，为茶器爱好者提供系统的紫砂壶鉴赏知识。', '紫砂玩家', 178, 1),
(5, '陆羽《茶经》：千年茶文化的源头', '追溯唐代茶圣陆羽与世界上第一部茶学专著《茶经》的历史地位与文化影响。', '文史茶人', 301, 1),
(6, '每天三杯茶：科学饮茶与养生之道', '结合现代营养学研究，分析绿茶、红茶、普洱茶的不同功效，倡导科学饮茶健康生活。', '健康茶道', 520, 1);

-- -----------------------------------------------------------
-- 初始化示例展览
-- -----------------------------------------------------------
INSERT INTO `tea_exhibition` (`title`, `location`, `start_time`, `end_time`, `description`, `status`) VALUES
('2026春季中国名茶博览会', '杭州国际博览中心', '2026-03-15 09:00:00', '2026-03-18 18:00:00', '汇聚全国六大茶类名优茶品，现场品鉴、茶艺表演、茶文化论坛等精彩活动。', 1),
('宜兴紫砂大师作品展', '宜兴陶瓷博物馆', '2026-04-10 09:00:00', '2026-05-10 17:00:00', '展出当代紫砂艺术大师精品力作百余件，涵盖传统器型与创新设计，紫砂爱好者不容错过。', 1),
('中华茶礼文化沉浸式体验展', '北京798艺术区', '2026-05-20 10:00:00', '2026-06-20 20:00:00', '以沉浸式数字艺术与实景还原相结合的方式，呈现唐宋元明清五朝茶礼文化的演变。', 1),
('武夷山岩茶文化节', '武夷山茶博园', '2026-07-01 09:00:00', '2026-07-05 18:00:00', '走进武夷岩茶核心产区，体验岩茶制作工艺，品尝大红袍、水仙、肉桂等经典岩茶。', 0);

-- -----------------------------------------------------------
-- 初始化示例商品
-- -----------------------------------------------------------
INSERT INTO `tea_product` (`title`, `description`, `price`, `stock`, `status`) VALUES
('明前西湖龙井 特级 50g', '2026年明前采摘，产自杭州西湖核心产区，色泽翠绿，香气清高持久，滋味鲜醇回甘。', 328.00, 100, 1),
('武夷山大红袍 特级 100g', '产自武夷山岩茶核心产区，传统炭焙工艺，岩韵显著，花香馥郁，七泡有余香。', 268.00, 80, 1),
('云南古树普洱生茶 357g饼', '选用300年以上古树春茶原料，传统石磨压制，汤色金黄透亮，回甘生津持久。', 498.00, 50, 1),
('福鼎白毫银针 特级 50g', '2026年头采白毫银针，芽头肥壮披毫，汤色杏黄清澈，毫香蜜韵，滋味清甜。', 258.00, 120, 1),
('宜兴原矿紫砂壶 西施壶 180cc', '选用黄龙山原矿朱泥，全手工制作，器型饱满圆润，出水流畅，适合冲泡乌龙茶。', 680.00, 20, 1),
('景德镇青花瓷茶具套装', '手工拉坯青花瓷，一壶四杯一公道，青花发色典雅，胎质细腻，送礼自用两相宜。', 358.00, 35, 1),
('金骏眉红茶 特级 50g', '产自武夷山桐木关，全芽头制作，金黄黑相间，蜜香薯香浓郁，滋味甜醇顺滑。', 388.00, 60, 1),
('安溪铁观音 清香型 250g', '传统半发酵工艺，绿叶红镶边，兰花香清雅持久，七泡余香，回甘明显。', 168.00, 90, 1),
('茶道六君子 竹制茶道工具套装', '精选五年以上老竹，手工打磨，含茶筒、茶匙、茶漏、茶则、茶夹、茶针，实用美观。', 98.00, 150, 1),
('云南滇红金芽 特级 100g', '产自云南凤庆，全芽头金毫显露，汤色红艳明亮，蜜糖香浓郁，滋味醇厚甜润。', 188.00, 75, 1);

-- ============================================================
-- 第四步：验证建表结果（可选执行）
-- ============================================================
-- SHOW TABLES;
-- SELECT COUNT(*) AS '用户数' FROM tea_user;
-- SELECT COUNT(*) AS '文章数' FROM tea_article;
-- SELECT COUNT(*) AS '展览数' FROM tea_exhibition;
-- SELECT COUNT(*) AS '商品数' FROM tea_product;