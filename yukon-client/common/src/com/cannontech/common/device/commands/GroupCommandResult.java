package com.cannontech.common.device.commands;

import com.cannontech.common.util.CancelStatus;

public class GroupCommandResult extends GroupActionResult implements CancelStatus {
    private boolean handleUnsupported = false;

    @Override
    public boolean isCanceled() {
        return resultHolder.isCanceled();
    }
    
    public boolean isHandleUnsupported() {
        return handleUnsupported;
    }
    
    public void setHandleUnsupported(boolean handleUnsupported) {
        this.handleUnsupported = handleUnsupported;
    }
}
