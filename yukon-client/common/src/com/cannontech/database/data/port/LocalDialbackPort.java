package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;

public class LocalDialbackPort extends LocalDialupPortBase {
    
    public LocalDialbackPort() {
        super(PaoType.LOCAL_DIALBACK);
    }
}