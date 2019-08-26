package com.cannontech.common.device.programming.dao;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.model.MeterProgram;

public interface MeterProgrammingDao {

	/**
	 * Returns program by guid
	 */
	MeterProgram getMeterProgram(String guid);

	/**
	 * Saves program
	 * 
	 * @throw DuplicateException - if description is used by another program
	 */
	void saveMeterProgram(MeterProgram program);

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
}
