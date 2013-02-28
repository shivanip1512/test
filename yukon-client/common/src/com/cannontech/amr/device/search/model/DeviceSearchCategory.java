package com.cannontech.amr.device.search.model;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public enum DeviceSearchCategory {
    MCT(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isMct();
        }
    }),
    IED(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isIed();
        }
    }),
    RTU(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isRtu();
        }
    }),
    TRANSMITTER(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isTransmitter();
        }
    }),
    LMGROUP(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isLmGroup();
        }
    }),
    CAP(new CategoryContentChecker() {
        @Override
        public boolean contains(LiteYukonPAObject lPao) {
            return lPao.getPaoType().isCapControl();
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
