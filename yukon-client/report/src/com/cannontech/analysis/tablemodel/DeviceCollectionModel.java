package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;

import com.cannontech.user.YukonUserContext;

public class DeviceCollectionModel extends DeviceGroupModelBase {
    
	private String collectionDescription;
	
	@Override
	public String getTitle() {
		return "Device Collection Report";
	}
	
    @Override
    public boolean isIncludeSubGroups() {
    	return true;
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        info.put("Collection Description", collectionDescription);
        return info;
    }
    
    public String getCollectionDescription() {
		return collectionDescription;
	}
    public void setCollectionDescription(String collectionDescription) {
		this.collectionDescription = collectionDescription;
	}
}
