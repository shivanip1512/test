package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsAppAirConditioner;
import com.cannontech.database.data.lite.stars.LiteStarsAppDualFuel;
import com.cannontech.database.data.lite.stars.LiteStarsAppGenerator;
import com.cannontech.database.data.lite.stars.LiteStarsAppGrainDryer;
import com.cannontech.database.data.lite.stars.LiteStarsAppHeatPump;
import com.cannontech.database.data.lite.stars.LiteStarsAppIrrigation;
import com.cannontech.database.data.lite.stars.LiteStarsAppStorageHeat;
import com.cannontech.database.data.lite.stars.LiteStarsAppWaterHeater;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceDualFuel;
import com.cannontech.database.db.stars.appliance.ApplianceGenerator;
import com.cannontech.database.db.stars.appliance.ApplianceGrainDryer;
import com.cannontech.database.db.stars.appliance.ApplianceHeatPump;
import com.cannontech.database.db.stars.appliance.ApplianceIrrigation;
import com.cannontech.database.db.stars.appliance.ApplianceStorageHeat;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ACType;
import com.cannontech.stars.xml.serialize.AirConditioner;
import com.cannontech.stars.xml.serialize.BinSize;
import com.cannontech.stars.xml.serialize.BlowerEnergySource;
import com.cannontech.stars.xml.serialize.BlowerHeatSource;
import com.cannontech.stars.xml.serialize.BlowerHorsePower;
import com.cannontech.stars.xml.serialize.DryerType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.EnergySource;
import com.cannontech.stars.xml.serialize.Generator;
import com.cannontech.stars.xml.serialize.GrainDryer;
import com.cannontech.stars.xml.serialize.HeatPump;
import com.cannontech.stars.xml.serialize.HorsePower;
import com.cannontech.stars.xml.serialize.Irrigation;
import com.cannontech.stars.xml.serialize.IrrigationType;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.MeterLocation;
import com.cannontech.stars.xml.serialize.MeterVoltage;
import com.cannontech.stars.xml.serialize.NumberOfGallons;
import com.cannontech.stars.xml.serialize.PumpSize;
import com.cannontech.stars.xml.serialize.PumpType;
import com.cannontech.stars.xml.serialize.SecondaryEnergySource;
import com.cannontech.stars.xml.serialize.SoilType;
import com.cannontech.stars.xml.serialize.StandbySource;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateAppliance;
import com.cannontech.stars.xml.serialize.StorageHeat;
import com.cannontech.stars.xml.serialize.StorageType;
import com.cannontech.stars.xml.serialize.SwitchOverType;
import com.cannontech.stars.xml.serialize.Tonnage;
import com.cannontech.stars.xml.serialize.TransferSwitchManufacturer;
import com.cannontech.stars.xml.serialize.TransferSwitchType;
import com.cannontech.stars.xml.serialize.WaterHeater;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			int appID = Integer.parseInt(req.getParameter("AppID"));
			StarsAppliance appliance = null;
			
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				if (appliances.getStarsAppliance(i).getApplianceID() == appID) {
					appliance = appliances.getStarsAppliance(i);
					break;
				}
			}
			
			if (appliance == null) return null;
			
			StarsUpdateAppliance updateApp = (StarsUpdateAppliance)
					StarsFactory.newStarsApp( appliance, StarsUpdateAppliance.class );
			
			updateApp.setApplianceID( appID );
			updateApp.setNotes( req.getParameter("Notes").replaceAll(System.getProperty("line.separator"), "<br>") );
			updateApp.setModelNumber( req.getParameter("ModelNo") );
			
			try {
				if (req.getParameter("ManuYear").length() > 0)
					updateApp.setYearManufactured( Integer.parseInt(req.getParameter("ManuYear")) );
				else
					updateApp.deleteYearManufactured();
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("ManuYear") + "' for year manufactured");
			}
			try {
				if (req.getParameter("KWCapacity").length() > 0)
					updateApp.setKWCapacity( Integer.parseInt(req.getParameter("KWCapacity")) );
				else
					updateApp.deleteKWCapacity();
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("KWCapacity") + "' for KW capacity");
			}
			try {
				if (req.getParameter("EffRating").length() > 0)
					updateApp.setEfficiencyRating( Integer.parseInt(req.getParameter("EffRating")) );
				else
					updateApp.deleteEfficiencyRating();
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("EffRating") + "' for efficiency rating");
			}
			
			updateApp.setManufacturer( (Manufacturer)StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, Integer.parseInt(req.getParameter("Manufacturer"))),
					Manufacturer.class) );
			
			updateApp.setLocation( (Location)StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION, Integer.parseInt(req.getParameter("Location"))),
					Location.class) );
			
			if (updateApp.getAirConditioner() != null) {
				AirConditioner ac = updateApp.getAirConditioner();
				ac.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE, Integer.parseInt(req.getParameter("AC_Tonnage"))),
						Tonnage.class) );
				ac.setACType( (ACType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE, Integer.parseInt(req.getParameter("AC_Type"))),
						ACType.class) );
			}
			else if (updateApp.getWaterHeater() != null) {
				WaterHeater wh = updateApp.getWaterHeater();
				wh.setNumberOfGallons( (NumberOfGallons)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_WH_NUM_OF_GALLONS, Integer.parseInt(req.getParameter("WH_GallonNum"))),
						NumberOfGallons.class) );
				wh.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_WH_ENERGY_SOURCE, Integer.parseInt(req.getParameter("WH_EnergySrc"))),
						EnergySource.class) );
				
				try {
					if (req.getParameter("WH_ElementNum").length() > 0)
						wh.setNumberOfElements( Integer.parseInt(req.getParameter("WH_ElementNum")) );
					else
						wh.deleteNumberOfElements();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("WH_ElementNum") + "' for # heating coils");
				}
			}
			else if (updateApp.getDualFuel() != null) {
				DualFuel df = updateApp.getDualFuel();
				df.setSwitchOverType( (SwitchOverType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DF_SWITCH_OVER_TYPE, Integer.parseInt(req.getParameter("DF_SwitchOverType"))),
						SwitchOverType.class) );
				df.setSecondaryEnergySource( (SecondaryEnergySource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DF_SECONDARY_SOURCE, Integer.parseInt(req.getParameter("DF_SecondarySrc"))),
						SecondaryEnergySource.class) );
				
				try {
					if (req.getParameter("DF_KWCapacity2").length() > 0)
						df.setSecondaryKWCapacity( Integer.parseInt(req.getParameter("DF_KWCapacity2")) );
					else
						df.deleteSecondaryKWCapacity();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("DF_KWCapacity2") + "' for secondary KW capacity");
				}
			}
			else if (updateApp.getGenerator() != null) {
				Generator gen = updateApp.getGenerator();
				gen.setTransferSwitchType( (TransferSwitchType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_TYPE, Integer.parseInt(req.getParameter("GEN_TranSwitchType"))),
						TransferSwitchType.class) );
				gen.setTransferSwitchManufacturer( (TransferSwitchManufacturer)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GEN_TRANSFER_SWITCH_MFG, Integer.parseInt(req.getParameter("GEN_TranSwitchMfg"))),
						TransferSwitchManufacturer.class) );
				
				try {
					if (req.getParameter("GEN_KWCapacity").length() > 0)
						gen.setPeakKWCapacity( Integer.parseInt(req.getParameter("GEN_KWCapacity")) );
					else
						gen.deletePeakKWCapacity();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_KWCapacity") + "' for peak KW capacity");
				}
				try {
					if (req.getParameter("GEN_FuelCapGal").length() > 0)
						gen.setFuelCapGallons( Integer.parseInt(req.getParameter("GEN_FuelCapGal")) );
					else
						gen.deleteFuelCapGallons();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_FuelCapGal") + "' for fuel capacity");
				}
				try {
					if (req.getParameter("GEN_StartDelaySec").length() > 0)
						gen.setStartDelaySeconds( Integer.parseInt(req.getParameter("GEN_StartDelaySec")) );
					else
						gen.deleteStartDelaySeconds();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_StartDelaySec") + "' for start delay");
				}
			}
			else if (updateApp.getGrainDryer() != null) {
				GrainDryer gd = updateApp.getGrainDryer();
				gd.setDryerType( (DryerType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GRAIN_DRYER_TYPE, Integer.parseInt(req.getParameter("GD_DryerType"))),
						DryerType.class) );
				gd.setBinSize( (BinSize)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GD_BIN_SIZE, Integer.parseInt(req.getParameter("GD_BinSize"))),
						BinSize.class) );
				gd.setBlowerEnergySource( (BlowerEnergySource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GD_ENERGY_SOURCE, Integer.parseInt(req.getParameter("GD_BlowerEnergySrc"))),
						BlowerEnergySource.class) );
				gd.setBlowerHorsePower( (BlowerHorsePower)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GD_HORSE_POWER, Integer.parseInt(req.getParameter("GD_BlowerHorsePower"))),
						BlowerHorsePower.class) );
				gd.setBlowerHeatSource( (BlowerHeatSource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_GD_HEAT_SOURCE, Integer.parseInt(req.getParameter("GD_BlowerHeatSrc"))),
						BlowerHeatSource.class) );
			}
			else if (updateApp.getStorageHeat() != null) {
				StorageHeat sh = updateApp.getStorageHeat();
				sh.setStorageType( (StorageType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE, Integer.parseInt(req.getParameter("SH_StorageType"))),
						StorageType.class) );
				
				try {
					if (req.getParameter("SH_KWCapacity").length() > 0)
						sh.setPeakKWCapacity( Integer.parseInt(req.getParameter("SH_KWCapacity")) );
					else
						sh.deletePeakKWCapacity();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("SH_KWCapacity") + "' for peak KW capacity");
				}
				try {
					if (req.getParameter("SH_RechargeHour").length() > 0)
						sh.setHoursToRecharge( Integer.parseInt(req.getParameter("SH_RechargeHour")) );
					else
						sh.deleteHoursToRecharge();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("SH_RechargeHour") + "' for recharge time");
				}
			}
			else if (updateApp.getHeatPump() != null) {
				HeatPump hp = updateApp.getHeatPump();
				hp.setPumpType( (PumpType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_TYPE, Integer.parseInt(req.getParameter("HP_PumpType"))),
						PumpType.class) );
				hp.setPumpSize( (PumpSize)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HEAT_PUMP_SIZE, Integer.parseInt(req.getParameter("HP_PumpSize"))),
						PumpSize.class) );
				hp.setStandbySource( (StandbySource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HP_STANDBY_SOURCE, Integer.parseInt(req.getParameter("HP_StandbySrc"))),
						StandbySource.class) );
				
				try {
					if (req.getParameter("HP_RestartDelaySec").length() > 0)
						hp.setRestartDelaySeconds( Integer.parseInt(req.getParameter("HP_RestartDelaySec")) );
					else
						hp.deleteRestartDelaySeconds();
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("HP_RestartDelaySec") + "' for restart delay");
				}
			}
			else if (updateApp.getIrrigation() != null) {
				Irrigation irr = updateApp.getIrrigation();
				irr.setIrrigationType( (IrrigationType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE, Integer.parseInt(req.getParameter("IRR_IrrigationType"))),
						IrrigationType.class) );
				irr.setHorsePower( (HorsePower)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_IRR_HORSE_POWER, Integer.parseInt(req.getParameter("IRR_HorsePower"))),
						HorsePower.class) );
				irr.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_IRR_ENERGY_SOURCE, Integer.parseInt(req.getParameter("IRR_EnergySrc"))),
						EnergySource.class) );
				irr.setSoilType( (SoilType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_IRR_SOIL_TYPE, Integer.parseInt(req.getParameter("IRR_SoilType"))),
						SoilType.class) );
				irr.setMeterLocation( (MeterLocation)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION, Integer.parseInt(req.getParameter("IRR_MeterLoc"))),
						MeterLocation.class) );
				irr.setMeterVoltage( (MeterVoltage)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE, Integer.parseInt(req.getParameter("IRR_MeterVolt"))),
						MeterVoltage.class) );
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateAppliance( updateApp );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsUpdateAppliance updateApp = reqOper.getStarsUpdateAppliance();
    		
			try {
				updateAppliance( updateApp, liteAcctInfo );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription("Appliance information updated successfully");
            
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the appliance information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsAppliance appliance = (StarsAppliance)
					StarsFactory.newStarsApp(reqOper.getStarsUpdateAppliance(), StarsAppliance.class);
			
			StarsYukonUser user = (StarsYukonUser)
					session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				if (appliances.getStarsAppliance(i).getApplianceID() == appliance.getApplianceID()) {
					appliances.setStarsAppliance(i, appliance);
					break;
				}
			}
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static void updateAppliance(StarsUpdateAppliance updateApp, LiteStarsCustAccountInformation liteAcctInfo)
		throws WebClientException, CommandExecutionException
	{
		LiteStarsAppliance liteApp = null;
		int appIdx = 0;
		
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
			if (lApp.getApplianceID() == updateApp.getApplianceID()) {
				liteApp = lApp;
				appIdx = i;
				break;
			}
		}
        
		if (liteApp == null)
			throw new WebClientException( "Cannot find the appliance to be updated" );
		
		com.cannontech.database.data.stars.appliance.ApplianceBase app =
				(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
    	
		if (updateApp.hasApplianceCategoryID())
			appDB.setApplianceCategoryID( new Integer(updateApp.getApplianceCategoryID()) );
		appDB.setManufacturerID( new Integer(updateApp.getManufacturer().getEntryID()) );
		appDB.setLocationID( new Integer(updateApp.getLocation().getEntryID()) );
		appDB.setNotes( updateApp.getNotes() );
		appDB.setModelNumber( updateApp.getModelNumber() );
		
		if (updateApp.hasYearManufactured())
			appDB.setYearManufactured( new Integer(updateApp.getYearManufactured()) );
		else
			appDB.setYearManufactured( new Integer(-1) );
		if (updateApp.hasKWCapacity())
			appDB.setKWCapacity( new Integer(updateApp.getKWCapacity()) );
		else
			appDB.setKWCapacity( new Integer(-1) );
		if (updateApp.hasEfficiencyRating())
			appDB.setEfficiencyRating( new Integer(updateApp.getEfficiencyRating()) );
		else
			appDB.setEfficiencyRating( new Integer(-1) );
    	
		Transaction.createTransaction(Transaction.UPDATE, appDB).execute();
		StarsLiteFactory.setLiteStarsAppliance( liteApp, app );
		
		if (updateApp.getAirConditioner() != null) {
			if (!(liteApp instanceof LiteStarsAppAirConditioner)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppAirConditioner();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceAirConditioner appAC = new ApplianceAirConditioner();
			StarsLiteFactory.setApplianceAirConditioner( appAC, (LiteStarsAppAirConditioner) liteApp );
			appAC.setTonnageID( new Integer(updateApp.getAirConditioner().getTonnage().getEntryID()) );
			appAC.setTypeID( new Integer(updateApp.getAirConditioner().getACType().getEntryID()) );
			
			if (liteApp.isExtended())
				appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.UPDATE, appAC).execute();
			else
				appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT, appAC).execute();
			StarsLiteFactory.setLiteAppAirConditioner( (LiteStarsAppAirConditioner) liteApp, appAC );
		}
		else if (updateApp.getWaterHeater() != null) {
			if (!(liteApp instanceof LiteStarsAppWaterHeater)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppWaterHeater();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceWaterHeater appWH = new ApplianceWaterHeater();
			StarsLiteFactory.setApplianceWaterHeater( appWH, (LiteStarsAppWaterHeater) liteApp );
			appWH.setNumberOfGallonsID( new Integer(updateApp.getWaterHeater().getNumberOfGallons().getEntryID()) );
			appWH.setEnergySourceID( new Integer(updateApp.getWaterHeater().getEnergySource().getEntryID()) );
			if (updateApp.getWaterHeater().hasNumberOfElements())
				appWH.setNumberOfElements( new Integer(updateApp.getWaterHeater().getNumberOfElements()) );
			else
				appWH.setNumberOfElements( new Integer(-1) );
			
			if (liteApp.isExtended())
				appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.UPDATE, appWH).execute();
			else
				appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
			StarsLiteFactory.setLiteAppWaterHeater( (LiteStarsAppWaterHeater) liteApp, appWH );
		}
		else if (updateApp.getDualFuel() != null) {
			if (!(liteApp instanceof LiteStarsAppDualFuel)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppDualFuel();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceDualFuel appDF = new ApplianceDualFuel();
			StarsLiteFactory.setApplianceDualFuel( appDF, (LiteStarsAppDualFuel) liteApp );
			appDF.setSwitchOverTypeID( new Integer(updateApp.getDualFuel().getSwitchOverType().getEntryID()) );
			appDF.setSecondaryEnergySourceID( new Integer(updateApp.getDualFuel().getSecondaryEnergySource().getEntryID()) );
			if (updateApp.getDualFuel().hasSecondaryKWCapacity())
				appDF.setSecondaryKWCapacity( new Integer(updateApp.getDualFuel().getSecondaryKWCapacity()) );
			else
				appDF.setSecondaryKWCapacity( new Integer(-1) );
			
			if (liteApp.isExtended())
				appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.UPDATE, appDF).execute();
			else
				appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
			StarsLiteFactory.setLiteAppDualFuel( (LiteStarsAppDualFuel) liteApp, appDF );
		}
		else if (updateApp.getGenerator() != null) {
			if (!(liteApp instanceof LiteStarsAppGenerator)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppGenerator();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceGenerator appGen = new ApplianceGenerator();
			StarsLiteFactory.setApplianceGenerator( appGen, (LiteStarsAppGenerator) liteApp );
			appGen.setTransferSwitchTypeID( new Integer(updateApp.getGenerator().getTransferSwitchType().getEntryID()) );
			appGen.setTransferSwitchMfgID( new Integer(updateApp.getGenerator().getTransferSwitchManufacturer().getEntryID()) );
			if (updateApp.getGenerator().hasPeakKWCapacity())
				appGen.setPeakKWCapacity( new Integer(updateApp.getGenerator().getPeakKWCapacity()) );
			else
				appGen.setPeakKWCapacity( new Integer(-1) );
			if (updateApp.getGenerator().hasFuelCapGallons())
				appGen.setFuelCapGallons( new Integer(updateApp.getGenerator().getFuelCapGallons()) );
			else
				appGen.setFuelCapGallons( new Integer(-1) );
			if (updateApp.getGenerator().hasStartDelaySeconds())
				appGen.setStartDelaySeconds( new Integer(updateApp.getGenerator().getStartDelaySeconds()) );
			else
				appGen.setStartDelaySeconds( new Integer(-1) );
			
			if (liteApp.isExtended())
				appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.UPDATE, appGen).execute();
			else
				appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
			StarsLiteFactory.setLiteAppGenerator( (LiteStarsAppGenerator) liteApp, appGen );
		}
		else if (updateApp.getGrainDryer() != null) {
			if (!(liteApp instanceof LiteStarsAppGrainDryer)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppGrainDryer();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceGrainDryer appGD = new ApplianceGrainDryer();
			StarsLiteFactory.setApplianceGrainDryer( appGD, (LiteStarsAppGrainDryer) liteApp );
			appGD.setDryerTypeID( new Integer(updateApp.getGrainDryer().getDryerType().getEntryID()) );
			appGD.setBinSizeID( new Integer(updateApp.getGrainDryer().getBinSize().getEntryID()) );
			appGD.setBlowerEnergySourceID( new Integer(updateApp.getGrainDryer().getBlowerEnergySource().getEntryID()) );
			appGD.setBlowerHorsePowerID( new Integer(updateApp.getGrainDryer().getBlowerHorsePower().getEntryID()) );
			appGD.setBlowerHeatSourceID( new Integer(updateApp.getGrainDryer().getBlowerHeatSource().getEntryID()) );
			
			if (liteApp.isExtended())
				appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.UPDATE, appGD).execute();
			else
				appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT, appGD).execute();
			StarsLiteFactory.setLiteAppGrainDryer( (LiteStarsAppGrainDryer) liteApp, appGD );
		}
		else if (updateApp.getStorageHeat() != null) {
			if (!(liteApp instanceof LiteStarsAppStorageHeat)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppStorageHeat();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceStorageHeat appSH = new ApplianceStorageHeat();
			StarsLiteFactory.setApplianceStorageHeat( appSH, (LiteStarsAppStorageHeat) liteApp );
			appSH.setStorageTypeID( new Integer(updateApp.getStorageHeat().getStorageType().getEntryID()) );
			if (updateApp.getStorageHeat().hasPeakKWCapacity())
				appSH.setPeakKWCapacity( new Integer(updateApp.getStorageHeat().getPeakKWCapacity()) );
			else
				appSH.setPeakKWCapacity( new Integer(-1) );
			if (updateApp.getStorageHeat().hasHoursToRecharge())
				appSH.setHoursToRecharge( new Integer(updateApp.getStorageHeat().getHoursToRecharge()) );
			else
				appSH.setHoursToRecharge( new Integer(-1) );
			
			if (liteApp.isExtended())
				appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.UPDATE, appSH).execute();
			else
				appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
			StarsLiteFactory.setLiteAppStorageHeat( (LiteStarsAppStorageHeat) liteApp, appSH );
		}
		else if (updateApp.getHeatPump() != null) {
			if (!(liteApp instanceof LiteStarsAppHeatPump)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppHeatPump();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceHeatPump appHP = new ApplianceHeatPump();
			StarsLiteFactory.setApplianceHeatPump( appHP, (LiteStarsAppHeatPump) liteApp );
			appHP.setPumpTypeID( new Integer(updateApp.getHeatPump().getPumpType().getEntryID()) );
			appHP.setPumpSizeID( new Integer(updateApp.getHeatPump().getPumpSize().getEntryID()) );
			appHP.setStandbySourceID( new Integer(updateApp.getHeatPump().getStandbySource().getEntryID()) );
			if (updateApp.getHeatPump().hasRestartDelaySeconds())
				appHP.setSecondsDelayToRestart( new Integer(updateApp.getHeatPump().getRestartDelaySeconds()) );
			else
				appHP.setSecondsDelayToRestart( new Integer(-1) );
			
			if (liteApp.isExtended())
				appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.UPDATE, appHP).execute();
			else
				appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
			StarsLiteFactory.setLiteAppHeatPump( (LiteStarsAppHeatPump) liteApp, appHP );
		}
		else if (updateApp.getIrrigation() != null) {
			if (!(liteApp instanceof LiteStarsAppIrrigation)) {
				removeApplianceChild( liteApp );
				LiteStarsAppliance liteApp2 = new LiteStarsAppIrrigation();
				copyLiteStarsAppliance( liteApp2, liteApp );
				liteAcctInfo.getAppliances().set( appIdx, liteApp2 );
				liteApp = liteApp2;
			}
			
			ApplianceIrrigation appIrr = new ApplianceIrrigation();
			StarsLiteFactory.setApplianceIrrigation( appIrr, (LiteStarsAppIrrigation) liteApp );
			appIrr.setIrrigationTypeID( new Integer(updateApp.getIrrigation().getIrrigationType().getEntryID()) );
			appIrr.setHorsePowerID( new Integer(updateApp.getIrrigation().getHorsePower().getEntryID()) );
			appIrr.setEnergySourceID( new Integer(updateApp.getIrrigation().getEnergySource().getEntryID()) );
			appIrr.setSoilTypeID( new Integer(updateApp.getIrrigation().getSoilType().getEntryID()) );
			appIrr.setMeterLocationID( new Integer(updateApp.getIrrigation().getMeterLocation().getEntryID()) );
			appIrr.setMeterVoltageID( new Integer(updateApp.getIrrigation().getMeterVoltage().getEntryID()) );
			
			if (liteApp.isExtended())
				appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.UPDATE, appIrr).execute();
			else
				appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT, appIrr).execute();
			StarsLiteFactory.setLiteAppIrrigation( (LiteStarsAppIrrigation) liteApp, appIrr );
		}
	}
	
	private static void removeApplianceChild(LiteStarsAppliance liteApp) throws CommandExecutionException {
		if (liteApp instanceof LiteStarsAppAirConditioner) {
			ApplianceAirConditioner appAC = new ApplianceAirConditioner();
			appAC.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appAC).execute();
		}
		else if (liteApp instanceof LiteStarsAppWaterHeater) {
			ApplianceWaterHeater appWH = new ApplianceWaterHeater();
			appWH.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appWH).execute();
		}
		else if (liteApp instanceof LiteStarsAppDualFuel) {
			ApplianceDualFuel appDF = new ApplianceDualFuel();
			appDF.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appDF).execute();
		}
		else if (liteApp instanceof LiteStarsAppGenerator) {
			ApplianceGenerator appGen = new ApplianceGenerator();
			appGen.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appGen).execute();
		}
		else if (liteApp instanceof LiteStarsAppGrainDryer) {
			ApplianceGrainDryer appGD = new ApplianceGrainDryer();
			appGD.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appGD).execute();
		}
		else if (liteApp instanceof LiteStarsAppStorageHeat) {
			ApplianceStorageHeat appSH = new ApplianceStorageHeat();
			appSH.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appSH).execute();
		}
		else if (liteApp instanceof LiteStarsAppHeatPump) {
			ApplianceHeatPump appHP = new ApplianceHeatPump();
			appHP.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appHP).execute();
		}
		else if (liteApp instanceof LiteStarsAppIrrigation) {
			ApplianceIrrigation appIrr = new ApplianceIrrigation();
			appIrr.setApplianceID( new Integer(liteApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, appIrr).execute();
		}
	}
	
	private static void copyLiteStarsAppliance(LiteStarsAppliance liteAppNew, LiteStarsAppliance liteAppOld) {
		liteAppNew.setApplianceID( liteAppOld.getApplianceID() );
		liteAppNew.setAccountID( liteAppOld.getAccountID() );
		liteAppNew.setApplianceCategoryID( liteAppOld.getApplianceCategoryID() );
		liteAppNew.setLmProgramID( liteAppOld.getLmProgramID() );
		liteAppNew.setYearManufactured( liteAppOld.getYearManufactured() );
		liteAppNew.setManufacturerID( liteAppOld.getManufacturerID() );
		liteAppNew.setLocationID( liteAppOld.getLocationID() );
		liteAppNew.setNotes( liteAppOld.getNotes() );
		liteAppNew.setModelNumber( liteAppOld.getModelNumber() );
		liteAppNew.setKWCapacity( liteAppOld.getKWCapacity() );
		liteAppNew.setEfficiencyRating( liteAppOld.getEfficiencyRating() );
		liteAppNew.setInventoryID( liteAppOld.getInventoryID() );
		liteAppNew.setAddressingGroupID( liteAppOld.getAddressingGroupID() );
	}

}
