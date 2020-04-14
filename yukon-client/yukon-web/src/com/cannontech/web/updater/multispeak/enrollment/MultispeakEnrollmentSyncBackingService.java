package com.cannontech.web.updater.multispeak.enrollment;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.multispeak.service.MultispeakEnrollmentSyncProgress;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.multispeak.MspHandler;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.multispeak.MultispeakSyncBackingServiceBase;
import com.cannontech.web.updater.multispeak.enrollmentSync.handler.MultispeakEnrollmentSyncUpdateHandler;

public class MultispeakEnrollmentSyncBackingService extends MultispeakSyncBackingServiceBase implements UpdateBackingService {

    private Map<MultispeakEnrollmentSyncUpdaterTypeEnum, MultispeakEnrollmentSyncUpdateHandler> handlersMap;

    @Autowired private MspHandler mspHandler;
    @Autowired private ObjectFormattingService objectFormattingService;

    @PostConstruct
    public void secondInit() {
        handlersMap = new HashMap<>();

        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.IS_RUNNING, (progress, userContext) -> {
            return progress != null ? progress.isRunning() : false;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.IS_NOT_RUNNING, (progress, userContext) -> {
            return progress != null ? !progress.isRunning() : true;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.IS_ABORTED, (progress, userContext) -> {
            return progress != null ? progress.isHasException() || progress.isCanceled() : false;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.STATUS_TEXT, (progress, userContext) -> {
            return progress != null ? progress.getStatus() : null;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.ENROLLMENT_MESSAGE_SENT_COUNT, (progress, userContext) -> {
            return progress != null ? progress.getEnrollmentSentCount() : 0;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.STATUS_CLASS, (progress, userContext) -> {
            return progress != null ? statusStyleClassNameMap.get(progress.getStatus()) : null;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.LAST_COMPLETED_SYNC_TIME, (progress, userContext) -> {
            return getLastCompletedSyncDateStr(MultispeakSyncTypeProcessorType.ENROLLMENT, userContext);
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.STATUS_TEXT_OR_LAST_SYNC_ENROLLMENT, (progress, userContext) -> {
            return getStatusTextObj(progress, MultispeakSyncTypeProcessorType.ENROLLMENT, userContext);
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.HAS_LINKABLE_PROGRESS,(progress, userContext) -> {
            if (progress != null) {
                return true;
            }
            return false;
        });
        
        handlersMap.put(MultispeakEnrollmentSyncUpdaterTypeEnum.NO_LINKABLE_PROGRESS,(progress, userContext) -> {
                if (progress == null) {
                    return true;
                }
                return false;
        });
    }

    @Override
    public String getLatestValue(String updaterTypeStr, long afterDate, YukonUserContext userContext) {
        MultispeakEnrollmentSyncProgress progress = null;
        if (mspHandler.isEnrollmentSyncSupported()) {
            progress = mspHandler.getMultispeakEnrollmentSyncService().getProgress();
        }
        MultispeakEnrollmentSyncUpdaterTypeEnum updaterType = MultispeakEnrollmentSyncUpdaterTypeEnum.valueOf(updaterTypeStr);
        MultispeakEnrollmentSyncUpdateHandler handler = handlersMap.get(updaterType);

        Object object = handler.handle(progress, userContext);
        return objectFormattingService.formatObjectAsString(object, userContext);
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }

}
