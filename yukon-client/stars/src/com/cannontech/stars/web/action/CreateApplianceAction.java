package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
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
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceBase;
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
import com.cannontech.stars.web.servlet.SOAPServer;
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
import com.cannontech.stars.xml.serialize.StarsCreateAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateApplianceResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
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
public class CreateApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			java.util.Hashtable selectionLists = (java.util.Hashtable) session.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateAppliance newApp = new StarsCreateAppliance();
			newApp.setApplianceCategoryID( Integer.parseInt(req.getParameter("AppCatID")) );
			newApp.setNotes( req.getParameter("Notes").replaceAll(System.getProperty("line.separator"), "<br>") );
			newApp.setModelNumber( req.getParameter("ModelNo") );
			
			try {
				if (req.getParameter("ManuYear").length() > 0)
					newApp.setYearManufactured( Integer.parseInt(req.getParameter("ManuYear")) );
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("ManuYear") + "' for year manufactured");
			}
			try {
				if (req.getParameter("KWCapacity").length() > 0)
					newApp.setKWCapacity( Integer.parseInt(req.getParameter("KWCapacity")) );
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("KWCapacity") + "' for KW capacity");
			}
			try {
				if (req.getParameter("EffRating").length() > 0)
					newApp.setEfficiencyRating( Integer.parseInt(req.getParameter("EffRating")) );
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("EffRating") + "' for efficiency rating");
			}
			
			newApp.setManufacturer( (Manufacturer)StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, Integer.parseInt(req.getParameter("Manufacturer"))),
					Manufacturer.class) );
			
			newApp.setLocation( (Location)StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION, Integer.parseInt(req.getParameter("Location"))),
					Location.class) );
			
			int categoryID = Integer.parseInt( req.getParameter("CatID") );
			int catDefID = ServletUtils.getStarsCustListEntryByID(
					selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY, categoryID).getYukonDefID();
			
			if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER)
			{
				AirConditioner ac = new AirConditioner();
				ac.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_AC_TONNAGE, Integer.parseInt(req.getParameter("AC_Tonnage"))),
						Tonnage.class) );
				ac.setACType( (ACType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE, Integer.parseInt(req.getParameter("AC_Type"))),
						ACType.class) );
				newApp.setAirConditioner( ac );
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER)
			{
				WaterHeater wh = new WaterHeater();
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
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("WH_ElementNum") + "' for # heating coils");
				}
				newApp.setWaterHeater( wh );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL)
			{
				DualFuel df = new DualFuel();
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
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("DF_KWCapacity2") + "' for secondary KW capacity");
				}
				newApp.setDualFuel( df );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR)
			{
				Generator gen = new Generator();
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
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_KWCapacity") + "' for peak KW capacity");
				}
				try {
					if (req.getParameter("GEN_FuelCapGal").length() > 0)
						gen.setFuelCapGallons( Integer.parseInt(req.getParameter("GEN_FuelCapGal")) );
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_FuelCapGal") + "' for fuel capacity");
				}
				try {
					if (req.getParameter("GEN_StartDelaySec").length() > 0)
						gen.setStartDelaySeconds( Integer.parseInt(req.getParameter("GEN_StartDelaySec")) );
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("GEN_StartDelaySec") + "' for start delay");
				}
				newApp.setGenerator( gen );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER)
			{
				GrainDryer gd = new GrainDryer();
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
				newApp.setGrainDryer( gd );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT)
			{
				StorageHeat sh = new StorageHeat();
				sh.setStorageType( (StorageType)StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_HEAT_TYPE, Integer.parseInt(req.getParameter("SH_StorageType"))),
						StorageType.class) );
				
				try {
					if (req.getParameter("SH_KWCapacity").length() > 0)
						sh.setPeakKWCapacity( Integer.parseInt(req.getParameter("SH_KWCapacity")) );
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("SH_KWCapacity") + "' for peak KW capacity");
				}
				try {
					if (req.getParameter("SH_RechargeHour").length() > 0)
						sh.setHoursToRecharge( Integer.parseInt(req.getParameter("SH_RechargeHour")) );
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("SH_RechargeHour") + "' for recharge time");
				}
				newApp.setStorageHeat( sh );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP)
			{
				HeatPump hp = new HeatPump();
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
				}
				catch (NumberFormatException e) {
					throw new WebClientException("Invalid number format '" + req.getParameter("HP_RestartDelaySec") + "' for restart delay");
				}
				newApp.setHeatPump( hp );
			}
			else if (categoryID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION)
			{
				Irrigation irr = new Irrigation();
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
				newApp.setIrrigation( irr );
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateAppliance( newApp );
			
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
			StarsCreateAppliance newApp = reqOper.getStarsCreateAppliance();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation)
					session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
			LiteStarsAppliance liteApp = createAppliance( newApp, liteAcctInfo, energyCompany );
            
			StarsCreateApplianceResponse resp = new StarsCreateApplianceResponse();
			resp.setStarsAppliance( StarsLiteFactory.createStarsAppliance(liteApp, energyCompany) );
			respOper.setStarsCreateApplianceResponse( resp );
            
			SOAPMessage respMsg = SOAPUtil.buildSOAPMessage( respOper );
			return respMsg;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the appliance") );
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

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCreateApplianceResponse resp = operation.getStarsCreateApplianceResponse();
			StarsAppliance app = resp.getStarsAppliance();
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
			StarsEnergyCompanySettings ecSettings = (StarsEnergyCompanySettings)
					session.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms categories = ecSettings.getStarsEnrollmentPrograms();
            
			StarsAppliances starsApps = accountInfo.getStarsAppliances();
			String appDesc = ServletUtils.getApplianceDescription( categories, app );
			int idx = -1;
            
			for (idx = starsApps.getStarsApplianceCount() - 1; idx >= 0; idx--) {
				String desc = ServletUtils.getApplianceDescription( categories, starsApps.getStarsAppliance(idx) );
				if (desc.compareTo( appDesc ) <= 0)
					break;
			}
            
			starsApps.addStarsAppliance( idx+1, app );
			session.setAttribute( ServletUtils.ATT_REDIRECT, "/operator/Consumer/Appliance.jsp?AppNo=" + String.valueOf(idx+1) );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static LiteStarsAppliance createAppliance(StarsCreateAppliance newApp, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws CommandExecutionException
	{
		com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
		ApplianceBase appDB = app.getApplianceBase();
        
		appDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
		appDB.setApplianceCategoryID( new Integer(newApp.getApplianceCategoryID()) );
		appDB.setLMProgramID( new Integer(0) );
		appDB.setManufacturerID( new Integer(newApp.getManufacturer().getEntryID()) );
		appDB.setLocationID( new Integer(newApp.getLocation().getEntryID()) );
		appDB.setNotes( newApp.getNotes() );
		appDB.setModelNumber( newApp.getModelNumber() );
        
		if (newApp.hasYearManufactured())
			appDB.setYearManufactured( new Integer(newApp.getYearManufactured()) );
		else
			appDB.setYearManufactured( new Integer(-1) );
		if (newApp.hasKWCapacity())
			appDB.setKWCapacity( new Integer(newApp.getKWCapacity()) );
		else
			appDB.setKWCapacity( new Integer(-1) );
		if (newApp.hasEfficiencyRating())
			appDB.setEfficiencyRating( new Integer(newApp.getEfficiencyRating()) );
		else
			appDB.setEfficiencyRating( new Integer(-1) );
        
		app = (com.cannontech.database.data.stars.appliance.ApplianceBase) Transaction.createTransaction(Transaction.INSERT, app).execute();
		LiteStarsAppliance liteApp = null;
        
		if (newApp.getAirConditioner() != null) {
			ApplianceAirConditioner appAC = new ApplianceAirConditioner();
			appAC.setApplianceID( app.getApplianceBase().getApplianceID() );
			appAC.setTonnageID( new Integer(newApp.getAirConditioner().getTonnage().getEntryID()) );
			appAC.setTypeID( new Integer(newApp.getAirConditioner().getACType().getEntryID()) );
			appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT, appAC).execute();
            
			liteApp = new LiteStarsAppAirConditioner();
			StarsLiteFactory.setLiteAppAirConditioner( (LiteStarsAppAirConditioner) liteApp, appAC );
		}
		else if (newApp.getWaterHeater() != null) {
			ApplianceWaterHeater appWH = new ApplianceWaterHeater();
			appWH.setApplianceID( app.getApplianceBase().getApplianceID() );
			appWH.setNumberOfGallonsID( new Integer(newApp.getWaterHeater().getNumberOfGallons().getEntryID()) );
			appWH.setEnergySourceID( new Integer(newApp.getWaterHeater().getEnergySource().getEntryID()) );
			if (newApp.getWaterHeater().hasNumberOfElements())
				appWH.setNumberOfElements( new Integer(newApp.getWaterHeater().getNumberOfElements()) );
			else
				appWH.setNumberOfElements( new Integer(-1) );
			appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
            
			liteApp = new LiteStarsAppWaterHeater();
			StarsLiteFactory.setLiteAppWaterHeater( (LiteStarsAppWaterHeater) liteApp, appWH );
		}
		else if (newApp.getDualFuel() != null) {
			ApplianceDualFuel appDF = new ApplianceDualFuel();
			appDF.setApplianceID( app.getApplianceBase().getApplianceID() );
			appDF.setSwitchOverTypeID( new Integer(newApp.getDualFuel().getSwitchOverType().getEntryID()) );
			appDF.setSecondaryEnergySourceID( new Integer(newApp.getDualFuel().getSecondaryEnergySource().getEntryID()) );
			if (newApp.getDualFuel().hasSecondaryKWCapacity())
				appDF.setSecondaryKWCapacity( new Integer(newApp.getDualFuel().getSecondaryKWCapacity()) );
			else
				appDF.setSecondaryKWCapacity( new Integer(-1) );
			appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
            
			liteApp = new LiteStarsAppDualFuel();
			StarsLiteFactory.setLiteAppDualFuel( (LiteStarsAppDualFuel) liteApp, appDF );
		}
		else if (newApp.getGenerator() != null) {
			ApplianceGenerator appGen = new ApplianceGenerator();
			appGen.setApplianceID( app.getApplianceBase().getApplianceID() );
			appGen.setTransferSwitchTypeID( new Integer(newApp.getGenerator().getTransferSwitchType().getEntryID()) );
			appGen.setTransferSwitchMfgID( new Integer(newApp.getGenerator().getTransferSwitchManufacturer().getEntryID()) );
			if (newApp.getGenerator().hasPeakKWCapacity())
				appGen.setPeakKWCapacity( new Integer(newApp.getGenerator().getPeakKWCapacity()) );
			else
				appGen.setPeakKWCapacity( new Integer(-1) );
			if (newApp.getGenerator().hasFuelCapGallons())
				appGen.setFuelCapGallons( new Integer(newApp.getGenerator().getFuelCapGallons()) );
			else
				appGen.setFuelCapGallons( new Integer(-1) );
			if (newApp.getGenerator().hasStartDelaySeconds())
				appGen.setStartDelaySeconds( new Integer(newApp.getGenerator().getStartDelaySeconds()) );
			else
				appGen.setStartDelaySeconds( new Integer(-1) );
			appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
            
			liteApp = new LiteStarsAppGenerator();
			StarsLiteFactory.setLiteAppGenerator( (LiteStarsAppGenerator) liteApp, appGen );
		}
		else if (newApp.getGrainDryer() != null) {
			ApplianceGrainDryer appGD = new ApplianceGrainDryer();
			appGD.setApplianceID( app.getApplianceBase().getApplianceID() );
			appGD.setDryerTypeID( new Integer(newApp.getGrainDryer().getDryerType().getEntryID()) );
			appGD.setBinSizeID( new Integer(newApp.getGrainDryer().getBinSize().getEntryID()) );
			appGD.setBlowerEnergySourceID( new Integer(newApp.getGrainDryer().getBlowerEnergySource().getEntryID()) );
			appGD.setBlowerHorsePowerID( new Integer(newApp.getGrainDryer().getBlowerHorsePower().getEntryID()) );
			appGD.setBlowerHeatSourceID( new Integer(newApp.getGrainDryer().getBlowerHeatSource().getEntryID()) );
			appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT, appGD).execute();
            
			liteApp = new LiteStarsAppGrainDryer();
			StarsLiteFactory.setLiteAppGrainDryer( (LiteStarsAppGrainDryer) liteApp, appGD );
		}
		else if (newApp.getStorageHeat() != null) {
			ApplianceStorageHeat appSH = new ApplianceStorageHeat();
			appSH.setApplianceID( app.getApplianceBase().getApplianceID() );
			appSH.setStorageTypeID( new Integer(newApp.getStorageHeat().getStorageType().getEntryID()) );
			if (newApp.getStorageHeat().hasPeakKWCapacity())
				appSH.setPeakKWCapacity( new Integer(newApp.getStorageHeat().getPeakKWCapacity()) );
			else
				appSH.setPeakKWCapacity( new Integer(-1) );
			if (newApp.getStorageHeat().hasHoursToRecharge())
				appSH.setHoursToRecharge( new Integer(newApp.getStorageHeat().getHoursToRecharge()) );
			else
				appSH.setHoursToRecharge( new Integer(-1) );
			appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
            
			liteApp = new LiteStarsAppStorageHeat();
			StarsLiteFactory.setLiteAppStorageHeat( (LiteStarsAppStorageHeat) liteApp, appSH );
		}
		else if (newApp.getHeatPump() != null) {
			ApplianceHeatPump appHP = new ApplianceHeatPump();
			appHP.setApplianceID( app.getApplianceBase().getApplianceID() );
			appHP.setPumpTypeID( new Integer(newApp.getHeatPump().getPumpType().getEntryID()) );
			appHP.setPumpSizeID( new Integer(newApp.getHeatPump().getPumpSize().getEntryID()) );
			appHP.setStandbySourceID( new Integer(newApp.getHeatPump().getStandbySource().getEntryID()) );
			if (newApp.getHeatPump().hasRestartDelaySeconds())
				appHP.setSecondsDelayToRestart( new Integer(newApp.getHeatPump().getRestartDelaySeconds()) );
			else
				appHP.setSecondsDelayToRestart( new Integer(-1) );
			appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
            	
			liteApp = new LiteStarsAppHeatPump();
			StarsLiteFactory.setLiteAppHeatPump( (LiteStarsAppHeatPump) liteApp, appHP );
		}
		else if (newApp.getIrrigation() != null) {
			ApplianceIrrigation appIrr = new ApplianceIrrigation();
			appIrr.setApplianceID( app.getApplianceBase().getApplianceID() );
			appIrr.setIrrigationTypeID( new Integer(newApp.getIrrigation().getIrrigationType().getEntryID()) );
			appIrr.setHorsePowerID( new Integer(newApp.getIrrigation().getHorsePower().getEntryID()) );
			appIrr.setEnergySourceID( new Integer(newApp.getIrrigation().getEnergySource().getEntryID()) );
			appIrr.setSoilTypeID( new Integer(newApp.getIrrigation().getSoilType().getEntryID()) );
			appIrr.setMeterLocationID( new Integer(newApp.getIrrigation().getMeterLocation().getEntryID()) );
			appIrr.setMeterVoltageID( new Integer(newApp.getIrrigation().getMeterVoltage().getEntryID()) );
			appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT, appIrr).execute();
            
			liteApp = new LiteStarsAppIrrigation();
			StarsLiteFactory.setLiteAppIrrigation( (LiteStarsAppIrrigation) liteApp, appIrr );
		}
		else
			liteApp = new LiteStarsAppliance();
	    
		StarsLiteFactory.setLiteStarsAppliance( liteApp, app );
		liteAcctInfo.getAppliances().add( liteApp );
		
		return liteApp;
	}

}
