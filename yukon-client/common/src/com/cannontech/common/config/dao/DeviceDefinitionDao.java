package com.cannontech.common.config.dao;

import java.io.InputStream;


public interface DeviceDefinitionDao {
    /**
     * Get the file size of the custom file.  Unlike {@link #findCustomDeviceDefinitions()}, this method should
     * not be called from a Java Web Start application.
     */
    long getCustomFileSize();

    /**
     * Get the XML file of custom device definitions from /Server/Config/deviceDefinition.xml.  If there is no file,
     * this will return null.  This method also handles getting the file from the web server if being called from a
     * Web Start application.
     */
    InputStream findCustomDeviceDefinitions();
}
