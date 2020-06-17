package com.cloud.personal.gatewayserver.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GateWayRoute {

    /**
     * 主键
     */
    private String id;

    /**
     * 路由主键
     */
    private String routeId;

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * predicates规则
     */
    private String predicates;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 正则规则
     */
    private String regexpUrl;

    /**
     * 可用状态
     */
    private Byte status;

}
