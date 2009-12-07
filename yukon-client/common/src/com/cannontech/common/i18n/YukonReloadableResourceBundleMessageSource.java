package com.cannontech.common.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.common.config.ConfigurationSource;

public class YukonReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource implements InitializingBean {
    
    private ConfigurationSource configurationSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean devMode = configurationSource.getBoolean("DEVELOPMENT_MODE", false);
        if(devMode) {
            setCacheSeconds(1);
        } else {
            setCacheSeconds(-1);
        }
    }
    
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
