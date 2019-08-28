package com.cannontech.common.device.programming.dao;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramConfiguration;

public interface MeterProgrammingDao {

	/**
	 * Returns program by guid
	 * 
	 * @throws NotFoundException
	 */
	MeterProgram getMeterProgram(String guid);

	/**
	 * Saves program
	 * @return guid created
	 * 
	 * @throw DuplicateException - if description is used by another program
	 */
	String saveMeterProgram(MeterProgram program);

	/**
	 * Returns all programs
	 */
	List<MeterProgram> getAllMeterPrograms();

	/**
	 * Assigns device to programs
	 * @param guid - program guid
	 */
	void assignDevicesToProgram(String guid, List<SimpleDevice> devices);

	/**
	 * Unassigns device from programs
	 */
	void unassignDeviceFromProgram(int deviceId);

	/**
	 * Deletes meter program
	 */
	void deleteMeterProgram(String guid);
	
	/**
	 * Returns program by device id
	 */
	MeterProgram getProgramByDeviceId(int deviceId);

	/**
	 * Returns program configuration by device id, throws exception if no program found
	 *
	 * @throws NotFoundException
	 */
	MeterProgramConfiguration getProgramConfigurationByDeviceId(int deviceId);
}
