package com.cannontech.importer.capcontrol;
import com.cannontech.cbc.model.CBCCreationModel;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

public class CBCCreationImpl implements CBCCreationModel {

    private String name = null;
    private boolean disabled = false;
    private int portId = PAOGroups.INVALID;
    private int secType = PAOGroups.INVALID;
    private int wizType = PAOGroups.INVALID;
    private boolean nestedCreate = false;
    private CBCCreationImpl nestedWizard = null;
    
    
    public CBCCreationImpl(){
        super();
    }
    
    public Boolean getDisabled() {
        return disabled;
    }

    public String getName() {
        return name;
    }

    public CBCCreationModel getNestedWizard() {
        if( nestedWizard == null )
            nestedWizard = new CBCCreationImpl();

        return nestedWizard;
    }

    public Integer getPortID() {
        return portId;
    }

    public Integer getSecondaryType() {
        return secType;
    }

    public int getSelectedType() {
        return getSecondaryType() == null || getSecondaryType().intValue() == PAOGroups.INVALID
        ? getWizPaoType() : getSecondaryType().intValue();
    }

    public int getWizPaoType() {
        return wizType;
    }

    public boolean isCreateNested() {
        return nestedCreate;
    }

    public boolean isPortNeeded() {
        return DeviceTypesFuncs.cbcHasPort( getSecondaryType().intValue() );
    }
    
    public void setCreateNested(boolean val) {
        this.nestedCreate = val;
    }

    public void setDisabled(Boolean boolean1) {
        this.disabled = boolean1;
    }

    public void setName(String string) {
        this.name = new String(string);
    }

    public void setPortID(Integer integer) {
        this.portId = integer;
    }

    public void setSecondaryType(Integer integer) {
        this.secType = integer;
    }

    public void setWizPaoType(int i) {
        this.wizType = i;
    }

    public void updateDataModel() {
        // TODO Auto-generated method stub
        
    }
}
