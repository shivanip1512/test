package com.cannontech.web.stars.dr.operator.deviceReconfig.service.impl;

import java.beans.PropertyEditor;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.deviceReconfig.dao.DeviceReconfigDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.IntegerSetType;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigOptions;
import com.cannontech.web.stars.dr.operator.deviceReconfig.DeviceReconfigSelectionType;
import com.cannontech.web.stars.dr.operator.deviceReconfig.service.DeviceReconfigSelectionTypeHandler;

public class DeviceReconfigLoadGroupSelectionTypeHandler implements DeviceReconfigSelectionTypeHandler {

	private StarsDatabaseCache starsDatabaseCache;
	private DeviceReconfigDao deviceReconfigDao;
	
	@Override
	public DeviceReconfigSelectionType getDeviceReconfigSelectionType() {
		return DeviceReconfigSelectionType.LOAD_GROUP;
	}

	
	@Override
	public List<LiteStarsLMHardware> getInventoryFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> loadGroupPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareByLmGroupPaoIds(loadGroupPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public int getInventoryCountFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> loadGroupPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareCountByLmGroupPaoIds(loadGroupPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<Integer> getInventoryIdsFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> loadGroupPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareIdsByLmGroupPaoIds(loadGroupPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<LiteStarsLMHardware> getInventoryFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		return deviceReconfigDao.getLmHardwareByLmGroupPaoIds(deviceReconfigOptions.getLoadGroupPaoIds(), energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<Integer> getInventoryIdsFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		return deviceReconfigDao.getLmHardwareIdsByLmGroupPaoIds(deviceReconfigOptions.getLoadGroupPaoIds(), energyCompany.getEnergyCompanyID());
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getPaoIdsFromRequest(HttpServletRequest request) throws ServletRequestBindingException {
		
		String loadGroupPaoIdsStr = ServletRequestUtils.getRequiredStringParameter(request, "loadGroupPaoIds");
		PropertyEditor propertyEditor = (new IntegerSetType()).getPropertyEditor();
		propertyEditor.setAsText(loadGroupPaoIdsStr);
		return (List<Integer>)propertyEditor.getValue();
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
	
	@Autowired
	public void setDeviceReconfigDao(DeviceReconfigDao deviceReconfigDao) {
		this.deviceReconfigDao = deviceReconfigDao;
	}
}
