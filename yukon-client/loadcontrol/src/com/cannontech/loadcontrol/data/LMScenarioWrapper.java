package com.cannontech.loadcontrol.data;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * A wrapper for LMPrograms that are instances of LiteYukonPAObject
 * LMProgramBase instance.
 */
public class LMScenarioWrapper implements ILMData {
	private String _yukCategory;
	private String _yukClass;
	private String _yukDescription;
	private Integer _yukID;
	private String _yukName;
	private String _yukType;

	public LMScenarioWrapper(LiteYukonPAObject liteLMScenario) {
		super();

		_yukCategory = PAOGroups.getCategory(liteLMScenario.getCategory());
		_yukClass = PAOGroups.getPAOClass(liteLMScenario.getCategory(),
				liteLMScenario.getPaoClass());
		_yukDescription = liteLMScenario.getPaoDescription();
		_yukID = new Integer(liteLMScenario.getYukonID());
		_yukName = liteLMScenario.getPaoName();
		_yukType = liteLMScenario.getPaoIdentifier().getPaoType().getDbString();
	}

	public java.lang.String getYukonCategory() {
		return _yukCategory;
	}

	public java.lang.String getYukonClass() {
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

	public String getYukonType() {
		return _yukType;
	}

	/**
	 * We want to keep this object immutable, so have the setters do nothing
	 */
	public void setYukonCategory(java.lang.String newYukonCategory) {
	}

	public void setYukonClass(java.lang.String newYukonClass) {
	}

	public void setYukonDescription(java.lang.String newYukonDescription) {
	}

	public void setYukonID(java.lang.Integer newYukonID) {
	}

	public void setYukonName(java.lang.String newYukonName) {
	}

	public void setYukonType(String newYukonType) {
	}

}
