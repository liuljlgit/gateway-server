package com.cloud.personal.gatewayserver.controller;

import com.alibaba.fastjson.JSONArray;
import com.cloud.personal.gatewayserver.constant.ServiceConstant;
import com.cloud.personal.gatewayserver.entity.GateWayRoute;
import com.cloud.personal.gatewayserver.route.DbDynamicRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RefreshGateWayController {

    @Autowired
    DbDynamicRouteService dbDynamicRouteService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/refresh/routes")
    public Mono refreshRoutes() {
        String gatewayRoutesStr = restTemplate.getForObject("http://"+ServiceConstant.DYNAMIC_GATEWAY_DB+"/gatewayroutes",String.class);
        List<GateWayRoute> gateWayRoutes = JSONArray.parseArray(gatewayRoutesStr,GateWayRoute.class);
        dbDynamicRouteService.updateGatewayRoutes(gateWayRoutes);
        return Mono.empty();
    }

    @RequestMapping("/test/filter")
    public Mono testFilter() {
        return Mono.empty();
    }
}
