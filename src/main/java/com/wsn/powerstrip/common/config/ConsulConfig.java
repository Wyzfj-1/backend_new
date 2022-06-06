package com.wsn.powerstrip.common.config;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 6:35 PM 08/05/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Configuration
@ConditionalOnConsulEnabled
public class ConsulConfig {
    //参照源码定义声名
    final private TtlScheduler ttlScheduler;

    @Autowired(required = false)
    public ConsulConfig(TtlScheduler ttlScheduler) {
        this.ttlScheduler = ttlScheduler;
    }

    @Bean
    public ConsulServiceRegistry consulServiceRegistry(ConsulClient consulClient, ConsulDiscoveryProperties properties,
                                                       HeartbeatProperties heartbeatProperties) {
        return new ConsulServiceRegistry(consulClient, properties, ttlScheduler, heartbeatProperties) {
            @Override
            public void register(ConsulRegistration reg) {
                //重新设计id，此处用的是名字也可以用其他方式例如instanceid、host、uri等
                reg.getService().setId(reg.getService().getName() + "-" + reg.getService().getAddress() + "-" + reg.getPort());
                super.register(reg);
            }
        };
    }
}
