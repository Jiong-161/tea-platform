CREATE DATABASE IF NOT EXISTS tea_platform
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
-- 只能作为本地开发环境临时建库的草稿，存在索引缺失、约束不全、数据不安全等问题，严禁直接用于正式环境。
USE tea_platform;

CREATE TABLE tea_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'

) COMMENT='用户表';
  -- 用户表增加角色字段
  ALTER TABLE tea_user ADD COLUMN role INT NOT NULL DEFAULT 0 COMMENT '角色 0=普通用户 1=管理员';

  -- 初始化一个管理员账户（密码为BCrypt加密的 123456）
  -- 通过注册接口注册后再手动改 role=1 即可
  UPDATE tea_user SET role = 1 WHERE username = 'admin';
 CREATE TABLE tea_address (
      id            BIGINT AUTO_INCREMENT PRIMARY KEY,
      user_id       BIGINT       NOT NULL,
      receiver_name VARCHAR(50)  NOT NULL COMMENT '收货人',
      phone         VARCHAR(20)  NOT NULL COMMENT '手机号',
      province      VARCHAR(50)  NOT NULL COMMENT '省份',
      city          VARCHAR(50)  NOT NULL COMMENT '城市',
      district      VARCHAR(50)  NOT NULL COMMENT '区县',
      detail        VARCHAR(200) NOT NULL COMMENT '详细地址',
      is_default    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认 0-否 1-是',
      create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
      update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      INDEX idx_user_id (user_id)
  ) COMMENT '收货地址表';
  select * from tea_user;

INSERT INTO tea_user(username, password, nickname)
VALUES ('admin', '123456', '系统管理员');
INSERT INTO tea_user(username, password, nickname)
VALUES ('text1', '123456', '用户1');
UPDATE tea_user
SET password = ''
WHERE username = 'chen01';
#-----------------------------------------
CREATE TABLE tea_article_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
) COMMENT='茶文化文章分类表';

CREATE TABLE tea_article (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    category_id BIGINT COMMENT '分类ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary VARCHAR(500) COMMENT '文章摘要',
    cover VARCHAR(255) COMMENT '封面图',
    content TEXT COMMENT '文章内容',
    author VARCHAR(50) COMMENT '作者',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
) COMMENT='茶文化文章表';
#-----------------
CREATE TABLE tea_exhibition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '展览ID',
    title VARCHAR(200) NOT NULL COMMENT '展览标题',
    cover VARCHAR(255) COMMENT '封面图',
    location VARCHAR(255) COMMENT '展览地点',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    description TEXT COMMENT '展览介绍',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP
) COMMENT='茶文化展览表';

CREATE TABLE tea_exhibition_signup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exhibition_id BIGINT NOT NULL COMMENT '展览ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='展览报名表';

#初始化数据
INSERT INTO tea_exhibition
(title, cover, location, start_time, end_time, description)
VALUES
(
'中国茶文化艺术展',
'https://test.com/tea1.jpg',
'天津文化馆',
'2026-06-01 09:00:00',
'2026-06-07 18:00:00',
'展示中国传统茶文化与茶艺发展历史'
);
select * from tea_exhibition;

#--------------------product-service商品----------
#-------------------------------------------------
CREATE TABLE tea_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '商品名称',
    cover VARCHAR(255) COMMENT '商品图片',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存',
    status TINYINT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='茶商品表';

CREATE TABLE tea_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    total_amount DECIMAL(10,2) COMMENT '订单金额',
    status TINYINT DEFAULT 1 COMMENT '订单状态: 1已支付',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='订单表';
#订单与商品的一对多关系
CREATE TABLE tea_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    product_cover VARCHAR(255),
    price DECIMAL(10,2),
    quantity INT DEFAULT 1
) COMMENT='订单商品表';

-- 购物车表：存储用户加入购物车的商品信息
CREATE TABLE tea_cart (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车记录ID，主键自增',
    user_id     BIGINT   NOT NULL COMMENT '用户ID，关联用户表',
    product_id  BIGINT   NOT NULL COMMENT '商品ID，关联商品表',
    quantity    INT      NOT NULL DEFAULT 1 COMMENT '商品购买数量，默认1件',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间，自动刷新',
    UNIQUE KEY uk_user_product (user_id, product_id) COMMENT '唯一索引：防止同一个用户重复添加同一件商品'
) COMMENT '购物车表';
-- #c初始化
INSERT INTO tea_product
(title, cover, description, price, stock)
VALUES
(
    '西湖龙井',
    'https://test.com/tea1.jpg',
    '中国十大名茶之一',
    199.00,
    100
),
(
    '铁观音',
    'https://test.com/tea2.jpg',
    '乌龙茶代表',
    168.00,
    200
);
select * from tea_order;
#----------------------------------------------------
INSERT INTO tea_article_category(name, sort)
VALUES
('茶文化', 1),
('茶历史', 2),
('茶道', 3),
('茶器', 4);
SELECT  * FROM tea_user; 
 SELECT  * FROM tea_address;
SELECT  * FROM tea_article;
SELECT  * FROM tea_exhibition;
SELECT  * FROM tea_product;



SELECT username, password FROM tea_user WHERE username = 'admin';
