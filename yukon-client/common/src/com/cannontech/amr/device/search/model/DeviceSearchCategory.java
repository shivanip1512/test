package com.cannontech.amr.device.search.model;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public enum DeviceSearchCategory {
    MCT(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return PaoType.getMctTypes().contains(lPao.getPaoType());
        }
    }),
    IED(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return PaoType.getIedTypes().contains(lPao.getPaoType());
        }
    }),
    RTU(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return PaoType.getRtuTypes().contains(lPao.getPaoType());
        }
    }),
    TRANSMITTER(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return PaoClass.TRANSMITTER == lPao.getPaoType().getPaoClass();
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
