package com.cannontech.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import com.cannontech.bootstrap.BootstrapServiceUtils;

@Configuration
public class YukonPropertySourcesPlaceholderConfigurer {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        String configFilePath = BootstrapServiceUtils.getBasePath() + "/Client/bin/cloudservice.properties";
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new FileSystemResource(configFilePath));
        return properties;
    }
}
