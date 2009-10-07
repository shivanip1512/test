package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.service.ControlAreaFieldService;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class ControlAreaBackingService extends UpdateBackingServiceBase<LMControlArea> {
    private ControlAreaService controlAreaService = null;
    private ControlAreaFieldService controlAreaFieldService;

    @Override
    public DatedObject<LMControlArea> getDatedObject(int controlAreaId) {
        DatedObject<LMControlArea> datedControlArea = 
            controlAreaService.getDatedControlArea(controlAreaId);
        return datedControlArea;
    }
    
    @Override
    public Object getValue(DatedObject<LMControlArea> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<LMControlArea> backingField = 
            controlAreaFieldService.getBackingField(fieldName);
        
        LMControlArea controlArea = null;
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

