package com.cannontech.stars.dr.deviceReconfig.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.deviceReconfig.model.DeviceReconfigDeviceType;

public interface DeviceReconfigDao {

	public List<DeviceReconfigDeviceType> getDeviceTypes(int energyCompanyId);
	
	/**
	 * Method to find all inventory in given load groups for a given energy company.
	 * @param lmGroupPaoIds
	 * @param energyCompanyId
	 * @return
	 */
	public List<LiteStarsLMHardware> getLmHardwareByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId);
	
	public int getLmHardwareCountByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId);
	public List<Integer> getLmHardwareIdsByLmGroupPaoIds(List<Integer> lmGroupPaoIds, int energyCompanyId);
	
	/**
	 * Method to find all inventory in given program for a given energy company.
	 * @param programPaoIds - program pao ids
	 * @param energyCompanyId
	 * @return
	 */
	public List<LiteStarsLMHardware> getLmHardwareByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId);
	
	public int getLmHardwareCountByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId);
	public List<Integer> getLmHardwareIdsByProgramPaoIds(List<Integer> programPaoIds, int energyCompanyId);
}
