package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.user.YukonUserContext;

public class DeviceGroupModel extends DeviceGroupModelBase {
    
	@Override
	public String getTitle() {
		return "Device Group Report";
	}
	
    @Override
    public boolean isIncludeSubGroups() {
    	return false;
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        info.put("Group", deviceGroup.getFullName());
        info.put("Include Sub Groups", BooleanUtils.toStringYesNo(isIncludeSubGroups()));
        return info;
    }
}
