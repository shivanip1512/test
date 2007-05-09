package com.cannontech.web.wizard;


import com.cannontech.cbc.model.CBCCreationModel;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Stores the sub types for wizards
 * @author ryan
 */
public class CBCWizardModel implements CBCCreationModel{
	
	private String name = null;
	private Boolean disabled = new Boolean(false);
	private boolean createNested = false;
	

	//what type we are currently creating
	private int wizPaoType = PAOGroups.INVALID;

	//use this type field if we need to create the sub type for creation:
	// ex: CAPBANKCONTROLLER -> CBC_7010  would use this.
	private Integer secondaryType = new Integer(PAOGroups.INVALID);

	//only use this for CBCs that have ports
	private Integer portID = new Integer(PAOGroups.INVALID);
	
	//a nested wizard that may or may not be used
	private CBCWizardModel nestedWizard = null;


	/**
	 * 
	 */
	public CBCWizardModel() {
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getSelectedType()
     */
	public int getSelectedType() {
		return getSecondaryType() == null || getSecondaryType().intValue() == PAOGroups.INVALID
					? getWizPaoType() : getSecondaryType().intValue();
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#isPortNeeded()
     */
	public boolean isPortNeeded() {
		return DeviceTypesFuncs.cbcHasPort( getSecondaryType().intValue() );
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getWizPaoType()
     */
	public int getWizPaoType() {
		return wizPaoType;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setWizPaoType(int)
     */
	public void setWizPaoType(int i) {
		wizPaoType = i;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getDisabled()
     */
	public Boolean getDisabled() {
		return disabled;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getName()
     */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setDisabled(java.lang.Boolean)
     */
	public void setDisabled(Boolean boolean1) {
		disabled = boolean1;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setName(java.lang.String)
     */
	public void setName(String string) {
		name = string;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getSecondaryType()
     */
	public Integer getSecondaryType() {
		return secondaryType;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setSecondaryType(java.lang.Integer)
     */
	public void setSecondaryType(Integer integer) {
		secondaryType = integer;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getPortID()
     */
	public Integer getPortID() {
		return portID;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setPortID(java.lang.Integer)
     */
	public void setPortID(Integer integer) {
		portID = integer;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getNestedWizard()
     */
	public CBCWizardModel getNestedWizard() {
		
		if( nestedWizard == null )
			nestedWizard = new CBCWizardModel();

		return nestedWizard;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#isCreateNested()
     */
	public boolean isCreateNested() {
		return createNested;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setCreateNested(boolean)
     */
	public void setCreateNested( boolean val ) {
		createNested = val;
	}

    /* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#updateDataModel()
     */
    public void updateDataModel() {
        
    }

}
