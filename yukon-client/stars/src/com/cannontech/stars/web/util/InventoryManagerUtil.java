/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.stars.hardware.InventoryBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsTwoWayLcrYukonDeviceAssignmentService;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.SASimple;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.TwoWayLcrSetupInfoDto;
import com.cannontech.stars.xml.serialize.TwoWayLcrSetupInfoDtoFactory;
import com.cannontech.stars.xml.serialize.VersaCom;
import com.cannontech.stars.xml.serialize.Voltage;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InventoryManagerUtil {

	public static final String STARS_INVENTORY_TEMP = "STARS_INVENTORY_TEMP";
	public static final String STARS_INVENTORY_NO = "STARS_INVENTORY_NO";
	public static final String STARS_INVENTORY_OPERATION = "STARS_INVENTORY_OPERATION";
	
	public static final String INVENTORY_TO_CHECK = "INVENTORY_TO_CHECK";
	public static final String INVENTORY_TO_DELETE = "INVENTORY_TO_DELETE";
	public static final String INVENTORY_SET = "INVENTORY_SET";
	public static final String INVENTORY_SET_DESC = "INVENTORY_SET_DESCRIPTION";
	public static final String SN_RANGE_TO_CONFIG = "SN_RANGE_TO_CONFIG";
	public static final String INVENTORY_SELECTED = "INVENTORY_SELECTED";
	public static final String DEVICE_SELECTED = "DEVICE_SELECTED";
	public static final String HARDWARE_ADDRESSING = "HARDWARE_ADDRESSING";
	
	public static final String[] MCT_TYPES = {
		PAOGroups.STRING_MCT_410IL[0],
		PAOGroups.STRING_MCT_410CL[0],
		PAOGroups.STRING_MCT_430A[0],
        PAOGroups.STRING_MCT_430S4[0],
        PAOGroups.STRING_MCT_430SL[0],
        PAOGroups.STRING_MCT_470[0],
        PAOGroups.STRING_MCT_370[0],
		PAOGroups.STRING_MCT_360[0],
		PAOGroups.STRING_MCT_318L[0],
		PAOGroups.STRING_MCT_318[0],
		PAOGroups.STRING_MCT_310CT[0],
		PAOGroups.STRING_MCT_310ID[0],
		PAOGroups.STRING_MCT_310IDL[0],
		PAOGroups.STRING_MCT_310IL[0],
		PAOGroups.STRING_MCT_310IM[0],
		PAOGroups.STRING_MCT_310[0],
		PAOGroups.STRING_MCT_250[0],
		PAOGroups.STRING_MCT_248[0],
		PAOGroups.STRING_MCT_240[0],
		PAOGroups.STRING_MCT_213[0],
		PAOGroups.STRING_MCT_210[0],
	};
	
	private static Map<Integer,Object[]> batchCfgSubmission = null;
	
	/**
	 * Store hardware information entered by user into a StarsLMHw object 
	 */
	public static void setStarsInv(StarsInv starsInv, HttpServletRequest req, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		if (req.getParameter("InvID") != null)
			starsInv.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
		else
			starsInv.setInventoryID( -1 );
		
		if (req.getParameter("DeviceID") != null)
			starsInv.setDeviceID( Integer.parseInt(req.getParameter("DeviceID")) );
		if (req.getParameter("DeviceLabel") != null)
			starsInv.setDeviceLabel( req.getParameter("DeviceLabel") );
		if (req.getParameter("AltTrackNo") != null)
			starsInv.setAltTrackingNumber( req.getParameter("AltTrackNo") );
		if (req.getParameter("Notes") != null)
			starsInv.setNotes( req.getParameter("Notes").replaceAll(System.getProperty("line.separator"), "<br>") );
		if (req.getParameter("InstallNotes") != null)
			starsInv.setInstallationNotes( req.getParameter("InstallNotes").replaceAll(System.getProperty("line.separator"), "<br>") );
		
		String instDateStr = req.getParameter("fieldInstallDate");
		if (instDateStr != null && instDateStr.length() > 0) {
			Date instDate = ServletUtil.parseDateStringLiberally( instDateStr, energyCompany.getDefaultTimeZone() );
			if (instDate == null)
				throw new WebClientException("Invalid install date format '" + instDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setInstallDate( instDate );
		}
		
        String recvDateStr = req.getParameter("fieldReceiveDate");
        if (recvDateStr != null && recvDateStr.length() > 0) {
            Date recvDate = ServletUtil.parseDateStringLiberally( recvDateStr, energyCompany.getDefaultTimeZone() );
            if (recvDate == null)
                throw new WebClientException("Invalid receive date format '" + recvDateStr + "', the date should be in the form of 'mm/dd/yy'");
            starsInv.setReceiveDate( recvDate );
        }
        
		String remvDateStr = req.getParameter("fieldRemoveDate");
		if (remvDateStr != null && remvDateStr.length() > 0) {
			Date remvDate = ServletUtil.parseDateStringLiberally( remvDateStr, energyCompany.getDefaultTimeZone() );
			if (remvDate == null)
				throw new WebClientException("Invalid remove date format '" + remvDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setRemoveDate( remvDate );
		}
        
		if (req.getParameter("Voltage") != null) {
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(req.getParameter("Voltage")) );
			starsInv.setVoltage( volt );
		}
		
		if (req.getParameter("ServiceCompany") != null) {
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			starsInv.setInstallationCompany( company );
		}
		
		DeviceType devType = null;
		if (req.getParameter("DeviceType") != null) {
			devType = new DeviceType();
			devType.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
			starsInv.setDeviceType( devType );
		}
		
        if (req.getParameter("Status") != null) {
            DeviceStatus devStat = new DeviceStatus();
            devStat.setEntryID( Integer.parseInt(req.getParameter("Status")) );
            starsInv.setDeviceStatus( devStat );
        
            /*
             * This violates so many standards of good coding I hate to even think about it
             * but it beat trying to put it in nine different spots in the Yao action/not-action use maze.
             */
            HttpSession session = req.getSession(false);
            StarsYukonUser user = (StarsYukonUser)
                    session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            
            if(req.getParameter("oldStateID") != null && Integer.parseInt(req.getParameter("oldStateID")) != devStat.getEntryID())
                EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, devStat.getEntryID(), starsInv.getInventoryID(), session);
        }
        
        /*
         * TODO This should be moved elsewhere
         */
        Integer warehouseID = Integer.valueOf(0);
        if(req.getParameter("Warehouse") != null)
            warehouseID = new Integer(req.getParameter("Warehouse"));
        
        Warehouse.moveInventoryToAnotherWarehouse(starsInv.getInventoryID(), warehouseID.intValue());
        
		if (starsInv.getDeviceType() != null) {
			int categoryID = InventoryUtils.getInventoryCategoryID( starsInv.getDeviceType().getEntryID(), energyCompany );
			if (InventoryUtils.isLMHardware( categoryID )) {
				LMHardware hw = new LMHardware();
				hw.setManufacturerSerialNumber( req.getParameter("SerialNo") );
				if (req.getParameter("Route") != null)
					hw.setRouteID( Integer.parseInt(req.getParameter("Route")) );
				starsInv.setLMHardware( hw );
			}
			else if (InventoryUtils.isMCT(categoryID) && starsInv.getDeviceID() == 0) {
				MCT mct = new MCT();
				mct.setDeviceName( req.getParameter("DeviceName") );
				if (req.getParameter("MCTType") != null)
					mct.setMctType( Integer.parseInt(req.getParameter("MCTType")) );
				
				try {
					if (req.getParameter("PhysicalAddr") != null)
						mct.setPhysicalAddress( Integer.parseInt(req.getParameter("PhysicalAddr")) );
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format in the \"Physical Address\" field.");
				}
				
				if (req.getParameter("MeterNumber") != null)
					mct.setMeterNumber( req.getParameter("MeterNumber") );
				if (req.getParameter("MCTRoute") != null)
					mct.setRouteID( Integer.parseInt(req.getParameter("MCTRoute")) );
				starsInv.setMCT( mct );
			}
		}
		
		// TWO WAY LCR
		// - pull info from request and check for initial problems
		// - create a TwoWayLcrSetupInfo object and set it on the starsInv
        if (devType != null) {
        	
        	PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class); 
        	
        	if (InventoryUtils.isTwoWayLcr(devType.getEntryID())) {
            	
        		try {
        			Integer.parseInt(req.getParameter("SerialNo"));
        		} catch (NumberFormatException e) {
        			throw new WebClientException("Two Way LCR serial number must be numeric.");
        		}
        		
        		// NEW YUKON DEVICE
            	if (req.getParameter("yukonDeviceCreationStyleRadio").equals("NEW")) {
                	
            		// create new YukonDevice
    	    		String yukonDeviceName = req.getParameter("yukonDeviceName");
    	    		if (StringUtils.isBlank(yukonDeviceName)) {
    	    			throw new WebClientException("Yukon device name is required.");
    	    		}
    	    		
    	    		
    	    		List<LiteYukonPAObject> existingDevicesWithName = paoDao.getLiteYukonPaoByName(yukonDeviceName, false);
    	    		if (existingDevicesWithName.size() > 0) {
    	    			throw new WebClientException("Yukon device " + yukonDeviceName + " already exists.");
    	    		}
    	    		
    	    		YukonListDao yukonListDao = YukonSpringHook.getBean("yukonListDao", YukonListDao.class); 
    	    		YukonListEntry entry = yukonListDao.getYukonListEntry(devType.getEntryID());
    	    		String deviceTypeName = entry.getEntryText();
    	    		int yukonDeviceTypeId = PAOGroups.getDeviceType(deviceTypeName);
    	    		if (!DeviceTypesFuncs.isTwoWayLcr(yukonDeviceTypeId)) {
    	    			throw new WebClientException("Selected yukon device must be a Two Way LCR type.");
    	    		}
    	    		
    	    		TwoWayLcrSetupInfoDto twoWayLCRSetupInfo = TwoWayLcrSetupInfoDtoFactory.getDeviceSetupInfoDtoForNewDevice(yukonDeviceTypeId, yukonDeviceName, Integer.parseInt(req.getParameter("yukonDeviceDemandRate")));
    	    		starsInv.setTwoWayLcrSetupInfoDto(twoWayLCRSetupInfo);

    	    	// EXISTING YUKON DEVICE
        		} else if (req.getParameter("yukonDeviceCreationStyleRadio").equals("EXISTING")) {
	    		
        			int deviceId = Integer.parseInt(req.getParameter("choosenYukonDeviceId"));
    	    		
        			InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean("inventoryBaseDao", InventoryBaseDao.class); 
    	    		List<com.cannontech.stars.dr.hardware.model.InventoryBase> matchedInventory = inventoryBaseDao.getByDeviceId(deviceId);
    	    		if (matchedInventory.size() != 0 && matchedInventory.get(0).getDeviceId() != starsInv.getDeviceID()) {
    	    			throw new WebClientException("Yukon device is already setup for another LCR-3102");
    	    		}
    	    		
    	    		
    	    		LiteYukonPAObject liteYukonPAO;
    	    		try {
    	    			liteYukonPAO = paoDao.getLiteYukonPAO(deviceId);
    	    		} catch (NotFoundException e) {
    	    			throw new WebClientException("The selected Yukon device no longer exists.");
    	    		}
    	    		
    	    		int yukonDeviceTypeId = liteYukonPAO.getType();
    	    		if (!DeviceTypesFuncs.isTwoWayLcr(yukonDeviceTypeId)) {
    	    			throw new WebClientException("Selected yukon device must be a Two Way LCR type.");
    	    		}
    	    		
    	    		TwoWayLcrSetupInfoDto twoWayLCRSetupInfo = TwoWayLcrSetupInfoDtoFactory.getDeviceSetupInfoDtoForExistingDevice(yukonDeviceTypeId, deviceId);
    	    		starsInv.setTwoWayLcrSetupInfoDto(twoWayLCRSetupInfo);
    	    		
        		} else {
        			throw new WebClientException("Must create a new Yukon device, or choose fom an existing Yukon device when creating an Two Way LCR.");
        		}
            }
        }
	}
	
	/**
	 * Used to do the work of either creating a new Yukon device and assigning to the Two Way LCR
	 * or to assigning an existing Yukon device to the LCR.
	 * @param starsInv
	 * @param liteInv
	 * @param energyCompany
	 * @throws WebClientException
	 */
	public static void assignTwoWayLcrDevice(StarsInv starsInv, LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws WebClientException {
		
		try {
			
			StarsTwoWayLcrYukonDeviceAssignmentService starsTwoWayLcrYukonDeviceAssignmentService = YukonSpringHook.getBean("starsTwoWayLcrYukonDeviceAssignmentService", StarsTwoWayLcrYukonDeviceAssignmentService.class); 
			
			// NEW YUKON DEVICE
			if (starsInv.getTwoWayLcrSetupInfoDto() != null && starsInv.getTwoWayLcrSetupInfoDto().isNewDevice()) {
				
				starsTwoWayLcrYukonDeviceAssignmentService.assignNewDeviceToLcr(liteInv, energyCompany, starsInv.getTwoWayLcrSetupInfoDto().getYukonDeviceTypeId(), starsInv.getTwoWayLcrSetupInfoDto().getDeviceName(), starsInv.getTwoWayLcrSetupInfoDto().getDemandRate(), true);
			
	        // EXISTING YUKON DEVICE
			} else if (starsInv.getTwoWayLcrSetupInfoDto() != null && !starsInv.getTwoWayLcrSetupInfoDto().isNewDevice()) {
				
				starsTwoWayLcrYukonDeviceAssignmentService.assignExistingDeviceToLcr(liteInv, energyCompany, starsInv.getTwoWayLcrSetupInfoDto().getDeviceId());
			
			} else if (starsInv.getTwoWayLcrSetupInfoDto() == null) {
				
				// pass, add/update action has been called but the yukon device is not being set in this case.
	            
			} else {
				throw new WebClientException("Unable to assign Two Way LCR Yukon device - invalid update object.");
			}
			
		} catch (Exception e) {
			throw new WebClientException(e.getMessage());
		}
	}
	
	/**
	 * Search for devices with specified category and device name
	 */
	public static List<LiteYukonPAObject> searchDevice(int categoryID, String deviceName) {
        List<LiteYukonPAObject> devList = new ArrayList<LiteYukonPAObject>();
        List<LiteYukonPAObject> allDevices = null;
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			if (InventoryUtils.isMCT( categoryID ))
				allDevices = cache.getAllMCTs();
			
			if (deviceName == null || deviceName.length() == 0) {
				devList.addAll( allDevices );
			}
			else {
				deviceName = deviceName.toUpperCase();
				for (int i = 0; i < allDevices.size(); i++) {
					LiteYukonPAObject litePao = allDevices.get(i);
					if (litePao.getPaoName().toUpperCase().startsWith( deviceName ))
						devList.add( litePao );
				}
			}
		}
		
		return devList;
	}
	
	public static void sendSwitchCommand(SwitchCommandQueue.SwitchCommand cmd) throws WebClientException 
    {
		StarsInventoryBaseDao starsInventoryBaseDao = 
			YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class); 

		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( cmd.getEnergyCompanyID() );
		LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getById(cmd.getInventoryID());
        boolean writeToFile = false;
        String batchProcessType = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.BATCHED_SWITCH_COMMAND_TOGGLE );
        if(batchProcessType != null)
        {
            writeToFile = batchProcessType.compareTo(StarsUtils.BATCH_SWITCH_COMMAND_MANUAL) == 0 
                && VersionTools.staticLoadGroupMappingExists();
        }
        
		if (cmd.getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE ))
        {
			if(writeToFile)
                YukonSwitchCommandAction.fileWriteConfigCommand( energyCompany, liteHw, true, cmd.getInfoString() );
            else    
                YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true, cmd.getInfoString() );
        }
        else if (cmd.getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_DISABLE ))
        {
            if(writeToFile)
                YukonSwitchCommandAction.fileWriteDisableCommand( energyCompany, liteHw, null );
            else
                YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, null );
        }
		else if (cmd.getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_ENABLE ))
        {
            if(writeToFile)
                YukonSwitchCommandAction.fileWriteEnableCommand( energyCompany, liteHw, null );
            else
                YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw, null );
        }
		
		SwitchCommandQueue.getInstance().removeCommand( cmd.getInventoryID() );
		
		if (liteHw.getAccountID() > 0) 
        {
			StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
			if (starsAcctInfo != null) {
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
				YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
			}
		}
	}
    
	public static void setStarsLMConfiguration(StarsLMConfiguration starsCfg, HttpServletRequest req) throws WebClientException {
		String[] clps = req.getParameterValues( "ColdLoadPickup" );
		String[] tds = req.getParameterValues( "TamperDetect" );
		
		if (clps != null && clps.length > 0) {
			String clp = clps[0];
			for (int i = 1; i < clps.length; i++)
				clp += "," + clps[i];
			starsCfg.setColdLoadPickup( clp );
		}
		
		if (tds != null && tds.length > 0) {
			String td = tds[0];
			for (int i = 1; i < tds.length; i++)
				td += "," + tds[i];
			starsCfg.setTamperDetect( td );
		}
		
		if (req.getParameter("XCOM_SPID") != null) {
			ExpressCom xcom = new ExpressCom();
			
			xcom.setServiceProvider( ServletUtils.parseNumber(req.getParameter("XCOM_SPID"), 0, 65534, "SPID") );
			xcom.setGEO( ServletUtils.parseNumber(req.getParameter("XCOM_GEO"), 0, 65534, 0, "GEO") );
			xcom.setSubstation( ServletUtils.parseNumber(req.getParameter("XCOM_SUB"), 0, 65534, 0, "SUB") );
			xcom.setZip( ServletUtils.parseNumber(req.getParameter("XCOM_ZIP"), 0, 16777214, 0, "ZIP") );
			xcom.setUserAddress( ServletUtils.parseNumber(req.getParameter("XCOM_USER"), 0, 65534, 0, "USER") );
			
			String[] feeders = req.getParameterValues( "XCOM_FEED" );
			int feeder = 0;
			if (feeders != null) {
				for (int i = 0; i < feeders.length; i++)
					feeder += Integer.parseInt( feeders[i] );
			}
			xcom.setFeeder( feeder );
			
			String program = "";
			String[] programs = req.getParameterValues("XCOM_Program");
			if (programs != null) {
			    for (int i = 0; i < programs.length; i++) {
			        ServletUtils.parseNumber(programs[i], 0, 254, 0, "Program" );
			        program += programs[i].trim();
			        if (i < programs.length - 1) program += ",";
			    }
			}
			xcom.setProgram( program );
			
			String splinter = "";
			String[] splinters = req.getParameterValues("XCOM_Splinter");
			if (splinters != null) {
			    for (int i = 0; i < splinters.length; i++) {
			        ServletUtils.parseNumber(splinters[i], 0, 254, 0, "Splinter" );
			        splinter += splinters[i].trim();
			        if (i < splinters.length - 1) splinter += ",";
			    }
			}
			xcom.setSplinter( splinter );
			
			starsCfg.setExpressCom( xcom );
		}
		else if (req.getParameter("VCOM_Utility") != null) {
			VersaCom vcom = new VersaCom();
			
			vcom.setUtility( ServletUtils.parseNumber(req.getParameter("VCOM_Utility"), 0, 255, "Utility") );
			vcom.setSection( ServletUtils.parseNumber(req.getParameter("VCOM_Section"), 1, 254, 0, "Section") );
			
			String[] classAddrs = req.getParameterValues( "VCOM_Class" );
			int classAddr = 0;
			if (classAddrs != null) {
				for (int i = 0; i < classAddrs.length; i++)
					classAddr += Integer.parseInt( classAddrs[i] );
			}
			vcom.setClassAddress( classAddr );
			
			String[] divisions = req.getParameterValues( "VCOM_Division" );
			int division = 0;
			if (divisions != null) {
				for (int i = 0; i < divisions.length; i++)
					division += Integer.parseInt( divisions[i] );
			}
			vcom.setDivision( division );
			
			starsCfg.setVersaCom( vcom );
		}
		else if (req.getParameter("SA205_Slot1") != null) {
			SA205 sa205 = new SA205();
			
			sa205.setSlot1( ServletUtils.parseNumber(req.getParameter("SA205_Slot1"), 0, 4095, 0, "Slot Address") );
			sa205.setSlot2( ServletUtils.parseNumber(req.getParameter("SA205_Slot2"), 0, 4095, 0, "Slot Address") );
			sa205.setSlot3( ServletUtils.parseNumber(req.getParameter("SA205_Slot3"), 0, 4095, 0, "Slot Address") );
			sa205.setSlot4( ServletUtils.parseNumber(req.getParameter("SA205_Slot4"), 0, 4095, 0, "Slot Address") );
			sa205.setSlot5( ServletUtils.parseNumber(req.getParameter("SA205_Slot5"), 0, 4095, 0, "Slot Address") );
			sa205.setSlot6( ServletUtils.parseNumber(req.getParameter("SA205_Slot6"), 0, 4095, 0, "Slot Address") );
			
			starsCfg.setSA205( sa205 );
		}
		else if (req.getParameter("SA305_Utility") != null) {
			SA305 sa305 = new SA305();
			
			sa305.setUtility( ServletUtils.parseNumber(req.getParameter("SA305_Utility"), 0, 15, "Utility") );
			sa305.setGroup( ServletUtils.parseNumber(req.getParameter("SA305_Group"), 0, 63, 0, "Group") );
			sa305.setDivision( ServletUtils.parseNumber(req.getParameter("SA305_Division"), 0, 63, 0, "Division") );
			sa305.setSubstation( ServletUtils.parseNumber(req.getParameter("SA305_Substation"), 0, 1023, 0, "Substation") );
			
			int rateFam = new Integer(req.getParameter("SA305_RateRate")).intValue() / 16;
			int rateMem = new Integer(req.getParameter("SA305_RateRate")).intValue() % 16;
			sa305.setRateFamily( ServletUtils.parseNumber(new Integer(rateFam).toString(), 0, 7, "Rate Family") );
			sa305.setRateMember( ServletUtils.parseNumber(new Integer(rateMem).toString(), 0, 15, "Rate Member") );
			//Rate Hierarchy should not be on the config page; it is part of the control command only
			sa305.setRateHierarchy(0);
			//sa305.setRateHierarchy( ServletUtils.parseNumber(req.getParameter("SA305_RateHierarchy"), 0, 1, "Rate Hierarchy") );
			
			starsCfg.setSA305( sa305 );
		}
		else if (req.getParameter("Simple_Address") != null) {
			SASimple simple = new SASimple();
			simple.setOperationalAddress( req.getParameter("Simple_Address") );
			starsCfg.setSASimple( simple );
		}
	}
	
	public static String getSNRange(Integer snFrom, Integer snTo) {
		if (snFrom == null && snTo == null)
			return null;
		else if (snFrom == null)
			return snTo.toString() + " and below";
		else if (snTo == null)
			return snFrom.toString() + " and above";
		else
			return snFrom.toString() + " to " + snTo.toString();
	}
	
	public static void deleteInventory(LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, boolean deleteFromYukon) throws Exception
	{
		InventoryBase inventory = (InventoryBase)StarsLiteFactory.createDBPersistent(liteInv);
		
		Transaction.createTransaction( Transaction.DELETE, inventory ).execute();
		
		if (liteInv.getDeviceID() > 0 && deleteFromYukon) {
			
			DBDeleteResult delRes = new DBDeleteResult( liteInv.getDeviceID(), DBDeletionDao.DEVICE_TYPE );
			byte status = DaoFactory.getDbDeletionDao().deletionAttempted( delRes );

			if (status == DBDeletionDao.STATUS_DISALLOW)
				throw new WebClientException( delRes.getDescriptionMsg().toString() );
			
			LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO( liteInv.getDeviceID() );
			DBPersistent dbPer = LiteFactory.convertLiteToDBPers( litePao );
			Transaction.createTransaction( Transaction.DELETE, dbPer ).execute();
			
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages(
					(CTIDbChange)dbPer, DBChangeMsg.CHANGE_TYPE_DELETE );
			for (int i = 0; i < dbChange.length; i++)
				ServerUtils.handleDBChangeMsg( dbChange[i] );
		}
	}
	
	/**
	 * Get all the hardware/devices for the given accounts in the inventory
	 * @param accounts List of LiteStarsCustAccountInformation, or Pair(LiteStarsCustAccountInformation, LiteStarsEnergyCompany)
	 * @return List of LiteInventoryBase or Pair(LiteInventoryBase, LiteStarsEnergyCompany), based on the element type of accounts
	 */
	private static List<LiteInventoryBase> getInventoryByAccounts(List<Object> accounts, LiteStarsEnergyCompany energyCompany) {
		List<LiteInventoryBase> invList = new ArrayList<LiteInventoryBase>();
		
		StarsInventoryBaseDao starsInventoryBaseDao = 
			YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
		
		for (int i = 0; i < accounts.size(); i++) {
			if (accounts.get(i) instanceof Pair) {
				@SuppressWarnings("unchecked") LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) ((Pair)accounts.get(i)).getFirst();
				
				for (int j = 0; j < liteAcctInfo.getInventories().size(); j++) {
					int invID = liteAcctInfo.getInventories().get(j).intValue();
					LiteInventoryBase liteInv = starsInventoryBaseDao.getById(invID);
					invList.add(liteInv);
				}
			}
			else {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(i);
				for (int j = 0; j < liteAcctInfo.getInventories().size(); j++) {
					int invID = liteAcctInfo.getInventories().get(j).intValue();
					LiteInventoryBase liteInv = starsInventoryBaseDao.getById(invID);
					invList.add(liteInv);
				}
			}
		}
		
		return invList;
	}
	
	/**
	 * Search the inventory for hardware/devices by the given search type and value.
	 * If searchMembers is true, returns a list of Pair(LiteInventoryBase, LiteStarsEnergyCompany);
	 * otherwise, returns a list of LiteInventoryBase
	 */
	public static List<LiteInventoryBase> searchInventory(LiteStarsEnergyCompany energyCompany, int searchBy, String searchValue, boolean searchMembers)
		throws WebClientException
	{
		
		StarsSearchDao starsSearchDao = YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
		List<LiteStarsEnergyCompany> ecList = new ArrayList<LiteStarsEnergyCompany>();
		ecList.add(energyCompany);
		if(searchMembers) {
			List<LiteStarsEnergyCompany> allChildren = ECUtils.getAllDescendants(energyCompany);
			ecList.addAll(allChildren);
		}
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO) {
			List<LiteInventoryBase> hardwareList = 
				starsSearchDao.searchLMHardwareBySerialNumber(searchValue, ecList);
			return hardwareList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME) {
			List<LiteInventoryBase> inventoryList = 
				starsSearchDao.searchInventoryByDeviceName(searchValue, ecList);
			return inventoryList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ALT_TRACK_NO) {
			List<LiteInventoryBase> inventoryList = 
				starsSearchDao.searchInventoryByAltTrackNumber(searchValue, ecList);
			return inventoryList;
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ACCT_NO) {
			List<Object> accounts = energyCompany.searchAccountByAccountNumber( searchValue, searchMembers, true);
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_PHONE_NO) {
			String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
			List<Object> accounts = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_LAST_NAME) {
            List<Object> accounts = energyCompany.searchAccountByLastName( searchValue, searchMembers, true );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ORDER_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
            List<Object> accounts = energyCompany.searchAccountByOrderNo( searchValue, searchMembers );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_ADDRESS) {
            List<Object> accounts = energyCompany.searchAccountByAddress( searchValue, searchMembers, true );
			return getInventoryByAccounts( accounts, energyCompany );
		}
		else {
			throw new WebClientException("Unrecognized search type");
		}
	}
	
	public synchronized static Map<Integer,Object[]> getBatchConfigSubmission() {
		if (batchCfgSubmission == null) {
			batchCfgSubmission = new Hashtable<Integer,Object[]>();
			
			String sql = "SELECT TimeStamp, EnergyCompanyID, Description FROM ActivityLog " +
				"WHERE TimeStamp > ? AND Action = '" + ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION + "'";
			
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rset = null;
            
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				stmt = conn.prepareStatement( sql );
				stmt.setDate( 1, new java.sql.Date(ServletUtil.getToday().getTime() - 600 * 1000) );	// set the time to be a bit earlier than midnight today
				rset = stmt.executeQuery();
				
				while (rset.next()) {
					long timeStamp = rset.getTimestamp(1).getTime();
					int energyCompanyID = rset.getInt(2);
					String description = rset.getString(3);
					batchCfgSubmission.put( new Integer(energyCompanyID), new Object[]{new Date(timeStamp), description} );
				}
			}
			catch (java.sql.SQLException e) {
				CTILogger.error( e.getMessage(), e );
			}
			finally {
				SqlUtils.close(rset, stmt, conn);
			}
		}
		
		return batchCfgSubmission;
	}
}
