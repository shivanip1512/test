package com.cannontech.common.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;

public class ReloadableResourceBundleThemeSource extends ResourceBundleThemeSource implements InitializingBean {
    
    private boolean useCodeAsDefaultMessage;
    private int cacheSeconds;
    private String defaultEncoding;
    private boolean fallbackToSystemLocale;
    private ConfigurationSource configurationSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        String yukonBase = CtiUtilities.getYukonBase();
        setBasenamePrefix("file:" + yukonBase + "/" + "Server/Config/Themes/");
        
        boolean devMode = configurationSource.getBoolean("DEVELOPMENT_MODE", false);
        if(devMode) {
            this.cacheSeconds = 1;
        } else {
            this.cacheSeconds = -1;
        }
    }
    
    @Override
    protected MessageSource createMessageSource(String basename) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setCacheSeconds(cacheSeconds);
        messageSource.setUseCodeAsDefaultMessage(useCodeAsDefaultMessage);
        messageSource.setDefaultEncoding(defaultEncoding);
        messageSource.setFallbackToSystemLocale(fallbackToSystemLocale);
        return messageSource;
    }
    
    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }
    
    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
    
    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }
    
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
