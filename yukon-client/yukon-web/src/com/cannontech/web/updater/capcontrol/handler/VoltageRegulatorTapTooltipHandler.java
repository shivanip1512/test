package com.cannontech.web.updater.capcontrol.handler;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.VoltageRegulatorUpdaterTypeEnum;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;

public class VoltageRegulatorTapTooltipHandler implements VoltageRegulatorUpdaterHandler {

    private CapControlCache capControlCache;
    private DateFormattingService dateFormattingService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public VoltageRegulatorUpdaterTypeEnum getUpdaterType() {
        return VoltageRegulatorUpdaterTypeEnum.TAP_TOOLTIP;
    }

    @Override
    public String handle(int id, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        try {
            VoltageRegulatorFlags regulatorFlags = capControlCache.getVoltageRegulatorFlags(id);
            Date opTime = regulatorFlags.getLastOperationTime();
            TapOperation tapOp = regulatorFlags.getLastOperation();
            String text = null;
            
            if (tapOp == TapOperation.NONE) {
                text = messageSourceAccessor.getMessage("yukon.web.defaults.na");
            } else if (tapOp == TapOperation.LOWER_TAP) {
                if (regulatorFlags.isRecentOperation()) {
                    if (isWarning(regulatorFlags)) {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.flashingOrangeDownArrow.tooltip");
                    } else {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.flashingGreenDownArrow.tooltip");
                    }
                } else {
                    if (isWarning(regulatorFlags)) {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.orangeDownArrow.tooltip");
                    } else {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.greenDownArrow.tooltip");
                    }
                }
            } else if (tapOp == TapOperation.RAISE_TAP) {
                if (regulatorFlags.isRecentOperation()) {
                    if (isWarning(regulatorFlags)) {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.flashingOrangeUpArrow.tooltip");
                    } else {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.flashingGreenUpArrow.tooltip");
                    }
                } else {
                    if (isWarning(regulatorFlags)) {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.orangeUpArrow.tooltip");
                    } else {
                        text = messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.ivvc.greenUpArrow.tooltip");
                    }
                }
            }
            
            if (tapOp != TapOperation.NONE) {
                String opTimeString = dateFormattingService.format(opTime, DateFormatEnum.BOTH, userContext);
                text += ": " + opTimeString;
            }
            return text;
        } catch (NotFoundException nfe) {
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
            // By returning null the service won't try to update
            // that object.
            return StringUtils.EMPTY;
        }
    }
    
    private boolean isWarning(VoltageRegulatorFlags regulatorFlags) {
        if (regulatorFlags.isAutoRemote()) {
            if (regulatorFlags.isAutoRemoteManual() == false) {
                return true;
            }
        }
        return false;
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache){
        this.capControlCache = capControlCache;
    }

    @Autowired
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
