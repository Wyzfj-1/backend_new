package com.wsn.powerstrip.common.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

/**
 * 按照名称注入mongoDB
 * @Author: wangzilinn@gmail.com
 * @Date: 2/1/2021 3:54 PM
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Slf4j
@Data
public class MongoDBConfig {

    private String username;
    private String password;
    private String host;
    private String port;
    private String authenticationDatabase;

    @Bean
    @Primary   //这里要加这个primary，否则每个bean在用时都要用qualifier指定bean名字
    public MongoTemplate mongoTemplateForUser() {
        log.debug("inject mongoTemplateForUser");
        return getMongoTemplateByDatabaseName("member");
    }

    @Bean
    public MongoTemplate mongoTemplateForSMS() {
        log.debug("inject mongoTemplateForSMS");
        return getMongoTemplateByDatabaseName("sms");
    }

    @Bean
    public MongoTemplate mongoTemplateForRecord() {
        log.debug("inject mongoTemplateForRecord");
        return getMongoTemplateByDatabaseName("record");
    }

    @Bean
    public MongoTemplate mongoTemplateForAlarm() {
        log.debug("inject mongoTemplateForAlarm");
        return getMongoTemplateByDatabaseName("alarm");
    }

    @Bean
    public MongoTemplate mongoTemplateForDevice() {
        log.debug("inject mongoTemplateForDevice");
        return getMongoTemplateByDatabaseName("device");
    }

    @Bean
    public MongoTemplate mongoTemplateForDeviceHistory() {
        log.debug("inject mongoTemplateForDeviceHistory");
        return getMongoTemplateByDatabaseName("history");
    }

    /**
     * 这个与上面的不同,是专门返回给iotplatform的mqtt历史用的
     * @return _
     */
    @Bean
    public MongoTemplate mongoTemplateForMQTTHistory() {
        log.debug("inject mongoTemplateForMQTTHistory");
        return getMongoTemplateByDatabaseName("mqtt_history");
    }

    private MongoTemplate getMongoTemplateByDatabaseName(String databaseName) {
        MongoCredential mongoCredential = MongoCredential.createCredential(username, authenticationDatabase,
                password.toCharArray());
        ServerAddress serverAddress = new ServerAddress(host, Integer.parseInt(port));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyToClusterSettings(builder -> {
            builder.hosts(Collections.singletonList(serverAddress));
        }).credential(mongoCredential).build();
        return new MongoTemplate(MongoClients.create(mongoClientSettings),databaseName);
    }

}
