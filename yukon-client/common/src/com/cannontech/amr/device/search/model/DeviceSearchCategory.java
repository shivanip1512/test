package com.cannontech.amr.device.search.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;


public enum DeviceSearchCategory {
    MCT(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return (DeviceTypesFuncs.isMCT(lPao.getPaoType().getDeviceTypeId())
                    && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE);
        }
    }),
    IED(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return (DeviceTypesFuncs.isMeter(lPao.getPaoType().getDeviceTypeId())
                    || lPao.getPaoType() == PaoType.DAVISWEATHER
                    && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE);
        }
    }),
    RTU(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return ((DeviceTypesFuncs.isRTU(lPao.getPaoType().getDeviceTypeId())
                        || lPao.getPaoType() == PaoType.DAVISWEATHER)
                    && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE
                    && !DeviceTypesFuncs.isIon(lPao.getPaoType().getDeviceTypeId()));
        }
    }),
    TRANSMITTER(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return (DeviceTypesFuncs.isTransmitter(lPao.getPaoType().getDeviceTypeId())
                    && DeviceClasses.isMeterClass(lPao.getPaoType().getPaoClass().getPaoClassId())
                    && lPao.getPaoType().getPaoCategory() == PaoCategory.DEVICE);
        }
    }),
    LMGROUP(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            // TODO Auto-generated method stub
            return (PaoClass.LOADMANAGEMENT == lPao.getPaoType().getPaoClass()
                    || PaoClass.GROUP == lPao.getPaoType().getPaoClass());
        }
    }),
    CAP(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return (PaoClass.CAPCONTROL == lPao.getPaoType().getPaoClass());
        }
    });
    
    private CategoryContentChecker contentChecker;
    
    private DeviceSearchCategory(final CategoryContentChecker contentChecker) {
        this.contentChecker = contentChecker;
    }
    
    public boolean contains(final LiteYukonPAObject lPao) {
        return contentChecker.contains(lPao);
    }
    
    private interface CategoryContentChecker {
        boolean contains(final LiteYukonPAObject lPao);
    }
    
    public static DeviceSearchCategory fromLiteYukonPAObject(LiteYukonPAObject lPao) {
        for(DeviceSearchCategory category : values()) {
            if(category.contains(lPao)) {
                return category;
            }
        }
        
        return null;
    }
}
