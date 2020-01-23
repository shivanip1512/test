package com.cannontech.web.updater.multispeak;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.service.MultispeakSyncProgressStatus;
import com.cannontech.multispeak.service.MultispeakSyncTypeProcessorType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.multispeak.MspHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public abstract class MultispeakSyncBackingServiceBase {

    protected ImmutableMap<MultispeakSyncProgressStatus, String> statusStyleClassNameMap;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MspHandler mspHandler;
    @Autowired MultispeakFuncs multispeakFuncs;

    @PostConstruct
    public void init() {
        
        Builder<MultispeakSyncProgressStatus, String> builder = ImmutableMap.builder();
        builder.put(MultispeakSyncProgressStatus.RUNNING, "");
        builder.put(MultispeakSyncProgressStatus.FAILED, "error");
        builder.put(MultispeakSyncProgressStatus.CANCELED, "error");
        builder.put(MultispeakSyncProgressStatus.FINISHED, "success");
        statusStyleClassNameMap = builder.build();
    }

    public String getLastCompletedSyncDateStr(MultispeakSyncTypeProcessorType type,
            YukonUserContext userContext) {
        int vendorId = multispeakFuncs.getPrimaryCIS();
        Instant instant = null;
        if (vendorId > 0) {
            if (type == MultispeakSyncTypeProcessorType.ENROLLMENT) {
                instant = mspHandler.getMultispeakEnrollmentSyncService().getLastSyncInstants();
            } else {
                Map<MultispeakSyncTypeProcessorType, Instant> lastSyncInstants = mspHandler.getDeviceGroupSyncService()
                        .getLastSyncInstants();
                instant = lastSyncInstants.get(type);
            }
        }

        if (vendorId <= 0 || instant == null) {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            return messageSourceAccessor.getMessage("yukon.common.na");
        }

        String dateStr = dateFormattingService.format(instant, DateFormatEnum.FULL, userContext);
        return dateStr;
    }
}
