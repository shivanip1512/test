package com.cannontech.web.updater.dr;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.controlarea.model.TriggerDisplayField;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class TriggerBackingService implements UpdateBackingService {
    private LoadControlClientConnection loadControlClientConnection = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;

    @Override
    public String getLatestValue(String identifier, long afterDateLong,
            YukonUserContext userContext) {
        String[] idBits = identifier.split("\\/");
        int controlAreaId = Integer.parseInt(idBits[0]);
        int triggerNumber = Integer.parseInt(idBits[1]);
        TriggerDisplayField displayField = TriggerDisplayField.valueOf(idBits[2]);

        DatedObject<LMControlArea> datedControlArea =
            loadControlClientConnection.getDatedControlArea(controlAreaId);

        MessageSource messageSource = messageSourceResolver.getMessageSource(userContext);
        if (datedControlArea == null) {
            if (displayField.isCssClass()) {
                return "not_in_control_area";
            }
            return messageSource.getMessage("yukon.web.modules.dr.fieldNotInLoadManagement",
                                            null,
                                            userContext.getLocale());
        }

        Date afterDate = new Date(afterDateLong);
        if (!datedControlArea.getDate().before(afterDate)) {
            LMControlAreaTrigger trigger = datedControlArea.getObject()
                                                           .getTrigger(triggerNumber);
            return messageSource.getMessage(displayField.getValue(trigger),
                                            userContext.getLocale());
        }

        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String identifier,
            long afterDate, YukonUserContext userContext) {
        return true;
    }

    @Autowired
    public void setLoadControlClientConnection(
            LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
