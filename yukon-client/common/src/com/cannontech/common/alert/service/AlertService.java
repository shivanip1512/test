package com.cannontech.common.alert.service;

import java.util.Collection;
import java.util.List;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AlertService {

    public void remove(int[] alertIds, LiteYukonUser user);
    
    public void add(Alert alert);
    
    public Collection<IdentifiableAlert> getAll(LiteYukonUser user);
    
    public long getLatestAlertTime(LiteYukonUser user);
    
    public int getCountForUser(LiteYukonUser user);
    
    public void setAlertClearHandlers(List<AlertClearHandler> alertClearHandlers);
    
}
