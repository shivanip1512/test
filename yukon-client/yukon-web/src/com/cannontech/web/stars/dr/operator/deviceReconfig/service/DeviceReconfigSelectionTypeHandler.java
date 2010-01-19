package com.cannontech.web.stars.dr.operator.deviceReconfig.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigSelectionType;

public interface DeviceReconfigSelectionTypeHandler {

	public List<LiteStarsLMHardware> getInventoryFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	public int getInventoryCountFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	public List<Integer> getInventoryIdsFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	
	public List<LiteStarsLMHardware> getInventoryFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext);
	public List<Integer> getInventoryIdsFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext);
    
    public DeviceReconfigSelectionType getDeviceReconfigSelectionType();
}
