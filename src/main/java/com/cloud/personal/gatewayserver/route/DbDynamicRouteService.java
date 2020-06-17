package com.cloud.personal.gatewayserver.route;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.cloud.personal.gatewayserver.entity.GateWayRoute;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class DbDynamicRouteService implements ApplicationEventPublisherAware {

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private static final List<String> ROUTE_LIST = new ArrayList<>();

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void updateGatewayRoutes(List<GateWayRoute> gateWayRoutes) {
        try {
            clearRoute();
            try {
                List<RouteDefinition> definitions = new ArrayList<>();
                if (!CollectionUtils.isEmpty(gateWayRoutes)) {
                    gateWayRoutes.forEach(r -> {
                        definitions.addAll(routeDefinitions(r.getRouteId(), r.getInstanceId(),
                                r.getRegexpUrl(), r.getPredicates().split(",")));
                    });
                }
                log.info("缓存中网关数据刷新：{}",definitions);
                for (RouteDefinition routeDefinition : definitions) {
                    addRoute(routeDefinition);
                }
                publish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.info(" db dynamic route config error!",e.getMessage());
        }
    }

    private List<RouteDefinition> routeDefinitions(String id,String uri,String regexp,String... pattern){
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        int index = -1;
        if(pattern!=null&&pattern.length>0){
            for(String p:pattern){
                RouteDefinition routeDefinition = new RouteDefinition();
                id = ++index==0?id:id+ "_" +index;
                routeDefinition.setId(id);
                routeDefinition.setUri(UriComponentsBuilder.fromUriString(uri).build().toUri());
                //断言规则
                PredicateDefinition predicateDefinition = new PredicateDefinition();
                Map<String,String> patternMap = Maps.newHashMap();
                patternMap.put("pattern",p);
                predicateDefinition.setName("Path");
                predicateDefinition.setArgs(patternMap);
                routeDefinition.setPredicates(Lists.newArrayList(predicateDefinition));

                //过滤规则
                Map<String,String> regexpMap = Maps.newHashMap();
                regexpMap.put("regexp",regexp+"/(?<remaining>.*)");
                regexpMap.put("replacement","/${remaining}");
                FilterDefinition filterDefinition = new FilterDefinition();
                filterDefinition.setName("RewritePath");
                filterDefinition.setArgs(regexpMap);
                routeDefinition.setFilters(Lists.newArrayList(filterDefinition));

                routeDefinitions.add(routeDefinition);
            }
        }
        return routeDefinitions;
    }

    private void clearRoute() {
        for(String id : ROUTE_LIST) {
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        }
        ROUTE_LIST.clear();
    }

    private void addRoute(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            ROUTE_LIST.add(definition.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish() {
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this.routeDefinitionWriter));
    }

}