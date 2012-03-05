package com.cannontech.analysis.tablemodel;

import com.cannontech.user.YukonUserContext;


/**
 * If an class implements this interface in addition to BareReportModel, these
 * values will be set at initialization.
 */
public interface UserContextModelAttributes {
    public void setUserContext(YukonUserContext userContext);
    public YukonUserContext getUserContext();
}