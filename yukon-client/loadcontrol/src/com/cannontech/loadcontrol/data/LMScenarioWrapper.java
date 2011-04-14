package com.cannontech.loadcontrol.data;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * A wrapper for LMPrograms that are instances of LiteYukonPAObject
 * LMProgramBase instance.
 */
public class LMScenarioWrapper implements ILMData {
	private PaoCategory _yukCategory;
	private PaoClass _yukClass;
	private String _yukDescription;
	private Integer _yukID;
	private String _yukName;
	private PaoType _yukType;

	public LMScenarioWrapper(LiteYukonPAObject liteLMScenario) {
		super();

		_yukCategory = liteLMScenario.getPaoType().getPaoCategory();
		_yukClass = liteLMScenario.getPaoType().getPaoClass();
		_yukDescription = liteLMScenario.getPaoDescription();
		_yukID = new Integer(liteLMScenario.getYukonID());
		_yukName = liteLMScenario.getPaoName();
		_yukType = liteLMScenario.getPaoType();
	}

	public PaoCategory getYukonCategory() {
		return _yukCategory;
	}

	public PaoClass getYukonClass() {
		return _yukClass;
	}

	public java.lang.String getYukonDescription() {
		return _yukDescription;
	}

	public java.lang.Integer getYukonID() {
		return _yukID;
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
	public void setYukonCategory(PaoCategory newYukonCategory) {
	}

	public void setYukonClass(PaoClass newYukonClass) {
	}

	public void setYukonDescription(java.lang.String newYukonDescription) {
	}

	public void setYukonID(java.lang.Integer newYukonID) {
	}

	public void setYukonName(java.lang.String newYukonName) {
	}

	public void setYukonType(PaoType newYukonType) {
	}

}
