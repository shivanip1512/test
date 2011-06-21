package com.cannontech.web.updater.capcontrol.handler;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.TapOperation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
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
    private static final Logger log = YukonLogManager.getLogger(VoltageRegulatorTapTooltipHandler.class);
    
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

            if (tapOp == TapOperation.NONE) {
                String na = messageSourceAccessor.getMessage("yukon.web.defaults.na");
                return na;
            }
            
            String tapOpString = (tapOp == TapOperation.LOWER_TAP) ? "lowerTap." : "raiseTap.";
            String recentString = regulatorFlags.isRecentOperation() ? "recent." : "notRecent.";
            String warningString = isWarning(regulatorFlags) ? "warning" : "notWarning";
            String key = "yukon.web.modules.capcontrol.ivvc.lastOperation.tooltip." + 
                            tapOpString + recentString + warningString;
            String text = messageSourceAccessor.getMessage(key);
            String opTimeString = dateFormattingService.format(opTime, DateFormatEnum.BOTH, userContext);
            text += ": " + opTimeString;
            return text;
        } catch (NotFoundException nfe) {
            log.info("Voltage Regulator with Id " + id + " not found in cache.");
            // This can happen if we delete an object and return
            // to a page that that used to have that object before
            // the server was able to update the cache.
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
