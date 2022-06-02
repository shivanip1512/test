package com.cannontech.common.device.terminal.model;

import com.cannontech.database.data.device.IEDBase;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TerminalCopy {

    private String name;
    private Boolean copyPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCopyPoints() {
        return copyPoints;
    }

    public void setCopyPoints(Boolean copyPoints) {
        this.copyPoints = copyPoints;
    }

    public void buildDBPersistent(IEDBase iedBase) {
        if (getName() != null) {
            iedBase.setPAOName(getName());
        }
    }

}
