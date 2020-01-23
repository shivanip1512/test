package com.cannontech.multispeak.service.impl.v5;

import javax.annotation.Resource;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.multispeak.dao.v5.MultispeakSendEnrollmentCallback;
import com.cannontech.multispeak.service.MultispeakEnrollmentSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncType;

public class MultispeakEnrollmentSyncService {

    private final static Logger log = YukonLogManager.getLogger(MultispeakEnrollmentSyncService.class);
    private MultispeakEnrollmentSyncProgress progress;

    @Resource(name = "globalScheduledExecutor") private ScheduledExecutor scheduledExecutor;
    @Autowired private MultispeakEnrollmentSyncHelper multispeakEnrollmentSyncHelper;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;

    public MultispeakEnrollmentSyncProgress getProgress() {
        return progress;
    }

    public Instant getLastSyncInstants() {
        return persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.MSP_SUBSTATION_DEVICE_GROUP_SYNC_LAST_COMPLETED);
    }

    public void startEnrollmentSync() {
        log.debug("Multispeak enrollment synchronization process started.");

        MultispeakSendEnrollmentCallback callback = new MultispeakSendEnrollmentCallback() {

            @Override
            public boolean isCanceled() {
                return progress.isCanceled();
            }

            @Override
            public void finish() {
                persistedSystemValueDao.setValue(PersistedSystemValueKey.MSP_ENROLLMENT_SYNC_LAST_COMPLETED, new Instant());
                progress.finish();
            }

            @Override
            public void enrollmentMessageSent() {
                progress.incrementEnrollmentSentCount();
            }

        };

        progress = new MultispeakEnrollmentSyncProgress(MultispeakSyncType.ENROLLMENT);

        Runnable runner = () -> {
            multispeakEnrollmentSyncHelper.startSync(callback);
        };

        scheduledExecutor.execute(runner);
    }
}
