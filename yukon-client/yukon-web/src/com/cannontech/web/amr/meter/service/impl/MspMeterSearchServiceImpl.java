package com.cannontech.web.amr.meter.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.web.amr.meter.MspFilterBy;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;

public class MspMeterSearchServiceImpl implements MspMeterSearchService {

	private MspObjectDao mspObjectDao;
	private MultispeakFuncs multispeakFuncs;
	private MultispeakDao multispeakDao;
	
	private Map<MspSearchField, MspMeterSearchMethodResultProvider> methodResultProviderMap = new HashMap<MspSearchField, MspMeterSearchMethodResultProvider>();
	private List<MspSearchField> mspSearchFields;
	
	@Override
	public List<MspFilterBy> getMspFilterByList() {
		
		List<MspFilterBy> msFilterByList = new ArrayList<MspFilterBy>();
		for (MspSearchField mspSearchField : mspSearchFields) {
			msFilterByList.add(new MspFilterBy(mspSearchField.name(), methodResultProviderMap.get(mspSearchField)));
        }
		
		return msFilterByList;
	}
	
	@PostConstruct
    public void init() throws Exception {
	    int vendorId = multispeakFuncs.getPrimaryCIS();
	    loadMspSearchFields(vendorId);
    }
	
	@Override
    public void loadMspSearchFields(int vendorId) {
	    //set to available mspSearchFields based on methods that vendor supports
        mspSearchFields = new ArrayList<MspSearchField>();
        
        if (vendorId > 0) {
            
            MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(vendorId);
            if (mspVendor.getMspInterfaceMap().containsKey(MultispeakDefines.CB_Server_STR)) {
            List<String> mspMethodNames = mspObjectDao.findMethods(MultispeakDefines.CB_Server_STR, mspVendor);
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
