package com.cannontech.web.smartNotifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.infrastructure.model.InfrastructureWarningDeviceCategory;

public class SmartNotificationEventFilter {
    
    private Date startDate;
    private Date endDate;
    private List<InfrastructureWarningDeviceCategory> categories = new ArrayList<>();
    
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public List<InfrastructureWarningDeviceCategory> getCategories() {
        return categories;
    }
    public void setCategories(List<InfrastructureWarningDeviceCategory> categories) {
        this.categories = categories;
    }
    

}
