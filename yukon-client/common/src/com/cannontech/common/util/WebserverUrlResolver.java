package com.cannontech.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class WebserverUrlResolver {
    private static final Logger log = YukonLogManager.getLogger(WebserverUrlResolver.class);
    @Autowired ConfigurationSource configurationSource;
    @Autowired GlobalSettingDao globalSettingDao;
    private static final String defaultUrl = "http://127.0.0.1:8080";
    
    /**
     * Retrieves the configured address for the Yukon Web Server. The YUKON_EXTERNAL_URL cparm is checked first. If that
     * is not present, the YUKON_EXTERNAL_URL global setting is checked. If that is also not present, the specified 
     * default value is returned.
     * 
     * This value is always returned without a trailing slash.
     * 
     * @param customDefaultUrl A default URL to use if no setting is configured.
     */
    public String getUrlBase(String customDefaultUrl) {
        String cparm = configurationSource.getString(MasterConfigString.YUKON_EXTERNAL_URL);
        if (cparm == null) {
            String globalSetting = globalSettingDao.getString(GlobalSettingType.YUKON_EXTERNAL_URL);
            if (StringUtils.isEmpty(globalSetting)) {
                String defaultToUse = StringUtils.isEmpty(customDefaultUrl) ? defaultUrl : customDefaultUrl;
                log.error("No master.cfg entry or configuration setting was found for YUKON_EXTERNAL_URL. Defaulting to " + defaultToUse);
                return stripTrailingSeparatorIfPresent(defaultToUse);
            }
            return stripTrailingSeparatorIfPresent(globalSetting);
        }
        return stripTrailingSeparatorIfPresent(cparm);
    }
    
    /**
     * Retrieves the configured address for the Yukon Web Server. The YUKON_EXTERNAL_URL cparm is checked first. If that
     * is not present, the YUKON_EXTERNAL_URL global setting is checked. If that is also not present, a default value is
     * returned.
     */
    public String getUrlBase() {
        return getUrlBase(null);
    }
    
    /**
     * Generates a complete URL by retrieving the configured base address for the Yukon Web Server and appending the
     * postfix to it. In resolving the base address, the YUKON_EXTERNAL_URL cparm is checked first. If that is not 
     * present, the YUKON_EXTERNAL_URL global setting is checked. If that is also not present, the specified default 
     * value is used.
     * @param customDefaultUrl A default URL to use if no setting is configured.
     */
    public String getUrl(String postfix, String customDefaultUrl) {
        return getUrlBase(customDefaultUrl) + prependSeparatorIfAbsent(postfix);
    }
    
    /**
     * Generates a complete URL by retrieving the configured base address for the Yukon Web Server and appending the
     * postfix to it. In resolving the base address, the YUKON_EXTERNAL_URL cparm is checked first. If that is not 
     * present, the YUKON_EXTERNAL_URL global setting is checked. If that is also not present, a default value is 
     * used.
     */
    public String getUrl(String postfix) {
        return getUrlBase() + prependSeparatorIfAbsent(postfix);
    }

    /**
     * Returns the Yukon Internal Url.
     */
    public String getYukonInternalUrl() {
        return globalSettingDao.getString(GlobalSettingType.YUKON_INTERNAL_URL);
    }

    private String prependSeparatorIfAbsent(String postfix) {
        if (postfix.startsWith("/")) {
            return postfix;
        }
        return "/" + postfix;
    }
    
    private String stripTrailingSeparatorIfPresent(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
