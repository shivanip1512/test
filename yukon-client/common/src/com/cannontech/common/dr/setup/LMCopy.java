package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ @JsonSubTypes.Type(value = LoadGroupCopy.class, name = "LOAD_GROUP_COPY") })
public class LMCopy {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void buildModel(LMGroup group) {
        group.setPAOName(getName());
    }

}
