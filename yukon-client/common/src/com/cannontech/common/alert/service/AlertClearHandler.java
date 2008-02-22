package com.cannontech.common.alert.service;

import com.cannontech.common.alert.model.Alert;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AlertClearHandler {

    public void clear(Alert alert, LiteYukonUser user);
    
}
