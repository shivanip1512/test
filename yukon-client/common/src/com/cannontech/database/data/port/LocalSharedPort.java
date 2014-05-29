package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;

public class LocalSharedPort extends LocalSharedPortBase {
    
    public LocalSharedPort() {
        super(PaoType.LOCAL_SHARED);
    }
}