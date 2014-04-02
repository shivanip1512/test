package com.cannontech.util.mbean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;

import com.cannontech.common.util.BootstrapUtils;

public class YukonNamingStrategy extends MetadataNamingStrategy {
    @Override
    public void setDefaultDomain(String defaultDomain) {
        String applicationName = BootstrapUtils.getApplicationName();
        if (applicationName != null) {
            defaultDomain += "." + StringUtils.substring(applicationName.replaceAll("[^\\w]", ""), 0, 20);
        }
        super.setDefaultDomain(defaultDomain);
    }
}
