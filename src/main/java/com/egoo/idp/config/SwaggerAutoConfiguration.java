package com.egoo.idp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true")
public class SwaggerAutoConfiguration {
    // 该类仅作为条件加载的标记，实际配置在SwaggerConfig中
}
