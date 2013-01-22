package com.cannontech.common.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;

public class YukonReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource implements InitializingBean {
    
    private ConfigurationSource configurationSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean devMode = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE);
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
