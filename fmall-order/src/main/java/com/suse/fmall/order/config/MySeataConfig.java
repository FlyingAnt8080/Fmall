package com.suse.fmall.order.config;


import org.springframework.context.annotation.Configuration;
/**
 * @Author LiuJing
 * @Date: 2021/04/17/ 21:42
 * @Description
 */
@Configuration
public class MySeataConfig {
   /* @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties){
        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(dataSource);
    }*/
}
