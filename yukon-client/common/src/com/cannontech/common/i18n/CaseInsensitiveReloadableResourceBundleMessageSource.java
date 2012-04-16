package com.cannontech.common.i18n;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;

public class CaseInsensitiveReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    @Override
    protected Properties loadProperties(Resource resource, String filename) throws IOException {
        Properties mixedCaseProperties = super.loadProperties(resource, filename);
        Properties retVal = new Properties() {
            @Override
            public synchronized Object get(Object key) {
                return super.get(((String) key).toLowerCase());
            }

            @Override
            public String getProperty(String key) {
                return super.getProperty(key.toLowerCase());
            }

            @Override
            public String getProperty(String key, String defaultValue) {
                return super.getProperty(key.toLowerCase(), defaultValue);
            }
        };
        for (String key : mixedCaseProperties.stringPropertyNames()) {
            retVal.put(key.toLowerCase(), mixedCaseProperties.get(key));
        }
        return retVal;
    }
}
