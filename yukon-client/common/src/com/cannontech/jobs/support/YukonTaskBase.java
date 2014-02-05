package com.cannontech.jobs.support;

import com.cannontech.jobs.model.YukonJob;
import com.cannontech.user.YukonUserContext;

public abstract class YukonTaskBase implements YukonTask {

    private YukonJob job;

    public YukonJob getJob() {
        return job;
    }

    protected YukonUserContext getUserContext() {
        return getJob().getUserContext();
    }

    @Override
    public void setJob(YukonJob job) {
        this.job = job;
    }

    @Override
    public void stop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot stop this task.");
    }
}
