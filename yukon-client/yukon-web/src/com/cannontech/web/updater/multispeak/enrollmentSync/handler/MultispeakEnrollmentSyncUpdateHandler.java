package com.cannontech.web.updater.multispeak.enrollmentSync.handler;

import com.cannontech.multispeak.service.MultispeakEnrollmentSyncProgress;
import com.cannontech.user.YukonUserContext;

public interface MultispeakEnrollmentSyncUpdateHandler {

    public Object handle(MultispeakEnrollmentSyncProgress progress, YukonUserContext userContext);
}
