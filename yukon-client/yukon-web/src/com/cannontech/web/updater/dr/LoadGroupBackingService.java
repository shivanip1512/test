package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class LoadGroupBackingService extends UpdateBackingServiceBase<LMDirectGroupBase> {
    private LoadGroupService loadGroupService = null;
    private LoadGroupFieldService loadGroupFieldService;

    @Override
    public DatedObject<LMDirectGroupBase> getDatedObject(int loadGroupId) {
        @SuppressWarnings("unchecked")
        DatedObject<LMDirectGroupBase> datedGroup = 
            (DatedObject<LMDirectGroupBase>) loadGroupService.findDatedGroup(loadGroupId);
        return datedGroup;
    }
    
    @Override
    public Object getValue(DatedObject<LMDirectGroupBase> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<LMDirectGroupBase> backingField = 
            loadGroupFieldService.getBackingField(fieldName);
        
        LMDirectGroupBase group = null;
        if (datedObject != null) {
            group = datedObject.getObject();
        }
        
        return backingField.getValue(group, userContext);
    }
    
    @Autowired
    public void setLoadGroupService(LoadGroupService loadGroupService) {
        this.loadGroupService = loadGroupService;
    }

    @Autowired
    public void setLoadGroupFieldService(LoadGroupFieldService loadGroupFieldService) {
        this.loadGroupFieldService = loadGroupFieldService;
    }
}

