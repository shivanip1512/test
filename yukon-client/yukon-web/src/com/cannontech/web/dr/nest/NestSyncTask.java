package com.cannontech.web.dr.nest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.jobs.support.YukonTaskBase;

public class NestSyncTask extends YukonTaskBase {

    private final Logger log = YukonLogManager.getLogger(NestSyncTask.class);
    @Autowired private NestSyncService nestSyncService;

    @Override
    public void start() {
        log.info("Starting nest syncs.");
        nestSyncService.sync(true);
    }
}
