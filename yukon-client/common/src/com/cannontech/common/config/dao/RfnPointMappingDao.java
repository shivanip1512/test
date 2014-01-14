package com.cannontech.common.config.dao;

import java.io.InputStream;

public interface RfnPointMappingDao {
    /**
     * Get the correct rfnPointMapping.xml file.  This will look first for a custom file but return the default if
     * not custom one exists.  This method also handles getting the file from the web server if being called from a
     * Web Start application.
     */
    InputStream getPointMappingFile();
}
