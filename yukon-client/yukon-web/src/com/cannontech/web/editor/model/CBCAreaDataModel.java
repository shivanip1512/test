package com.cannontech.web.editor.model;

import java.util.List;

import com.cannontech.database.data.capcontrol.CapControlArea;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class CBCAreaDataModel extends EditorDataModelImpl {
    CapControlArea areaPers;
    List<LiteYukonPAObject> assignedSubs;
    List<LiteYukonPAObject> unassignedSubs;
    
    public CBCAreaDataModel(CapControlArea area) {
        super(area);
        areaPers = area;
    }
    
    public void addLiteObj (LiteYukonPAObject sub) {
        
    }
    
    public void removeLiteObj (LiteYukonPAObject sub) {
        
    }

}
