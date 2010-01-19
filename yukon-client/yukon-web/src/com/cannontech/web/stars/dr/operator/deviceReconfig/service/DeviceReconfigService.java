package com.cannontech.web.stars.dr.operator.deviceReconfig.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigSelectionType;

public interface DeviceReconfigService {

	public List<LiteStarsLMHardware> getInventoryBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	public int getInventoryCountBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	public List<Integer> getInventoryIdsBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException;
	
	public List<LiteStarsLMHardware> getInventoryIntersectionFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext);
	public List<Integer> getInventoryIdsIntersectionFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext);
}
