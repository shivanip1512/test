package com.cannontech.web.api.dr.setup.model;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMGroup;

public class LoadGroupFilteredResult {

    private LMDto group;
    private PaoType type;
    private LoadGroupBase<? extends LMGroup> details;

    public LMDto getGroup() {
        return group;
    }

    public void setGroup(LMDto group) {
        this.group = group;
    }

    public LoadGroupBase<? extends LMGroup> getDetails() {
        return details;
    }

    public void setDetails(LoadGroupBase<? extends LMGroup> details) {
        this.details = details;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

}
