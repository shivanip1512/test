package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.service.ControlAreaFieldService;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class ControlAreaBackingService extends UpdateBackingServiceBase<ControlAreaItem> {
    private ControlAreaService controlAreaService = null;
    private ControlAreaFieldService controlAreaFieldService;

    @Override
    public DatedObject<ControlAreaItem> getDatedObject(int controlAreaId) {
        DatedObject<ControlAreaItem> datedControlArea = 
            controlAreaService.getDatedControlArea(controlAreaId);
        return datedControlArea;
    }
    
    @Override
    public Object getValue(DatedObject<ControlAreaItem> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<ControlAreaItem> backingField = 
            controlAreaFieldService.getBackingField(fieldName);
        
        ControlAreaItem controlArea = null;
        if (datedObject != null) {
            controlArea = datedObject.getObject();
        }
        
        return backingField.getValue(controlArea, userContext);
    }
    
    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setControlAreaFieldService(ControlAreaFieldService controlAreaFieldService) {
        this.controlAreaFieldService = controlAreaFieldService;
    }
}

