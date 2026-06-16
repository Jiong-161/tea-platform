# 茶文平台数据库设计说明书（Markdown）
## 1. tea_user 用户表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|用户ID，主键自增|主键、自增|
|username|VARCHAR(50)|登录用户名|非空、唯一索引|
|password|VARCHAR(255)|BCrypt加密密码|非空|
|nickname|VARCHAR(50)|用户昵称|可为NULL|
|phone|VARCHAR(20)|手机号|可为NULL|
|email|VARCHAR(100)|邮箱|可为NULL|
|avatar|VARCHAR(500)|头像RustFS完整URL|可为NULL|
|role|TINYINT|角色：0普通用户，1管理员|非空，默认0|
|status|TINYINT|账号状态：0禁用，1启用|非空，默认1|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 2. tea_address 用户收货地址表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|地址主键ID|主键、自增|
|user_id|BIGINT|关联用户ID|非空、普通索引|
|receiver_name|VARCHAR(50)|收货人姓名|非空|
|phone|VARCHAR(20)|收货人电话|非空|
|province|VARCHAR(50)|省份|可为NULL|
|city|VARCHAR(50)|城市|可为NULL|
|district|VARCHAR(50)|区县|可为NULL|
|detail|VARCHAR(255)|详细收货地址|非空|
|is_default|TINYINT|0非默认，1默认地址|非空，默认0|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 3. tea_article_category 文章分类表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|分类主键ID|主键、自增|
|name|VARCHAR(50)|分类名称|非空|
|sort|INT|排序字段|非空，默认0|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 4. tea_article 茶文化文章表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|文章ID|主键、自增|
|category_id|BIGINT|关联分类ID|非空、索引|
|title|VARCHAR(200)|文章标题|非空|
|summary|VARCHAR(500)|文章摘要|可为NULL|
|cover|VARCHAR(500)|封面图片地址|可为NULL|
|content|LONGTEXT|文章富文本内容|可为NULL|
|author|VARCHAR(100)|作者名称|非空|
|view_count|INT|浏览量|默认0|
|status|TINYINT|0待审核、1已发布、2驳回|非空，默认1|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 5. tea_exhibition 茶事展览表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|展览ID|主键、自增|
|title|VARCHAR(200)|展览名称|非空|
|cover|VARCHAR(500)|展览封面图地址|可为NULL|
|location|VARCHAR(200)|举办地点|非空|
|start_time|DATETIME|开展时间|非空|
|end_time|DATETIME|结束时间|非空|
|description|TEXT|展览详情介绍|可为NULL|
|status|TINYINT|0未开始、1进行中、2已结束、3取消|非空，默认0|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 6. tea_exhibition_signup 展览报名表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|报名记录ID|主键、自增|
|exhibition_id|BIGINT|关联展览ID|非空、索引|
|user_id|BIGINT|报名用户ID|非空|
|username|VARCHAR(50)|报名用户名|非空、联合唯一索引|
|create_time|DATETIME|报名时间|默认当前时间|

## 7. tea_product 茶叶商品表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|商品ID|主键、自增|
|title|VARCHAR(200)|商品名称|非空|
|cover|VARCHAR(500)|商品封面图片地址|可为NULL|
|description|TEXT|商品详情|可为NULL|
|price|DECIMAL(10,2)|商品单价|默认0.00|
|stock|INT|库存数量|默认0|
|status|TINYINT|0下架，1上架|默认1|
|create_time|DATETIME|创建时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 8. tea_cart 购物车表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|购物车主键ID|主键、自增|
|user_id|BIGINT|用户ID|非空|
|product_id|BIGINT|商品ID|非空，用户+商品联合唯一|
|quantity|INT|选购数量|非空，默认1|
|create_time|DATETIME|加入购物车时间|默认当前时间|
|update_time|DATETIME|更新时间|自动更新|

## 9. tea_order 订单主表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|订单主键ID|主键、自增|
|order_no|VARCHAR(32)|订单编号|非空、唯一索引|
|user_id|BIGINT|下单用户ID|非空|
|username|VARCHAR(50)|下单用户名|非空|
|total_amount|DECIMAL(12,2)|订单总金额|默认0.00|
|status|TINYINT|0待支付1已支付2已发货3完成4取消|非空，默认1|
|create_time|DATETIME|下单时间|默认当前时间|

## 10. tea_order_item 订单明细表
|字段名|数据类型|字段说明|约束说明|
|----|----|----|----|
|id|BIGINT|明细主键ID|主键、自增|
|order_id|BIGINT|关联订单ID|非空、索引|
|product_id|BIGINT|关联商品ID|非空|
|product_name|VARCHAR(200)|下单商品名称|非空|
|product_cover|VARCHAR(500)|商品封面|可为NULL|
|price|DECIMAL(10,2)|下单单价|非空|
|quantity|INT|购买数量|默认1|

