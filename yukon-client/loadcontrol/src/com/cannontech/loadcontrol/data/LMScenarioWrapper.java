package com.cannontech.loadcontrol.data;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * A wrapper for LMPrograms that are instances of LiteYukonPAObject
 * LMProgramBase instance.
 * 
 * @author: 
 */
public class LMScenarioWrapper implements ILMData
{
	private String _yukCategory;
	private String _yukClass;
	private String _yukDescription;
	private Integer _yukID;
	private String _yukName;
	private Integer _yukType;

	public LMScenarioWrapper( LiteYukonPAObject liteLMScenario )
	{
		super();

		_yukCategory = PAOGroups.getCategory( liteLMScenario.getCategory() );
		_yukClass = PAOGroups.getPAOClass( liteLMScenario.getCategory(), liteLMScenario.getPaoClass() );
		_yukDescription = liteLMScenario.getPaoDescription();
		_yukID = new Integer( liteLMScenario.getYukonID() );
		_yukName = liteLMScenario.getPaoName();
		_yukType = new Integer( liteLMScenario.getType() );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonCategory() {
		return _yukCategory;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonClass() {
		return _yukClass;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonDescription() {
		return _yukDescription;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getYukonID() {
		return _yukID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getYukonName() {
		return _yukName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 10:43:42 AM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getYukonType() {
		return _yukType;
	}

	/**
	 * We want to keep this object immutable, so have the setters do nothing
	 */
	public void setYukonCategory(java.lang.String newYukonCategory)
	{}

	public void setYukonClass(java.lang.String newYukonClass)
	{}

	public void setYukonDescription(java.lang.String newYukonDescription)
	{}

	public void setYukonID(java.lang.Integer newYukonID)
	{}

	public void setYukonName(java.lang.String newYukonName) 
	{}

	public void setYukonType(java.lang.Integer newYukonType)
	{}

}
