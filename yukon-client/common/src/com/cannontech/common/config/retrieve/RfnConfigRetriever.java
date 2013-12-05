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

public class RfnConfigRetriever implements ConfigFileRetriever {
    
    private static final Logger log = YukonLogManager.getLogger(RfnConfigRetriever.class);
    
    public static final String defaultPath = "classpath:com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";
    public static final String customFilePath = "file:" + CtiUtilities.getYukonBase()  + "/Server/Config/rfnPointMapping.xml";
    
    @Autowired private ResourceLoader loader;
    
    @Override
    public Resource retrieve() {
        
        if (ClientSession.isRemoteSession()) {
            log.info("Loading rfnPointMapping.xml for webstart client.");
            try {
                InputStream inputStream = ClientSession.getRemoteSession().getInputStreamFromUrl("/common/config/rfn");
                Resource resource = new InputStreamResource(inputStream);
                return resource;
            } catch (IOException e) {
                throw new RuntimeException("Unable to retrieve rfnPointMapping.xml for java webstart client." , e);
            }
        } else {
            /* Check for a custom rfn point mapping file, use if there, otherwise use default */
            Resource customFile = loader.getResource(customFilePath);
            if (customFile.isReadable()) {
                log.info("Loading custom rfnPointMapping.xml");
                return customFile;
            } else {
                log.info("Loading rfnPointMapping.xml");
                return loader.getResource(defaultPath);
            }
        }
    }

    @Override
    public ConfigFile getType() {
        return ConfigFile.RFN_POINT_MAPPING;
    }
    
}