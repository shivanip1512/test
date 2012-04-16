package com.cannontech.common.i18n;

import org.springframework.beans.factory.InitializingBean;

import com.cannontech.common.config.ConfigurationSource;

public class YukonReloadableResourceBundleMessageSource extends CaseInsensitiveReloadableResourceBundleMessageSource implements InitializingBean {
    
    private ConfigurationSource configurationSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean devMode = configurationSource.getBoolean("DEVELOPMENT_MODE", false);
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
