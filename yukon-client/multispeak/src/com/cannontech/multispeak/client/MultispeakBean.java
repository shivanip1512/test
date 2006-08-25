package com.cannontech.multispeak.client;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;

public class MultispeakBean
{
    private String selectedCompanyName = "Cannon";
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
     * @return Returns the selectedCompanyName.
     */
    public String getSelectedCompanyName()
    {
        return selectedCompanyName;
    }

    /**
     * @param selectedCompanyName The selectedCompanyName to set.
     */
    public void setSelectedCompanyName(String selectedCompanyName)
    {
        //clear out the old vendor if this changes.
        if( !this.selectedCompanyName.equalsIgnoreCase(selectedCompanyName))
            selectedMspVendor = null;
        this.selectedCompanyName = selectedCompanyName;
    }

    /**
     * @return Returns the selectedMspVendor.
     */
    public MultispeakVendor getSelectedMspVendor()
    {
        if (selectedMspVendor == null)
        {
            selectedMspVendor = MultispeakFuncs.getMultispeakDao().getMultispeakVendor(getSelectedCompanyName());
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
