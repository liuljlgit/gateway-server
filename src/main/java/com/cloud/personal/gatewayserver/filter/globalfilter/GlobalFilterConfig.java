package com.cloud.personal.gatewayserver.filter.globalfilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalFilterConfig {

    /**
     * 配置了这个就可以开启全局路由
     */
    @Bean
    public AuthorizeGlobalFilter authorizeGlobalFilter(){
        return new AuthorizeGlobalFilter();
    }

}
