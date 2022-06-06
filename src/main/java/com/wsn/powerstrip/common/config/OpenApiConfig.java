package com.wsn.powerstrip.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 4:26 PM 5/16/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("EasecureLab API")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                .description("REST的测试文件/requsetTest路径下,可以提供更详细的信息. 在网页Request body中的JSON,基本不需要传id字段, 大部分字段可以为null, " +
                        "详情请参照每个API下的Request body->Schema, 带星号的为必须含有的字段")
                .version("2.0")
        );
    }
}
