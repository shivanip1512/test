package com.cannontech.common.i18n;

import com.cannontech.user.YukonUserContext;

public interface ObjectFormattingService {
    public String formatObjectAsValue(Object object, YukonUserContext userContext);
    public String formatObjectAsKey(Object object, YukonUserContext userContext);
}
