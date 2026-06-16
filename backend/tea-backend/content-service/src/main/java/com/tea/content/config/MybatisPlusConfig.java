package com.tea.content.config;

// 导入MyBatis-Plus相关类
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 用于注册MyBatis-Plus的核心插件，这里主要配置分页插件
 * 测试：GET
 * http://localhost:10010/content/article/list?current=1&size=5
 */
@Configuration // 标记为Spring配置类，项目启动时自动加载
public class MybatisPlusConfig {

    /**
     * 注册MyBatis-Plus拦截器（核心插件管理器）
     * @return 配置好的拦截器实例
     */
    @Bean // 将该方法返回的对象注册为Spring Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        // 1. 创建MyBatis-Plus核心拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 2. 添加分页插件（MyBatis-Plus 3.5+版本分页配置方式）
        // PaginationInnerInterceptor：分页内部拦截器，自动生成分页SQL
        // DbType.MYSQL：指定数据库类型为MySQL，适配MySQL的分页语法（LIMIT）
        interceptor.addInnerInterceptor(
                new PaginationInnerInterceptor(DbType.MYSQL)
        );

        // 3. 返回配置好的拦截器，交给Spring管理
        return interceptor;
    }
}