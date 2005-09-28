package com.cannontech.web.editor;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Stores the sub types for wizards
 * @author ryan
 */
public class CBCWizardModel {
	
	private String name = null;
	private Boolean disabled = new Boolean(false);
	

	//what type we are currently creating
	private int wizPaoType = PAOGroups.INVALID;

	//use this type field if we need to create the sub type for creation:
	// ex: CAPBANKCONTROLLER -> CBC_7010  would use this.
	private Integer secondaryType = new Integer(PAOGroups.INVALID);

	//only use this for CBCs that have ports
	private Integer portID = new Integer(PAOGroups.INVALID);
	

	/**
	 * 
	 */
	public CBCWizardModel() {
		super();
	}

	/**
	 * Returns the type that is selected based on on the fact that if the 
	 *  secondaryType is set return it, else return type
	 * @return
	 */
	public int getSelectedType() {
		return getSecondaryType() == null || getSecondaryType().intValue() == PAOGroups.INVALID
					? getWizPaoType() : getSecondaryType().intValue();
	}
	
	/**
	 * Returns all the CBC that require a port for communications.
	 * @return
	 */
	public boolean isPortNeeded() {
		return DeviceTypesFuncs.cbcHasPort( getSecondaryType().intValue() );
	}

	/**
	 * @return
	 */
	public int getWizPaoType() {
		return wizPaoType;
	}

	/**
	 * @param i
	 */
	public void setWizPaoType(int i) {
		wizPaoType = i;
	}

	/**
	 * @return
	 */
	public Boolean getDisabled() {
		return disabled;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param boolean1
	 */
	public void setDisabled(Boolean boolean1) {
		disabled = boolean1;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @return
	 */
	public Integer getSecondaryType() {
		return secondaryType;
	}

	/**
	 * @param integer
	 */
	public void setSecondaryType(Integer integer) {
		secondaryType = integer;
	}

	/**
	 * @return
	 */
	public Integer getPortID() {
		return portID;
	}

	/**
	 * @param integer
	 */
	public void setPortID(Integer integer) {
		portID = integer;
	}

}
