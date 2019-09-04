package com.cannontech.common.device.programming.dao;

import java.util.List;
import java.util.UUID;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.core.dao.NotFoundException;

public interface MeterProgrammingDao {

	/**
	 * Returns program by guid
	 * 
	 * @throws NotFoundException
	 */
	 MeterProgram getMeterProgram(UUID guid);

	/**
	 * Saves program
	 * @return guid created
	 * 
	 * @throw DuplicateException - if description is used by another program
	 */
	UUID saveMeterProgram(MeterProgram program);

	/**
	 * Returns all programs
	 */
	List<MeterProgram> getAllMeterPrograms();

	/**
	 * Assigns device to programs
	 * @param guid - program guid
	 */
	void assignDevicesToProgram(UUID guid, List<SimpleDevice> devices);

	/**
	 * Unassigns device from programs
	 */
	void unassignDeviceFromProgram(int deviceId);

	/**
	 * Deletes meter program
	 */
	void deleteMeterProgram(UUID guid);
	
	/**
	 * Returns program by device id
	 */
	MeterProgram getProgramByDeviceId(int deviceId);

	/**
	 * Returns program configuration by device id, throws exception if no program found
	 *
	 * @throws NotFoundException
	 */
	//MeterProgramConfiguration getProgramConfigurationByDeviceId(int deviceId);

	/**
	 * Returns status by device id
	 */
	MeterProgramStatus getMeterProgramStatus(int deviceId);

	/**
	 * Creates status
	 */
	void createMeterProgramStatus(MeterProgramStatus status);

	/**
	 * Updates status
	 */
	void updateMeterProgramStatus(MeterProgramStatus status);
}
