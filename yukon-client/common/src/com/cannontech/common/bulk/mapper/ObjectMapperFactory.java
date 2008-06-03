package com.cannontech.common.bulk.mapper;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.util.ObjectMapper;

/**
 * Factory interface which generates ObjectMappers
 */
public interface ObjectMapperFactory {
    
    static public enum FileMapperEnum {
        PAONAME("paoNameToYukonDeviceMapper"),
        METERNUMBER("meterNumberToYukonDeviceMapper"),
        ADDRESS("addressToYukonDeviceMapper"),
        DEVICEID("paoIdStrToYukonDeviceMapper"),
        BULK("bulkImporterToYukonDeviceMapper", true),
        ;
        
        private final String beanName;
        private final boolean hasHeader;
        private FileMapperEnum(String beanName) {
            this(beanName, false);
        }
        
        private FileMapperEnum(String beanName, boolean hasHeader) {
            this.beanName = beanName;
            this.hasHeader = hasHeader;
        }
        
        public String getBeanName() {
            return beanName;
        }
        
        public boolean isHasHeader() {
            return hasHeader;
        }
    }
    
    /**
     * Method to get one of several mappers designed for imported files (where
     * the "String" is one line of a file).
     * @param type
     * @return
     */
    public ObjectMapper<String, YukonDevice> getFileImportMapper(FileMapperEnum type);

}