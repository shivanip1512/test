package com.cannontech.multispeak.client;

import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.MultispeakRole;

public class MultispeakBean
{
    private int selectedVendorID = 1;
    private int primaryCIS = 0;
    private MultispeakVendor selectedMspVendor;
    private LiteYukonUser yukonUser;
    private List<MultispeakVendor> mspVendorList;
    
    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public List<MultispeakVendor> getMspVendorList()
    {
        mspVendorList = MultispeakFuncs.getMultispeakDao().getMultispeakVendors();
        return mspVendorList;
    }

    /**
     * @return Returns the selectedVendorID.
     */
    public int getSelectedVendorID()
    {
        return selectedVendorID;
    }

    /**
     * @param selectedVendorID The selectedVendorID to set.
     */
    public void setSelectedVendorID(int selectedVendorID)
    {
        //clear out the old vendor if this changes.
        if( this.selectedVendorID != selectedVendorID)
            selectedMspVendor = null;
        this.selectedVendorID = selectedVendorID;
    }

    /**
     * @return Returns the primaryCIS vendorID.
     */
    public int getPrimaryCIS()
    {
        return Integer.valueOf(DaoFactory.getRoleDao().getGlobalPropertyValue(MultispeakRole.MSP_PRIMARY_CB_VENDORID)).intValue();
    }

    /**
     * @return Returns the selectedMspVendor.
     */
    public MultispeakVendor getSelectedMspVendor()
    {
        if (selectedMspVendor == null)
        {
            selectedMspVendor = MultispeakFuncs.getMultispeakDao().getMultispeakVendor(getSelectedVendorID());
        }
        return selectedMspVendor;
    }
    
    /**
     * @param selectedMspVendor The selectedMspVendor to set.
     */
    public void setSelectedMspVendor(MultispeakVendor selectedMspVendor)
    {
        this.selectedMspVendor = selectedMspVendor;
    }

    public String[] getAllInterfaces()
    {
        return MultispeakDefines.getMSP_INTERFACE_ARRAY();
    }
    
    public Map getSelectedInterfacesMap()
    {
        return getSelectedMspVendor().getMspInterfaceMap();
    }
}
