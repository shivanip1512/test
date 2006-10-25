package com.cannontech.analysis.tablemodel;

import java.util.Date;

/**
 * If an class implements this interface in addition to BareReportModel, these
 * values will be set at initialization.
 */
public interface CommonModelAttributes {
    public void setEnergyCompanyId(int energyCompanyId);
    public void setStartDate(Date startDate);
    public void setStopDate(Date stopDate);
}