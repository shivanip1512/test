package com.cannontech.common.config.retrieve;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;

public class RfnConfigRetriever implements ConfigFileRetriever {
    
    private static final Logger log = YukonLogManager.getLogger(RfnConfigRetriever.class);
    
    public static final String defaultPath = "classpath:com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";
    public static final String customFilePath = "file:" + CtiUtilities.getYukonBase()  + "/Server/Config/rfnPointMapping.xml";
    
    @Autowired private ResourceLoader loader;
    
    @Override
    public Resource retrieve() {
        
        String host = System.getProperty("yukon.jws.host");
        String username = System.getProperty("yukon.jws.user");
        String password = System.getProperty("yukon.jws.pass");
        boolean isJavaWebstart = StringUtils.isNotBlank(host);
        
        if (isJavaWebstart) {
            log.info("Loading rfnPointMapping.xml for webstart client.");
            String location = host + "/common/config/rfn";
            try {
                location += "?" + "USERNAME=" + URLEncoder.encode(username, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8") + "&noLoginRedirect=true";
                return loader.getResource(location);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to encode username and password while retrieving rfnPointMapping.xml for java webstart client." , e);
            }
        } else {
            
            /* Check for a custom rfn point mapping file, use if there, otherwise use default */
            Resource customFile = loader.getResource(customFilePath);
            if (customFile.isReadable()) {
                return customFile;
            } else {
                return loader.getResource(defaultPath);
            }
        }
        
    }

    @Override
    public ConfigFile getType() {
        return ConfigFile.RFN_POINT_MAPPING;
    }
    
}