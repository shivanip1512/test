package com.cannontech.common.device.commands;

import com.cannontech.common.util.CancelStatus;

public class GroupCommandResult extends GroupActionResult implements CancelStatus {
    private String command;
    private boolean handleUnsupported = false;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

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
