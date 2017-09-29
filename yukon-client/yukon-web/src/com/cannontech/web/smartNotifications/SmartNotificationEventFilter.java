package com.cannontech.web.smartNotifications;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;

public class SmartNotificationEventFilter {
    
    private DateTime startDate;
    private DateTime endDate;
    private List<InfrastructureWarningDeviceCategory> categories = new ArrayList<>();
    
    public DateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }
    public DateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }
    public List<InfrastructureWarningDeviceCategory> getCategories() {
        return categories;
    }
    public void setCategories(List<InfrastructureWarningDeviceCategory> categories) {
        this.categories = categories;
    }
    

}
