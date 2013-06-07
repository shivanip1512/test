package com.cannontech.messaging.message.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * A wrapper for LMPrograms that are instances of LiteYukonPAObject
 * LMProgramBase instance.
 */
public class ScenarioWrapper implements Data {

    private String _yukDescription;
    private Integer _yukId;
    private String _yukName;
    private PaoType _yukType;

    public ScenarioWrapper(LiteYukonPAObject liteLMScenario) {
        super();

        _yukDescription = liteLMScenario.getPaoDescription();
        _yukId = new Integer(liteLMScenario.getYukonId());
        _yukName = liteLMScenario.getPaoName();
        _yukType = liteLMScenario.getPaoType();
    }

    public java.lang.String getYukonDescription() {
        return _yukDescription;
    }

    public java.lang.Integer getYukonId() {
        return _yukId;
    }

    public java.lang.String getYukonName() {
        return _yukName;
    }

    public PaoType getYukonType() {
        return _yukType;
    }

    /**
     * We want to keep this object immutable, so have the setters do nothing
     */
    public void setYukonDescription(java.lang.String newYukonDescription) {}

    public void setYukonId(java.lang.Integer newYukonId) {}

    public void setYukonName(java.lang.String newYukonName) {}

    public void setYukonType(PaoType newYukonType) {}

}
