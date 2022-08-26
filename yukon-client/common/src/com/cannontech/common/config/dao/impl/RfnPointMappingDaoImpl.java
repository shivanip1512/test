package com.cannontech.common.config.dao.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.dao.RfnPointMappingDao;
import com.cannontech.common.util.CtiUtilities;

public class RfnPointMappingDaoImpl implements RfnPointMappingDao {
    private static final Logger log = YukonLogManager.getLogger(RfnPointMappingDao.class);

    private final static String defaultPath = "classpath:com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml";
    private final static File customFile = new File(CtiUtilities.getYukonBase() + "/Server/Config/rfnPointMapping.xml");

    @Autowired private ResourceLoader loader;

    @Override
    public InputStream getPointMappingFile() {
        // Check for a custom RFN point mapping file, use if there, otherwise use default.
        if (customFile.exists() && customFile.isFile()) {
            log.info("Loading custom rfnPointMapping.xml");
            try {
                return new BufferedInputStream(new FileInputStream(customFile));
            } catch (FileNotFoundException e) {
                log.error("could not find custom rfnPointMapping.xml even though Java said it was available", e);
            }
        }
        log.info("Loading rfnPointMapping.xml");
        try {
            return loader.getResource(defaultPath).getInputStream();
        } catch (IOException ioe) {
            // This should never happen.
            log.error("could not open default rfnPointMapping.xml file", ioe);
            throw new RuntimeException("could not open default rfnPointMapping.xml file", ioe);
        }
    }
}
