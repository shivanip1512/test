package com.cannontech.common.config.retrieve;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;

public class DefinitionsConfigRetriever implements ConfigFileRetriever {
    
private static final Logger log = YukonLogManager.getLogger(DefinitionsConfigRetriever.class);
    
    public static final String customFilePath = "file:" + CtiUtilities.getYukonBase()  + "/Server/Config/deviceDefinition.xml";
    
    @Autowired private ResourceLoader loader;
    
    @Override
    public Resource retrieve() {
        
        String host = System.getProperty("yukon.jws.host");
        String username = System.getProperty("yukon.jws.user");
        String password = System.getProperty("yukon.jws.pass");
        boolean isJavaWebstart = StringUtils.isNotBlank(host);
        
        if (isJavaWebstart) {
            log.info("Loading pao/device definitons xml for webstart client.");
            String location = host + "/spring/common/config/deviceDefinition";
            try {
                location += "?" + "USERNAME=" + URLEncoder.encode(username, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8") + "&noLoginRedirect=true";
                Resource resource = loader.getResource(location);
                
                /* Check length of content since the resource will never actually be null coming from a spring controller. */
                long contentLength = resource.contentLength();
                if (contentLength > 0) {
                    return resource;
                } else {
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unable to encode username and password while retrieving deviceDefinition.xml for java webstart client." , e);
            } catch (IOException e) {
                /* resource.contentLength() can throw IOException so, return null I guess. */
                return null;
            }
        } else {
            return loader.getResource(customFilePath);
        }
    }

    @Override
    public ConfigFile getType() {
        return ConfigFile.PAO_DEFINITIONS;
    }
    
}