package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
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
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
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
					YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("Manufacturer")) ),
					Manufacturer.class) );
			
			updateApp.setLocation( (Location)StarsFactory.newStarsCustListEntry(
					YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("Location")) ),
					Location.class) );
			
			if (updateApp.getAirConditioner() != null) {
				AirConditioner ac = updateApp.getAirConditioner();
				ac.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("AC_Tonnage")) ),
						Tonnage.class) );
				ac.setACType( (ACType)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("AC_Type")) ),
						ACType.class) );
			}
			else if (updateApp.getWaterHeater() != null) {
				WaterHeater wh = updateApp.getWaterHeater();
				wh.setNumberOfGallons( (NumberOfGallons)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("WH_GallonNum")) ),
						NumberOfGallons.class) );
				wh.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("WH_EnergySrc")) ),
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
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("DF_SwitchOverType")) ),
						SwitchOverType.class) );
				df.setSecondaryEnergySource( (SecondaryEnergySource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("DF_SecondarySrc")) ),
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
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GEN_TranSwitchType")) ),
						TransferSwitchType.class) );
				gen.setTransferSwitchManufacturer( (TransferSwitchManufacturer)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GEN_TranSwitchMfg")) ),
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
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GD_DryerType")) ),
						DryerType.class) );
				gd.setBinSize( (BinSize)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GD_BinSize")) ),
						BinSize.class) );
				gd.setBlowerEnergySource( (BlowerEnergySource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerEnergySrc")) ),
						BlowerEnergySource.class) );
				gd.setBlowerHorsePower( (BlowerHorsePower)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerHorsePower")) ),
						BlowerHorsePower.class) );
				gd.setBlowerHeatSource( (BlowerHeatSource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerHeatSrc")) ),
						BlowerHeatSource.class) );
			}
			else if (updateApp.getStorageHeat() != null) {
				StorageHeat sh = updateApp.getStorageHeat();
				sh.setStorageType( (StorageType)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("SH_StorageType")) ),
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
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("HP_PumpType")) ),
						PumpType.class) );
				hp.setPumpSize( (PumpSize)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("HP_PumpSize")) ),
						PumpSize.class) );
				hp.setStandbySource( (StandbySource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("HP_StandbySrc")) ),
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
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_IrrigationType")) ),
						IrrigationType.class) );
				irr.setHorsePower( (HorsePower)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_HorsePower")) ),
						HorsePower.class) );
				irr.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_EnergySrc")) ),
						EnergySource.class) );
				irr.setSoilType( (SoilType)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_SoilType")) ),
						SoilType.class) );
				irr.setMeterLocation( (MeterLocation)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_MeterLoc")) ),
						MeterLocation.class) );
				irr.setMeterVoltage( (MeterVoltage)StarsFactory.newStarsCustListEntry(
						YukonListFuncs.getYukonListEntry( Integer.parseInt(req.getParameter("IRR_MeterVolt")) ),
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
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
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
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
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
			ApplianceAirConditioner appAC = new ApplianceAirConditioner();
			appAC.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appAC.setTonnageID( new Integer(updateApp.getAirConditioner().getTonnage().getEntryID()) );
			appAC.setTypeID( new Integer(updateApp.getAirConditioner().getACType().getEntryID()) );
			
			if (liteApp.getAirConditioner() != null) {
				appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.UPDATE, appAC).execute();
			}
			else {
				appAC = (ApplianceAirConditioner) Transaction.createTransaction(Transaction.INSERT, appAC).execute();
				liteApp.setAirConditioner( new LiteStarsAppliance.AirConditioner() );
			}
			
			StarsLiteFactory.setLiteAppAirConditioner( liteApp.getAirConditioner(), appAC );
		}
		else if (updateApp.getWaterHeater() != null) {
			ApplianceWaterHeater appWH = new ApplianceWaterHeater();
			appWH.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appWH.setNumberOfGallonsID( new Integer(updateApp.getWaterHeater().getNumberOfGallons().getEntryID()) );
			appWH.setEnergySourceID( new Integer(updateApp.getWaterHeater().getEnergySource().getEntryID()) );
			if (updateApp.getWaterHeater().hasNumberOfElements())
				appWH.setNumberOfElements( new Integer(updateApp.getWaterHeater().getNumberOfElements()) );
			else
				appWH.setNumberOfElements( new Integer(-1) );
			
			if (liteApp.getWaterHeater() != null) {
				appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.UPDATE, appWH).execute();
			}
			else {
				appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
				liteApp.setWaterHeater( new LiteStarsAppliance.WaterHeater() );
			}
			
			StarsLiteFactory.setLiteAppWaterHeater( liteApp.getWaterHeater(), appWH );
		}
		else if (updateApp.getDualFuel() != null) {
			ApplianceDualFuel appDF = new ApplianceDualFuel();
			appDF.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appDF.setSwitchOverTypeID( new Integer(updateApp.getDualFuel().getSwitchOverType().getEntryID()) );
			appDF.setSecondaryEnergySourceID( new Integer(updateApp.getDualFuel().getSecondaryEnergySource().getEntryID()) );
			if (updateApp.getDualFuel().hasSecondaryKWCapacity())
				appDF.setSecondaryKWCapacity( new Integer(updateApp.getDualFuel().getSecondaryKWCapacity()) );
			else
				appDF.setSecondaryKWCapacity( new Integer(-1) );
			
			if (liteApp.getDualFuel() != null) {
				appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.UPDATE, appDF).execute();
			}
			else {
				appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
				liteApp.setDualFuel( new LiteStarsAppliance.DualFuel() );
			}
			
			StarsLiteFactory.setLiteAppDualFuel( liteApp.getDualFuel(), appDF );
		}
		else if (updateApp.getGenerator() != null) {
			ApplianceGenerator appGen = new ApplianceGenerator();
			appGen.setApplianceID( new Integer(liteApp.getApplianceID()) );
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
			
			if (liteApp.getGenerator() != null) {
				appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.UPDATE, appGen).execute();
			}
			else {
				appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
				liteApp.setGenerator( new LiteStarsAppliance.Generator() );
			}
			
			StarsLiteFactory.setLiteAppGenerator( liteApp.getGenerator(), appGen );
		}
		else if (updateApp.getGrainDryer() != null) {
			ApplianceGrainDryer appGD = new ApplianceGrainDryer();
			appGD.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appGD.setDryerTypeID( new Integer(updateApp.getGrainDryer().getDryerType().getEntryID()) );
			appGD.setBinSizeID( new Integer(updateApp.getGrainDryer().getBinSize().getEntryID()) );
			appGD.setBlowerEnergySourceID( new Integer(updateApp.getGrainDryer().getBlowerEnergySource().getEntryID()) );
			appGD.setBlowerHorsePowerID( new Integer(updateApp.getGrainDryer().getBlowerHorsePower().getEntryID()) );
			appGD.setBlowerHeatSourceID( new Integer(updateApp.getGrainDryer().getBlowerHeatSource().getEntryID()) );
			
			if (liteApp.getGrainDryer() != null) {
				appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.UPDATE, appGD).execute();
			}
			else {
				appGD = (ApplianceGrainDryer) Transaction.createTransaction(Transaction.INSERT, appGD).execute();
				liteApp.setGrainDryer( new LiteStarsAppliance.GrainDryer() );
			}
			
			StarsLiteFactory.setLiteAppGrainDryer( liteApp.getGrainDryer(), appGD );
		}
		else if (updateApp.getStorageHeat() != null) {
			ApplianceStorageHeat appSH = new ApplianceStorageHeat();
			appSH.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appSH.setStorageTypeID( new Integer(updateApp.getStorageHeat().getStorageType().getEntryID()) );
			if (updateApp.getStorageHeat().hasPeakKWCapacity())
				appSH.setPeakKWCapacity( new Integer(updateApp.getStorageHeat().getPeakKWCapacity()) );
			else
				appSH.setPeakKWCapacity( new Integer(-1) );
			if (updateApp.getStorageHeat().hasHoursToRecharge())
				appSH.setHoursToRecharge( new Integer(updateApp.getStorageHeat().getHoursToRecharge()) );
			else
				appSH.setHoursToRecharge( new Integer(-1) );
			
			if (liteApp.getStorageHeat() != null) {
				appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.UPDATE, appSH).execute();
			}
			else {
				appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
				liteApp.setStorageHeat( new LiteStarsAppliance.StorageHeat() );
			}
			
			StarsLiteFactory.setLiteAppStorageHeat( liteApp.getStorageHeat(), appSH );
		}
		else if (updateApp.getHeatPump() != null) {
			ApplianceHeatPump appHP = new ApplianceHeatPump();
			appHP.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appHP.setPumpTypeID( new Integer(updateApp.getHeatPump().getPumpType().getEntryID()) );
			appHP.setPumpSizeID( new Integer(updateApp.getHeatPump().getPumpSize().getEntryID()) );
			appHP.setStandbySourceID( new Integer(updateApp.getHeatPump().getStandbySource().getEntryID()) );
			if (updateApp.getHeatPump().hasRestartDelaySeconds())
				appHP.setSecondsDelayToRestart( new Integer(updateApp.getHeatPump().getRestartDelaySeconds()) );
			else
				appHP.setSecondsDelayToRestart( new Integer(-1) );
			
			if (liteApp.getHeatPump() != null) {
				appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.UPDATE, appHP).execute();
			}
			else {
				appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
				liteApp.setHeatPump( new LiteStarsAppliance.HeatPump() );
			}
			
			StarsLiteFactory.setLiteAppHeatPump( liteApp.getHeatPump(), appHP );
		}
		else if (updateApp.getIrrigation() != null) {
			ApplianceIrrigation appIrr = new ApplianceIrrigation();
			appIrr.setApplianceID( new Integer(liteApp.getApplianceID()) );
			appIrr.setIrrigationTypeID( new Integer(updateApp.getIrrigation().getIrrigationType().getEntryID()) );
			appIrr.setHorsePowerID( new Integer(updateApp.getIrrigation().getHorsePower().getEntryID()) );
			appIrr.setEnergySourceID( new Integer(updateApp.getIrrigation().getEnergySource().getEntryID()) );
			appIrr.setSoilTypeID( new Integer(updateApp.getIrrigation().getSoilType().getEntryID()) );
			appIrr.setMeterLocationID( new Integer(updateApp.getIrrigation().getMeterLocation().getEntryID()) );
			appIrr.setMeterVoltageID( new Integer(updateApp.getIrrigation().getMeterVoltage().getEntryID()) );
			
			if (liteApp.getIrrigation() != null) {
				appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.UPDATE, appIrr).execute();
			}
			else {
				appIrr = (ApplianceIrrigation) Transaction.createTransaction(Transaction.INSERT, appIrr).execute();
				liteApp.setIrrigation( new LiteStarsAppliance.Irrigation() );
			}
			
			StarsLiteFactory.setLiteAppIrrigation( liteApp.getIrrigation(), appIrr );
		}
	}

}
