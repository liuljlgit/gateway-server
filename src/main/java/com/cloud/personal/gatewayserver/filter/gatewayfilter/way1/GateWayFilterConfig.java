package com.cloud.personal.gatewayserver.filter.gatewayfilter.way1;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayFilterConfig {

    /**
     * implements GatewayFilter, Ordered 这种实现方式需通过配置类配置
     * @param builder
     * @return
     */
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        return builder.routes().route(r ->
//                r.path("/app/link-nacos-filter/**")
//                        .uri("lb://link-nacos-server")
//                        .filters(new AuthorizeGatewayFilter())
//                        .id("link-nacos-server-filter"))
//                .build();
//    }

}
