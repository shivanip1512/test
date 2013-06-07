package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.controlarea.service.TriggerFieldService;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class TriggerBackingService extends UpdateBackingServiceBase<ControlAreaItem> {
    private ControlAreaService controlAreaService = null;
    private TriggerFieldService triggerFieldService;

    @Override
    public DatedObject<ControlAreaItem> getDatedObject(int controlAreaId) {
        DatedObject<ControlAreaItem> datedControlArea = 
            controlAreaService.getDatedControlArea(controlAreaId);
        return datedControlArea;
    }
    
    @Override
    public Object getValue(DatedObject<ControlAreaItem> datedObject, String[] idBits,
                           YukonUserContext userContext) {
        
        int triggerNumber = Integer.parseInt(idBits[1]);
        String fieldName = idBits[2];
        

        DemandResponseBackingField<ControlAreaTriggerItem> backingField = 
            triggerFieldService.getBackingField(fieldName);
        
        ControlAreaTriggerItem trigger = null;
        if (datedObject != null) {
            trigger = datedObject.getObject().getTrigger(triggerNumber);
        }
        
        return backingField.getValue(trigger, userContext);
    }
    
    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setTriggerFieldService(TriggerFieldService triggerFieldService) {
        this.triggerFieldService = triggerFieldService;
    }
}

