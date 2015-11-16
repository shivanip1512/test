package com.cannontech.web.wizard;


import com.cannontech.cbc.service.CapControlCreationModel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Stores the sub types for wizards
 * @author ryan
 */
public class CBCWizardModel implements CapControlCreationModel{
	
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
	
	public void reset() {
	    name = "";
	    disabled = new Boolean(false);
	    createNested = false;
	    wizPaoType = PAOGroups.INVALID;
	    secondaryType = new Integer(PAOGroups.INVALID);
	    portID = new Integer(PAOGroups.INVALID);
	    nestedWizard = null;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getSelectedType()
     */
	@Override
    public int getSelectedType() {
		return getSecondaryType() == null || getSecondaryType().intValue() == PAOGroups.INVALID
					? getWizPaoType() : getSecondaryType().intValue();
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#isPortNeeded()
     */
	@Override
    public boolean isPortNeeded() {
	    int cbcType = getSecondaryType().intValue();
	    PaoType paoType;
        try {
            paoType = PaoType.getForId(cbcType);
        } catch (IllegalArgumentException e) {
            return false;
        }
        //All the CBCs that require a port for communications
        return paoType == PaoType.DNP_CBC_6510 || 
                paoType == PaoType.CBC_7020 || 
                paoType == PaoType.CBC_7022 || 
                paoType == PaoType.CBC_7023 || 
                paoType == PaoType.CBC_7024 || 
                paoType == PaoType.CBC_8020 || 
                paoType == PaoType.CBC_8024 || 
                paoType == PaoType.CBC_DNP;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getWizPaoType()
     */
	@Override
    public int getWizPaoType() {
		return wizPaoType;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setWizPaoType(int)
     */
	@Override
    public void setWizPaoType(int i) {
		wizPaoType = i;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getDisabled()
     */
	@Override
    public Boolean getDisabled() {
		return disabled;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getName()
     */
	@Override
    public String getName() {
		return name;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setDisabled(java.lang.Boolean)
     */
	@Override
    public void setDisabled(Boolean boolean1) {
		disabled = boolean1;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setName(java.lang.String)
     */
	@Override
    public void setName(String string) {
		name = string;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getSecondaryType()
     */
	@Override
    public Integer getSecondaryType() {
		return secondaryType;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setSecondaryType(java.lang.Integer)
     */
	@Override
    public void setSecondaryType(Integer integer) {
		secondaryType = integer;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getPortID()
     */
	@Override
    public Integer getPortID() {
		return portID;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setPortID(java.lang.Integer)
     */
	@Override
    public void setPortID(Integer integer) {
		portID = integer;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#getNestedWizard()
     */
	@Override
    public CBCWizardModel getNestedWizard() {
		
		if( nestedWizard == null )
			nestedWizard = new CBCWizardModel();

		return nestedWizard;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#isCreateNested()
     */
	@Override
    public boolean isCreateNested() {
		return createNested;
	}
	
	public boolean isRegulator(){
		int wizPaoType = getWizPaoType();
		
		if (wizPaoType == CapControlTypes.CAP_CONTROL_LTC) {
			return true;
		} else if (wizPaoType == CapControlTypes.GANG_OPERATED_REGULATOR) {
			return true;
		} else if (wizPaoType == CapControlTypes.PHASE_OPERATED_REGULATOR) {
			return true;
		}
	    return false;
	}

	/* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#setCreateNested(boolean)
     */
	@Override
    public void setCreateNested( boolean val ) {
		createNested = val;
	}

    /* (non-Javadoc)
     * @see com.cannontech.web.wizard.CreateModel#updateDataModel()
     */
    @Override
    public void updateDataModel() {
        
    }

}
