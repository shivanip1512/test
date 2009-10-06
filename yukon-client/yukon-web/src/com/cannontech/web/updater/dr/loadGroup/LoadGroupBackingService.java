package com.cannontech.web.updater.dr.loadGroup;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.dr.loadgroup.service.LoadGroupService;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class LoadGroupBackingService extends UpdateBackingServiceBase<LMGroupBase> {
    private LoadGroupService loadGroupService = null;
    private LoadGroupFieldService loadGroupFieldService;

    @Override
    public DatedObject<LMGroupBase> getDatedObject(int loadGroupId) {
        DatedObject<LMGroupBase> datedGroup = loadGroupService.findDatedGroup(loadGroupId);
        return datedGroup;
    }
    
    @Override
    public Object getValue(DatedObject<LMGroupBase> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<LMGroupBase> backingField = 
            loadGroupFieldService.getBackingField(fieldName);
        
        LMGroupBase group = null;
        if (datedObject != null && (datedObject.getObject() instanceof LMDirectGroupBase)) {
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

