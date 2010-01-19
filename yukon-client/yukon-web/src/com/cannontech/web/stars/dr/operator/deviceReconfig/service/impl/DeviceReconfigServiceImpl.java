package com.cannontech.web.stars.dr.operator.deviceReconfig.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigDeviceSelectionStyle;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigSelectionType;
import com.cannontech.web.stars.dr.operator.deviceReconfig.service.DeviceReconfigSelectionTypeHandler;
import com.cannontech.web.stars.dr.operator.deviceReconfig.service.DeviceReconfigService;
import com.google.common.collect.Lists;

public class DeviceReconfigServiceImpl implements DeviceReconfigService, InitializingBean {

	private List<DeviceReconfigSelectionTypeHandler> handlers;
	private Map<DeviceReconfigSelectionType, DeviceReconfigSelectionTypeHandler> handlersMap;
	
	@Override
	public List<LiteStarsLMHardware> getInventoryBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {
		
		DeviceReconfigSelectionTypeHandler handler = handlersMap.get(selectionType);
		return handler.getInventoryFromRequest(request, userContext);
	}
	
	@Override
	public int getInventoryCountBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {
		
		DeviceReconfigSelectionTypeHandler handler = handlersMap.get(selectionType);
		return handler.getInventoryCountFromRequest(request, userContext);
	}
	
	@Override
	public List<Integer> getInventoryIdsBySelectionTypeFromRequest(DeviceReconfigSelectionType selectionType, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		DeviceReconfigSelectionTypeHandler handler = handlersMap.get(selectionType);
		return handler.getInventoryIdsFromRequest(request, userContext);
	}
	
	@Override
	public List<LiteStarsLMHardware> getInventoryIntersectionFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {
		
		List<List<LiteStarsLMHardware>> inventoryLists = Lists.newArrayList();
		List<LiteStarsLMHardware> commonInventory = Lists.newArrayList();
		
		if (DeviceReconfigDeviceSelectionStyle.SELECTION.equals(deviceReconfigOptions.getDeviceSelectionStyle())) {
			
			// load group
			if (deviceReconfigOptions.getLoadGroupPaoIds().size() > 0) {
				
				DeviceReconfigSelectionTypeHandler handler = handlersMap.get(DeviceReconfigSelectionType.LOAD_GROUP);
				List<LiteStarsLMHardware> inventory = handler.getInventoryFromReconfigOptions(deviceReconfigOptions, userContext);
				
				inventoryLists.add(inventory);
				commonInventory.addAll(inventory);
			}
			
			// program
			if (deviceReconfigOptions.getLoadProgramPaoIds().size() > 0) {
				
				DeviceReconfigSelectionTypeHandler handler = handlersMap.get(DeviceReconfigSelectionType.PROGRAM);
				List<LiteStarsLMHardware> inventory = handler.getInventoryFromReconfigOptions(deviceReconfigOptions, userContext);
				
				inventoryLists.add(inventory);
				commonInventory.addAll(inventory);
			}
		}
		
		for (List<LiteStarsLMHardware> inventoryList : inventoryLists) {
			commonInventory.retainAll(inventoryList);
		}
		
		return commonInventory;
	}
	
	@Override
	public List<Integer> getInventoryIdsIntersectionFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {

		List<List<Integer>> inventoryIdsLists = Lists.newArrayList();
		List<Integer> commonInventoryIds = Lists.newArrayList();
		
		if (DeviceReconfigDeviceSelectionStyle.SELECTION.equals(deviceReconfigOptions.getDeviceSelectionStyle())) {
			
			// load group
			if (deviceReconfigOptions.getLoadGroupPaoIds().size() > 0) {
				
				DeviceReconfigSelectionTypeHandler handler = handlersMap.get(DeviceReconfigSelectionType.LOAD_GROUP);
				List<Integer> inventoryIds = handler.getInventoryIdsFromReconfigOptions(deviceReconfigOptions, userContext);
				
				inventoryIdsLists.add(inventoryIds);
				commonInventoryIds.addAll(inventoryIds);
			}
			
			// program
			if (deviceReconfigOptions.getLoadProgramPaoIds().size() > 0) {
				
				DeviceReconfigSelectionTypeHandler handler = handlersMap.get(DeviceReconfigSelectionType.PROGRAM);
				List<Integer> inventoryIds = handler.getInventoryIdsFromReconfigOptions(deviceReconfigOptions, userContext);
				
				inventoryIdsLists.add(inventoryIds);
				commonInventoryIds.addAll(inventoryIds);
			}
		}
		
		for (List<Integer> inventoryList : inventoryIdsLists) {
			commonInventoryIds.retainAll(inventoryList);
		}
		
		return commonInventoryIds;
	}
	
	
	@Override
    public void afterPropertiesSet() throws Exception {

        this.handlersMap = new HashMap<DeviceReconfigSelectionType, DeviceReconfigSelectionTypeHandler>();
        for (DeviceReconfigSelectionTypeHandler handler : this.handlers) {
            this.handlersMap.put(handler.getDeviceReconfigSelectionType(), handler);
        }
    }
	
	@Autowired
	public void setHandlers(List<DeviceReconfigSelectionTypeHandler> handlers) {
		this.handlers = handlers;
	}
}
