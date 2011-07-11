package com.cannontech.web.support.development;

import com.cannontech.web.support.development.database.objects.DevAMR;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.objects.DevStars;

public class DevDbSetupTask {
    private boolean updateRoleProperties = true;
    private DevAMR devAMR = new DevAMR();
    private DevCapControl devCapControl = new DevCapControl();
    private DevStars devStars = new DevStars();
    private boolean running = false;
    private boolean hasRun = false;
    private boolean cancelled = false;
    
    public boolean isUpdateRoleProperties() {
        return updateRoleProperties;
    }
    public void setUpdateRoleProperties(boolean updateRoleProperties) {
        this.updateRoleProperties = updateRoleProperties;
    }
    public DevAMR getDevAMR() {
        return devAMR;
    }
    public void setDevAMR(DevAMR devAMR) {
        this.devAMR = devAMR;
    }
    public DevCapControl getDevCapControl() {
        return devCapControl;
    }
    public void setDevCapControl(DevCapControl devCapControl) {
        this.devCapControl = devCapControl;
    }
    public DevStars getDevStars() {
        return devStars;
    }
    public void setDevStars(DevStars devStars) {
        this.devStars = devStars;
    }
    public boolean isHasRun() {
        return hasRun;
    }
    public void setHasRun(boolean hasRun) {
        this.hasRun = hasRun;
    }
    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
