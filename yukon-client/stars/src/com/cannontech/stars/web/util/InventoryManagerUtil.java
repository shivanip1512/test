/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.ExpressCom;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.SA205;
import com.cannontech.stars.xml.serialize.SA305;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.VersaCom;
import com.cannontech.stars.xml.serialize.Voltage;
import com.cannontech.util.ServletUtil;

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
		
		String recvDateStr = req.getParameter("ReceiveDate");
		if (recvDateStr != null && recvDateStr.length() > 0) {
			Date recvDate = ServletUtil.parseDateStringLiberally( recvDateStr, energyCompany.getDefaultTimeZone() );
			if (recvDate == null)
				throw new WebClientException("Invalid receive date format '" + recvDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setReceiveDate( recvDate );
		}
		
		String instDateStr = req.getParameter("InstallDate");
		if (instDateStr != null && instDateStr.length() > 0) {
			Date instDate = ServletUtil.parseDateStringLiberally( instDateStr, energyCompany.getDefaultTimeZone() );
			if (instDate == null)
				throw new WebClientException("Invalid install date format '" + instDateStr + "', the date should be in the form of 'mm/dd/yy'");
			starsInv.setInstallDate( instDate );
		}
		
		String remvDateStr = req.getParameter("RemoveDate");
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
		
		if (req.getParameter("DeviceType") != null) {
			DeviceType devType = new DeviceType();
			devType.setEntryID( Integer.parseInt(req.getParameter("DeviceType")) );
			starsInv.setDeviceType( devType );
		}
		
		if (starsInv.getDeviceType() != null) {
			int categoryID = ECUtils.getInventoryCategoryID( starsInv.getDeviceType().getEntryID(), energyCompany );
			if (ECUtils.isLMHardware( categoryID )) {
				LMHardware hw = new LMHardware();
				hw.setManufacturerSerialNumber( req.getParameter("SerialNo") );
				if (req.getParameter("Route") != null)
					hw.setRouteID( Integer.parseInt(req.getParameter("Route")) );
				starsInv.setLMHardware( hw );
			}
		}
	}
	
	/**
	 * Search for devices with specified category and device name
	 */
	public static ArrayList searchDevice(int categoryID, String deviceName) {
		ArrayList devList = new ArrayList();
		java.util.List allDevices = null;
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			if (ECUtils.isMCT( categoryID ))
				allDevices = cache.getAllMCTs();
			
			if (deviceName == null || deviceName.length() == 0) {
				devList.addAll( allDevices );
			}
			else {
				deviceName = deviceName.toUpperCase();
				for (int i = 0; i < allDevices.size(); i++) {
					LiteYukonPAObject litePao = (LiteYukonPAObject) allDevices.get(i);
					if (litePao.getPaoName().toUpperCase().startsWith( deviceName ))
						devList.add( litePao );
				}
			}
		}
		
		return devList;
	}
	
	public static void sendSwitchCommands(LiteStarsEnergyCompany energyCompany, int[] invIDs) throws WebClientException {
		SwitchCommandQueue queue = energyCompany.getSwitchCommandQueue();
		if (queue == null)
			throw new WebClientException( "Failed to retrieve the batched switch commands" );
		
		SwitchCommandQueue.SwitchCommand[] commands = null;
		if (invIDs == null) {
			commands = queue.getCommands( energyCompany.getLiteID(), true );
		}
		else {
			commands = new SwitchCommandQueue.SwitchCommand[ invIDs.length ];
			for (int i = 0; i < invIDs.length; i++)
				commands[i] = queue.getCommand( invIDs[i], true );
		}
		
		if (commands.length == 0)
			throw new WebClientException( "There is no batched switch command" );
		
		for (int i = 0; i < commands.length; i++) {
			if (commands[i] == null) continue;
			
			try {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory(commands[i].getInventoryID(), true);
				
				if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE ))
					YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, true, commands[i].getInfoString() );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_DISABLE ))
					YukonSwitchCommandAction.sendDisableCommand( energyCompany, liteHw, null );
				else if (commands[i].getCommandType().equalsIgnoreCase( SwitchCommandQueue.SWITCH_COMMAND_ENABLE ))
					YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw, null );
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
						YukonSwitchCommandAction.parseResponse( starsAcctInfo, starsInv );
					}
				}
			}
			catch (WebClientException e) {
				String errorMsg = "Error in command '" + commands[i].toString() + "'" +
						System.getProperty( "line.separator" ) + e.getMessage();
				CTILogger.error( errorMsg, e );
			}
		}
	}
	
	public static void setStarsLMConfiguration(StarsLMConfiguration starsCfg, HttpServletRequest req) throws WebClientException {
		String[] clps = req.getParameterValues( "ColdLoadPickup" );
		String[] tds = req.getParameterValues( "TamperDetect" );
		
		String clp = "";
		if (clps != null) {
			if (clps.length > 0) clp += clps[0];
			for (int i = 1; i < clps.length; i++)
				clp += "," + clps[i];
		}
		starsCfg.setColdLoadPickup( clp );
		
		String td = "";
		if (tds != null) {
			if (tds.length > 0) td += tds[0];
			for (int i = 1; i < tds.length; i++)
				td += "," + tds[i];
		}
		starsCfg.setTamperDetect( td );
		
		if (req.getParameter("SA205_Slot1") != null) {
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
			sa305.setRateFamily( ServletUtils.parseNumber(req.getParameter("SA305_RateFamily"), 0, 7, "Rate Family") );
			sa305.setRateMember( ServletUtils.parseNumber(req.getParameter("SA305_RateMember"), 0, 15, "Rate Member") );
			sa305.setRateHierarchy( ServletUtils.parseNumber(req.getParameter("SA305_RateHierarchy"), 0, 1, "Rate Hierarchy") );
			
			starsCfg.setSA305( sa305 );
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
		else if (req.getParameter("XCOM_SPID") != null) {
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
			
			String[] programs = req.getParameterValues("XCOM_Program");
			String program = "";
			for (int i = 0; i < programs.length; i++) {
				ServletUtils.parseNumber(programs[i], 0, 254, 0, "Program" );
				program += programs[i].trim();
				if (i < programs.length - 1) program += ",";
			}
			xcom.setProgram( program );
			
			String[] splinters = req.getParameterValues("XCOM_Splinter");
			String splinter = "";
			for (int i = 0; i < splinters.length; i++) {
				ServletUtils.parseNumber(splinters[i], 0, 254, 0, "Splinter" );
				splinter += splinters[i].trim();
				if (i < splinters.length - 1) splinter += ",";
			}
			xcom.setSplinter( splinter );
			
			starsCfg.setExpressCom( xcom );
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
}
