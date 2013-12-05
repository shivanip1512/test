package com.cannontech.common.config.retrieve;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;

public class DefinitionsConfigRetriever implements ConfigFileRetriever {
    
private static final Logger log = YukonLogManager.getLogger(DefinitionsConfigRetriever.class);
    
    public static final String customFilePath = "file:" + CtiUtilities.getYukonBase()  + "/Server/Config/deviceDefinition.xml";
    
    @Autowired private ResourceLoader  loader;
    
    @Override
    public Resource retrieve() {
              
        if (ClientSession.isRemoteSession()) {
            log.info("Loading deviceDefinition.xml for webstart client.");
            try {
                InputStream inputStream = ClientSession.getRemoteSession().getInputStreamFromUrl("/common/config/deviceDefinition");
                Resource resource = new InputStreamResource(inputStream);
                
                /* Check length of content since the resource will never actually be null coming from a spring controller. */
                long contentLength = resource.contentLength();
                if (contentLength > 0) {
                    return resource;
                } else {
                    log.error("Unable to retrieve deviceDefinition.xml for java webstart client.");
                    return null;
                }
         
            } catch (IOException e) {
                throw new RuntimeException("Unable to retrieve deviceDefinition.xml for java webstart client." , e);
            }
        } else {
            log.info("Loading deviceDefinition.xml.");
            return loader.getResource(customFilePath);
        }
    }

    @Override
    public ConfigFile getType() {
        return ConfigFile.PAO_DEFINITIONS;
    }
    
}