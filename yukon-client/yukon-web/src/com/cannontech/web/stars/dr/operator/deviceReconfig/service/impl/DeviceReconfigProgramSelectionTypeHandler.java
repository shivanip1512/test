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

public class DeviceReconfigProgramSelectionTypeHandler implements DeviceReconfigSelectionTypeHandler {

	private StarsDatabaseCache starsDatabaseCache;
	private DeviceReconfigDao deviceReconfigDao;
	
	@Override
	public DeviceReconfigSelectionType getDeviceReconfigSelectionType() {
		return DeviceReconfigSelectionType.PROGRAM;
	}

	@Override
	public List<LiteStarsLMHardware> getInventoryFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> programPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareByProgramPaoIds(programPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public int getInventoryCountFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> programPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareCountByProgramPaoIds(programPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<Integer> getInventoryIdsFromRequest(HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		List<Integer> programPaoIds = getPaoIdsFromRequest(request);
		
		return deviceReconfigDao.getLmHardwareIdsByProgramPaoIds(programPaoIds, energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<LiteStarsLMHardware> getInventoryFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		return deviceReconfigDao.getLmHardwareByProgramPaoIds(deviceReconfigOptions.getLoadProgramPaoIds(), energyCompany.getEnergyCompanyID());
	}
	
	@Override
	public List<Integer> getInventoryIdsFromReconfigOptions(DeviceReconfigOptions deviceReconfigOptions, YukonUserContext userContext) {

		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		return deviceReconfigDao.getLmHardwareIdsByProgramPaoIds(deviceReconfigOptions.getLoadProgramPaoIds(), energyCompany.getEnergyCompanyID());
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getPaoIdsFromRequest(HttpServletRequest request) throws ServletRequestBindingException {
		
		String programPaoIdsStr = ServletRequestUtils.getRequiredStringParameter(request, "loadProgramPaoIds");
		PropertyEditor propertyEditor = (new IntegerSetType()).getPropertyEditor();
		propertyEditor.setAsText(programPaoIdsStr);
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
