package com.cannontech.dr.eatonCloud.message;

import java.io.Serializable;

public class EatonCloudAuthTokenRequestV1 implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean clearCache;

    public EatonCloudAuthTokenRequestV1(boolean clearCache) {
        //used by simulator to clear SM cache
        this.clearCache = clearCache;
    }

    public EatonCloudAuthTokenRequestV1() {
    }

    public boolean isClearCache() {
        return clearCache;
    }
}
