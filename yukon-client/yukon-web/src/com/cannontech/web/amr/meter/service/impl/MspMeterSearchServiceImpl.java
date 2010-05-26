package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.amr.meter.MspFilterBy;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

public class MspMeterSearchServiceImpl implements MspMeterSearchService, InitializingBean {

	private RolePropertyDao rolePropertyDao;
	private MspObjectDao mspObjectDao;
	private MultispeakFuncs multispeakFuncs;
	private MultispeakDao multispeakDao;
	
	private Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMap = new HashMap<MspSearchField, MspMeterSearchMethodResultProvider>();
	private List<MspSearchField> mspSearchFields;
	
	@Override
	public List<MspFilterBy> getMspFilterByList() {
		
		List<MspFilterBy> msFilterByList = new ArrayList<MspFilterBy>();
		for (MspSearchField mspSearchField : mspSearchFields) {
			msFilterByList.add(new MspFilterBy(mspSearchField.getName(), methodResultProviderMap.get(mspSearchField)));
        }
		
		return msFilterByList;
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
	    loadMspSearchFields();
    }
	
	public void loadMspSearchFields() {
	    //set to available mspSearchFields based on methods that vendor supports
        mspSearchFields = new ArrayList<MspSearchField>();
        
        int vendorId = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MSP_PRIMARY_CB_VENDORID, null);
        if (vendorId > 0) {
            
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
            List<String> mspMethodNames = mspObjectDao.getMspMethods(MultispeakDefines.CB_Server_STR, mspVendor);
            
            MspSearchField[] allMspSearchFields = MspSearchField.values();
            for (MspSearchField mspSearchField : allMspSearchFields) {
                
                for (String mspMethodName : mspMethodNames) {
                    
                    if (mspSearchField.getRequiredMspMethodName().equalsIgnoreCase(mspMethodName)) {
                        
                        if (!methodResultProviderMap.keySet().contains(mspSearchField)) {
                            throw new IllegalArgumentException("MspSearchField (" + mspSearchField + ") has no associated MspMeterSearchMethodResultProvider");
                        }
                        
                        mspSearchFields.add(mspSearchField);
                        break;
                    }
                }
            }
        }
	}
	
	@Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
		this.multispeakFuncs = multispeakFuncs;
	}
    
    @Autowired
    public void setMultispeakDao(MultispeakDao multispeakDao) {
		this.multispeakDao = multispeakDao;
	}
    
    @Autowired
    public void setMethodResultProviders(List<MspMeterSearchMethodResultProvider> methodResultProviders) {
		
		for (MspMeterSearchMethodResultProvider methodResultProvider : methodResultProviders) {
			methodResultProviderMap.put(methodResultProvider.getSearchField(), methodResultProvider);
    	}
	}
}
