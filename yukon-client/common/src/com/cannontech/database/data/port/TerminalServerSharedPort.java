package com.cannontech.database.data.port;

import com.cannontech.common.pao.PaoType;

public class TerminalServerSharedPort extends TerminalServerSharedPortBase {
    
    public TerminalServerSharedPort() {
        super(PaoType.TSERVER_SHARED);
    }
}