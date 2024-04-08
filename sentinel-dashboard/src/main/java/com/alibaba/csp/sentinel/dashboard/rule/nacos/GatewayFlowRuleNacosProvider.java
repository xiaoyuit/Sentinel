package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("gatewayFlowRuleNacosProvider")
public class GatewayFlowRuleNacosProvider {

    @Autowired
    private ConfigService configService;

    /**
     * 获取网关流控规则
     * @param app application服务名
     * @param ip ip地址
     * @param port 端口
     * @return 转换号的List<GatewayFlowRuleEntity>
     * @throws Exception
     */
    public List<GatewayFlowRuleEntity> getRules(String app, String ip, Integer port) throws Exception {
        String jsonStr = configService.getConfig(
                app + NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID,
                3000);
        if (StringUtil.isEmpty(jsonStr)) {
            return new ArrayList<>();
        }

        // 将获取到的JSON字符串转换成GatewayFlowRule列表
        List<GatewayFlowRule> ruleList = JSON.parseArray(jsonStr, GatewayFlowRule.class);
        // 将GatewayFlowRule列表转换成GatewayFlowRuleEntity列表
        List<GatewayFlowRuleEntity> entityList = ruleList.stream()
                .map(rule -> GatewayFlowRuleEntity.fromGatewayFlowRule(app, ip, port, rule))
                .collect(Collectors.toList());
        return entityList;
    }

}
