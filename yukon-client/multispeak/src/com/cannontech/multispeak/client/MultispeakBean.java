package com.cannontech.multispeak.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.spring.YukonSpringHook;

public class MultispeakBean
{
    private MultispeakDao multispeakDao = YukonSpringHook.getBean("multispeakDao", MultispeakDao.class);
    private MultispeakFuncs multispeakFuncs = YukonSpringHook.getBean("multispeakFuncs", MultispeakFuncs.class);
    
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
     * @return Returns the selectedMspVendor.
     */
    public MultispeakVendor getSelectedMspVendor()
    {
        if (selectedMspVendor == null) {
            selectedMspVendor = multispeakDao.getMultispeakVendor("Cannon", "");
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
    
    public Map<String, MultispeakInterface> getSelectedInterfacesMap()
    {
        return getSelectedMspVendor().getMspInterfaceMap();
    }
    
    public String[] getPossibleInterfaces() {
    	
    	if (getSelectedMspVendor().getVendorID() == 1) {
    		return MultispeakDefines.MSP_SERVER_INTERFACE_ARRAY;
    	} else {
    		return MultispeakDefines.MSP_CLIENT_INTERFACE_ARRAY;
    	}
    }
    public String[] getClientInterfaces() {
    	
    	return MultispeakDefines.MSP_CLIENT_INTERFACE_ARRAY;
    }

   public int getPrimaryCIS() {
       return multispeakFuncs.getPrimaryCIS();
   }

   public int getPaoNameAlias() {
       return multispeakFuncs.getPaoNameAlias();
   }
}
