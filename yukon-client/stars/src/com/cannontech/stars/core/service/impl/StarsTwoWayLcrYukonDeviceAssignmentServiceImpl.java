package com.cannontech.stars.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceAssignmentException;
import com.cannontech.stars.dr.hardware.exception.StarsTwoWayLcrYukonDeviceCreationException;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsInventory;

public class StarsTwoWayLcrYukonDeviceAssignmentServiceImpl implements StarsTwoWayLcrYukonDeviceAssignmentService {

	private DeviceCreationService deviceCreationService;
	private DeviceDao deviceDao;
	private DBPersistentDao dbPersistentDao;
	private PaoDao paoDao;
	
	public void assignTwoWayLcrDevice(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws StarsTwoWayLcrYukonDeviceCreationException {
		
		try {
			
			// NEW YUKON DEVICE
			if (starsInv.getTwoWayLcrSetupInfoDto() != null && starsInv.getTwoWayLcrSetupInfoDto().isNewDevice()) {
				
				checkSerialNumberMatchesAddress(starsInv);
				assignNewDeviceToLcr(liteInv, energyCompany, starsInv.getTwoWayLcrSetupInfoDto().getYukonDeviceTypeId(), starsInv.getTwoWayLcrSetupInfoDto().getDeviceName(), starsInv.getTwoWayLcrSetupInfoDto().getDemandRate(), true);
			
	        // EXISTING YUKON DEVICE
			} else if (starsInv.getTwoWayLcrSetupInfoDto() != null && !starsInv.getTwoWayLcrSetupInfoDto().isNewDevice()) {
				
				checkSerialNumberMatchesAddress(starsInv);
				assignExistingDeviceToLcr(liteInv, energyCompany, starsInv.getTwoWayLcrSetupInfoDto().getDeviceId());
			
			} else if (starsInv.getTwoWayLcrSetupInfoDto() == null) {
				
				// pass, add/update action has been called but the yukon device is not being set in this case.
	            
			} else {
				throw new WebClientException("Unable to assign Two Way LCR Yukon device - invalid update object.");
			}
			
		} catch (Exception e) {
			throw new StarsTwoWayLcrYukonDeviceCreationException(e.getMessage());
		}
	}
	
	// ASSIGN NEW DEVICE
	@Override
	public void assignNewDeviceToLcr(LiteInventoryBase liteInv,
			LiteStarsEnergyCompany energyCompany, 
			int yukonDeviceTypeId, 
			String deviceName,
			Integer demandRateSeconds,
			boolean allowCreateIfAlreadyHasAssignedDevice)
			throws StarsTwoWayLcrYukonDeviceCreationException {

		if (!DeviceTypesFuncs.isTwoWayLcr(yukonDeviceTypeId)) {
			throw new StarsTwoWayLcrYukonDeviceCreationException("Yukon device must be a Two Way LCR type.");
		}
		
		StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
		
		// double check we are only doing this to a Two Way LCR
		if (InventoryUtils.isTwoWayLcr(inventory.getDeviceType().getEntryID())) {
			
			// if the LCR already has a device assigned to it, skip
        	if (inventory.getDeviceID() < 1 || allowCreateIfAlreadyHasAssignedDevice) {
        		
        		int routeId = inventory.getLMHardware().getRouteID();
        		String serial = inventory.getLMHardware().getManufacturerSerialNumber();
        		if (deviceName == null) {
        			deviceName = generateUniqueTwoWayLcrYukonDeviceName("YK TWLCR " + serial);
        		}
        		if(demandRateSeconds == null) {
        			demandRateSeconds = 300;
        		}
        		
        		YukonDevice yukonDevice = null;
        		try {
        			yukonDevice = deviceCreationService.createDeviceByDeviceType(yukonDeviceTypeId, deviceName, Integer.parseInt(serial), routeId, true);
        			
        			// set demand rate on new device
    	    		LiteYukonPAObject paoDevice = paoDao.getLiteYukonPAO(yukonDevice.getDeviceId());
    	            YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(paoDevice);
    	            DeviceLoadProfile deviceLoadProfile = ((TwoWayLCR)yukonPaobject).getDeviceLoadProfile();
    	            deviceLoadProfile.setLastIntervalDemandRate(demandRateSeconds);
    	            dbPersistentDao.performDBChange(yukonPaobject, DBChangeMsg.CHANGE_TYPE_UPDATE);
    	            
        		} catch (Exception e) {
        			throw new StarsTwoWayLcrYukonDeviceCreationException("Unable to create new yukon device.", e);
        		}
        		
        		// update LM with new device 
    	        updateTwoWayLcrDeviceId(liteInv, yukonDevice.getDeviceId());
        	}
		}
	}
	
	// ASSIGN EXISTING DEVICE
	/**
	 * Assigns an existing Yukon device to the Two Way LCR
	 * @param liteInv inventory representing the Two Way LCR
	 * @param energyCompany
	 * @param deviceId the devieId of the existing Yukon device
	 * @throws StarsTwoWayLcrYukonDeviceAssignmentException
	 */
	private void assignExistingDeviceToLcr(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, int deviceId) throws StarsTwoWayLcrYukonDeviceAssignmentException {
		
		// double check we are only doing this to a Two Way LCR
		StarsInventory inventory = StarsLiteFactory.createStarsInventory(liteInv, energyCompany);
		if (InventoryUtils.isTwoWayLcr(inventory.getDeviceType().getEntryID())) {
			updateTwoWayLcrDeviceId(liteInv, deviceId);
		}
	}
	
	// HELPERS
	private void checkSerialNumberMatchesAddress(StarsInv starsInv) throws StarsTwoWayLcrYukonDeviceCreationException {
		
		Integer yukonDeviceId = starsInv.getTwoWayLcrSetupInfoDto().getDeviceId();
		if (yukonDeviceId != null) {
			LiteYukonPAObject pao = paoDao.getLiteYukonPAO(yukonDeviceId);
			
			int serial = Integer.valueOf(starsInv.getLMHardware().getManufacturerSerialNumber());
			int address = pao.getAddress();
			
			if (address != serial) {
				throw new StarsTwoWayLcrYukonDeviceCreationException("Yukon device serial must match that of the Two Way LCR.");
			}
		}
	}

	private void updateTwoWayLcrDeviceId(LiteInventoryBase liteInv, int deviceId) {
		
		try {
			com.cannontech.database.db.stars.hardware.InventoryBase inventoryBase = new com.cannontech.database.db.stars.hardware.InventoryBase();
	        inventoryBase.setInventoryID(liteInv.getInventoryID());
	        inventoryBase = (com.cannontech.database.db.stars.hardware.InventoryBase)Transaction.createTransaction(Transaction.RETRIEVE, inventoryBase).execute();
	        inventoryBase.setDeviceID(deviceId);
	        dbPersistentDao.performDBChange(inventoryBase, DBChangeMsg.CHANGE_TYPE_UPDATE);
		} catch (Exception e) {
			throw new StarsTwoWayLcrYukonDeviceCreationException("Unable to assign Yukon device to hardware.", e);
		}
	}
		
	private String generateUniqueTwoWayLcrYukonDeviceName(String baseName) {
		return generateUniqueTwoWayLcrYukonDeviceNameRecurse(baseName, 1);
	}
	
	private String generateUniqueTwoWayLcrYukonDeviceNameRecurse(String baseName, int inc) {
		
		String attemptName = baseName;
		if (inc > 1) {
			attemptName += "-" + Integer.toString(inc);
		}
		
		if (deviceDao.findYukonDeviceObjectByName(attemptName) == null) {
			return attemptName;
		} else {
			return generateUniqueTwoWayLcrYukonDeviceNameRecurse(baseName, ++inc);
		}
	}
	    
	
	@Autowired
	public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
		this.deviceCreationService = deviceCreationService;
	}
	@Autowired
	public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
		this.dbPersistentDao = dbPersistentDao;
	}
	@Autowired
	public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}
