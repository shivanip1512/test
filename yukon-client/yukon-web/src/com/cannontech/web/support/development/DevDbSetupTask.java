package com.cannontech.web.support.development;

import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.web.support.development.database.objects.DevAMR;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.objects.DevStars;

public class DevDbSetupTask {
    private boolean updateRoleProperties = true;
    private DevAMR devAMR = new DevAMR();
    private DevCapControl devCapControl = new DevCapControl();
    private DevStars devStars = new DevStars();
    private boolean hasRun = false;
    
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
    public void resetCompletedCounts() {
        devAMR.setSuccessCount(new AtomicInteger());
        devCapControl.setSuccessCount(new AtomicInteger());
        devStars.setSuccessCount(new AtomicInteger());
    }
}
