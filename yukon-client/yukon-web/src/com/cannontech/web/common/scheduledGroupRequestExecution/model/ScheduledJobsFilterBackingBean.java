package com.cannontech.web.common.scheduledGroupRequestExecution.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoOnetimeFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.web.util.ListBackingBean;

public class ScheduledJobsFilterBackingBean extends ListBackingBean {
    private Date toDate;
    private Date fromDate;
    private ScheduleGroupRequestExecutionDaoEnabledFilter statusFilter =
        ScheduleGroupRequestExecutionDaoEnabledFilter.ANY;
    boolean excludePendingFilterBool = false;
    boolean includeOnetimeFilterBool = false;
    private String typeFilterAsString;
    
    public ScheduledJobsFilterBackingBean() {
        setSort("NEXT_RUN");
        setItemsPerPage(10);
    }
    
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public ScheduleGroupRequestExecutionDaoEnabledFilter getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(ScheduleGroupRequestExecutionDaoEnabledFilter statusFilter) {
        this.statusFilter = statusFilter;
    }

    public boolean isExcludePendingFilterBool() {
        return excludePendingFilterBool;
    }

    public void setExcludePendingFilterBool(boolean excludePendingFilterBool) {
        this.excludePendingFilterBool = excludePendingFilterBool;
    }

    public ScheduleGroupRequestExecutionDaoPendingFilter getExcludePendingFilter() {
        if (excludePendingFilterBool) {
            return ScheduleGroupRequestExecutionDaoPendingFilter.EXECUTED_ONLY;
        }
        return ScheduleGroupRequestExecutionDaoPendingFilter.ANY;
    }

    public boolean isIncludeOnetimeFilterBool() {
        return includeOnetimeFilterBool;
    }

    public void setIncludeOnetimeFilterBool(boolean includeOnetimeFilterBool) {
        this.includeOnetimeFilterBool = includeOnetimeFilterBool;
    }

    public ScheduleGroupRequestExecutionDaoOnetimeFilter getIncludeOnetimeFilter() {
        if (includeOnetimeFilterBool) {
            return ScheduleGroupRequestExecutionDaoOnetimeFilter.INCLUDE_ONETIME;
        }
        return ScheduleGroupRequestExecutionDaoOnetimeFilter.EXCLUDE_ONETIME;
    }

    public String getTypeFilterAsString() {
        return typeFilterAsString;
    }

    public void setTypeFilterAsString(String typeFilterAsString) {
        this.typeFilterAsString = typeFilterAsString;
    }

    public List<DeviceRequestType> getTypeFilterAsList() {
        if (typeFilterAsString != null && !typeFilterAsString.equals("ANY")) {
            return Collections.singletonList(DeviceRequestType.valueOf(typeFilterAsString));
        }
        return null;
    }
}
