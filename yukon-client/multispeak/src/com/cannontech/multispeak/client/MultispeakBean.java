package com.cannontech.multispeak.client;

import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.impl.RoleDaoImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.impl.MultispeakDaoImpl;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.spring.YukonSpringHook;

public class MultispeakBean
{
    private MultispeakDao multispeakDao = (MultispeakDaoImpl)YukonSpringHook.getBean("multispeakDao");
    private MultispeakFuncs multispeakFuncs = (MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs");
    
    private int selectedVendorID = 1;
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
        mspVendorList = multispeakDao.getMultispeakVendors();
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
     * @return Returns the selectedMspVendor.
     */
    public MultispeakVendor getSelectedMspVendor()
    {
        if (selectedMspVendor == null) {
            selectedMspVendor = multispeakDao.getMultispeakVendor(getSelectedVendorID());
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
    
    public Map<String, MultispeakInterface> getSelectedInterfacesMap()
    {
        return getSelectedMspVendor().getMspInterfaceMap();
    }

   public int getPrimaryCIS() {
       return multispeakFuncs.getPrimaryCIS();
   }

   public int getPaoNameAlias() {
       return multispeakFuncs.getPaoNameAlias();
   }
}
