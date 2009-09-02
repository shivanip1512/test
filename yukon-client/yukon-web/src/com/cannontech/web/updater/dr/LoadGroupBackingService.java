package com.cannontech.web.updater.dr;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.controlarea.model.LoadGroupDisplayField;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class LoadGroupBackingService implements UpdateBackingService {
    private LoadControlClientConnection loadControlClientConnection = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;

    @Override
    public String getLatestValue(String identifier, long afterDateLong,
            YukonUserContext userContext) {
        String[] idBits = identifier.split("\\/");
        int loadGroupId = Integer.parseInt(idBits[0]);
        LoadGroupDisplayField displayField = LoadGroupDisplayField.valueOf(idBits[1]);

        DatedObject<LMGroupBase> datedGroup = loadControlClientConnection.getDatedGroup(loadGroupId);

        MessageSource messageSource = messageSourceResolver.getMessageSource(userContext);
        if (datedGroup == null || !(datedGroup.getObject() instanceof LMDirectGroupBase)) {
            if (displayField.isCssClass()) {
                return "not_in_control_area";
            }
            return messageSource.getMessage("yukon.web.modules.dr.fieldNotInLoadManagement",
                                            null,
                                            userContext.getLocale());
        }

        Date afterDate = new Date(afterDateLong);
        if (!datedGroup.getDate().before(afterDate)) {
            LMDirectGroupBase group = (LMDirectGroupBase) datedGroup.getObject();
            return messageSource.getMessage(displayField.getValue(group),
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

