package com.cannontech.web.common.dashboard.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class WidgetHelper {
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private final static String helpTextBaseKey = "yukon.web.widgets.";

    public void setWidgetHelpTextArguments(List<Widget> allWidgets, YukonUserContext yukonUserContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        for (Widget widget : allWidgets) {
            switch (widget.getType()) {
            case ASSET_AVAILABILITY:
                long totalHours = globalSettingDao.getInteger(GlobalSettingType.LAST_RUNTIME_HOURS);
                long totalDays = TimeUnit.DAYS.convert(totalHours, TimeUnit.HOURS);
                long hoursRemaining = TimeUtil.hoursRemainingAfterConveritngToDays(totalHours);
                String days = accessor.getMessage(helpTextBaseKey + "days", totalDays);
                String hours = "";
                if (totalDays > 0) {
                    hours = accessor.getMessage(helpTextBaseKey + "hours.withDays", hoursRemaining);
                } else {
                    hours = accessor.getMessage(helpTextBaseKey + "hours", hoursRemaining);
                }
                widget.setHelpText(accessor.getMessage(helpTextBaseKey + widget.getType().getBeanName() + ".helpText",
                    days, hours, globalSettingDao.getString(GlobalSettingType.LAST_COMMUNICATION_HOURS)));
                break;
            default:
                widget.setHelpText(
                    accessor.getMessageWithDefault(helpTextBaseKey + widget.getType().getBeanName() + ".helpText", ""));
            }
        }
    }
}
