package com.cannontech.common.config.dao.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.config.dao.DeviceDefinitionDao;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;

public class DeviceDefinitionDaoImpl implements DeviceDefinitionDao {
    private static final Logger log = YukonLogManager.getLogger(DeviceDefinitionDaoImpl.class);

    private final static File customFile = new File(CtiUtilities.getYukonBase() + "/Server/Config/deviceDefinition.xml");

    @Override
    public long getCustomFileSize() {
        if (ClientSession.isRemoteSession()) {
            throw new IllegalStateException("getCustomFileSize cannot be called from a Web Start client");
        }
        return customFile.exists() ? customFile.length() : 0;
    }

    @Override
    public InputStream findCustomDeviceDefinitions() {
        if (ClientSession.isRemoteSession()) {
            log.info("Loading deviceDefinition.xml for webstart client.");
            try {
                RemoteLoginSession remoteSession = ClientSession.getRemoteLoginSession();
                InputStream inputStream = remoteSession.getInputStreamForUrl("/common/config/deviceDefinition", true);
                return inputStream;
            } catch (IOException e) {
                throw new RuntimeException("Unable to retrieve deviceDefinition.xml for java webstart client." , e);
            }
        } else {
            log.info("Loading deviceDefinition.xml.");
            InputStream retVal;
            try {
                retVal = new BufferedInputStream(new FileInputStream(customFile));
                return retVal;
            } catch (FileNotFoundException e) {
                log.info("No custom deviceDefinition.xml file.");
                return null;
            }
        }
    }
}
