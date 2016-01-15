package com.cannontech.common.i18n;

import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;

public class YukonReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    private static final Logger log = YukonLogManager.getLogger(YukonReloadableResourceBundleMessageSource.class);

    private ConfigurationSource configurationSource;

    @PostConstruct
    public void init() throws Exception {
        boolean devMode = false;
        try {
            log.error("configurationSoure=" + configurationSource);
            devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
            log.error("success");
        } catch (UndeclaredThrowableException e) {
            log.error("<UndeclaredThrowableException>", e.getUndeclaredThrowable());
            throw e;
        } catch (Exception e) {
            log.error("<UndeclaredThrowableException>", e);
            throw e;
        }
        log.error("devMode=" + devMode);
        if (devMode) {
            setCacheSeconds(10); // using a value of 10 instead of 1 dramatically increases performance, but you'll have
                                 // to wait up to 10 sec to see xml/property changes.
        } else {
            setCacheSeconds(-1);
        }
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
