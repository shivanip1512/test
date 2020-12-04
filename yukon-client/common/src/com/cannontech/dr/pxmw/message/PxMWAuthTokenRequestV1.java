package com.cannontech.dr.pxmw.message;

import java.io.Serializable;

public class PxMWAuthTokenRequestV1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean clearCache;

    public PxMWAuthTokenRequestV1(boolean clearCache) {
        //used by simulator to clear SM cache
        this.clearCache = clearCache;
    }

    public PxMWAuthTokenRequestV1() {
    }

    public boolean isClearCache() {
        return clearCache;
    }
}
