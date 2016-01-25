package com.cannontech.common.i18n;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;

public class YukonReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    
    private ConfigurationSource configurationSource;

    @PostConstruct
    public void init() throws Exception {
        boolean devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        if(devMode) {
            setCacheSeconds(10); // using a value of 10 instead of 1 dramatically increases performance, but you'll have to wait up to 10 sec to see xml/property changes.
        } else {
            setCacheSeconds(-1);
        }
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}