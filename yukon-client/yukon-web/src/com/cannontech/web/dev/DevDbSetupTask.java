package com.cannontech.web.dev;

import com.cannontech.development.model.DevAmr;
import com.cannontech.web.dev.database.objects.DevCapControl;
import com.cannontech.web.dev.database.objects.DevRoleProperties;
import com.cannontech.web.dev.database.objects.DevStars;

public class DevDbSetupTask {

    private DevRoleProperties devRoleProperties = new DevRoleProperties();
    private DevAmr devAmr = new DevAmr();
    private DevCapControl devCapControl = new DevCapControl();
    private DevStars devStars = new DevStars();
    private boolean running = false;
    private boolean hasRun = false;
    private boolean cancelled = false;

    public DevRoleProperties getDevRoleProperties() {
        return devRoleProperties;
    }

    public void setDevRoleProperties(DevRoleProperties devRoleProperties) {
        this.devRoleProperties = devRoleProperties;
    }

    public DevAmr getDevAmr() {
        return devAmr;
    }

    public void setDevAmr(DevAmr devAmr) {
        this.devAmr = devAmr;
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
