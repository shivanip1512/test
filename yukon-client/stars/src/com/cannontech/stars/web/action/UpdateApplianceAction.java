package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.*;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateAppliance;
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
			
			int appID = Integer.parseInt(req.getParameter("AppID"));
			StarsAppliance appliance = null;
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++)
				if (appliances.getStarsAppliance(i).getApplianceID() == appID) {
					appliance = appliances.getStarsAppliance(i);
					break;
				}
			if (appliance == null) return null;
			
			appliance.setYearManufactured( req.getParameter("ManuYear") );
			appliance.setNotes( req.getParameter("Notes") );
			appliance.setModelNumber( req.getParameter("ModelNo") );
			if (req.getParameter("KWCapacity").trim().length() > 0)
				appliance.setKWCapacity( Integer.parseInt(req.getParameter("KWCapacity")) );
			if (req.getParameter("EffRating").trim().length() > 0)
				appliance.setEfficiencyRating( Integer.parseInt(req.getParameter("EffRating")) );
			
			Manufacturer manu = (Manufacturer) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER, Integer.parseInt(req.getParameter("Manufacturer"))),
					Manufacturer.class );
			appliance.setManufacturer( manu );
			
			Location loc = (Location) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION, Integer.parseInt(req.getParameter("Location"))),
					Location.class );
			appliance.setLocation( loc );
			
			if (appliance.getAirConditioner() != null) {
				AirConditioner ac = appliance.getAirConditioner();
				Tonnage tonnage = (Tonnage) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_TONNAGE, Integer.parseInt(req.getParameter("AC_Tonnage"))),
						Tonnage.class );
				ac.setTonnage( tonnage );
				ACType acType = (ACType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_AC_TYPE, Integer.parseInt(req.getParameter("AC_Type"))),
						ACType.class );
				ac.setACType( acType );
			}
			else if (appliance.getWaterHeater() != null) {
				WaterHeater wh = appliance.getWaterHeater();
				NumberOfGallons galNum = (NumberOfGallons) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_NUM_OF_GALLONS, Integer.parseInt(req.getParameter("WH_GallonNum"))),
						NumberOfGallons.class );
				wh.setNumberOfGallons( galNum );
				EnergySource engSrc = (EnergySource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE, Integer.parseInt(req.getParameter("WH_EnergySrc"))),
						EnergySource.class );
				wh.setEnergySource( engSrc );
				wh.setNumberOfElements( Integer.parseInt(req.getParameter("WH_ElementNum")) );
			}
			else if (appliance.getDualFuel() != null) {
				DualFuel df = appliance.getDualFuel();
				SwitchOverType soType = (SwitchOverType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SWITCH_OVER_TYPE, Integer.parseInt(req.getParameter("DF_SwitchOverType"))),
						SwitchOverType.class );
				df.setSwitchOverType( soType );
				df.setSecondaryKWCapacity( Integer.parseInt(req.getParameter("DF_KWCapacity2")) );
				SecondaryEnergySource engSrc = (SecondaryEnergySource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE, Integer.parseInt(req.getParameter("DF_EnergySrc2"))),
						SecondaryEnergySource.class );
				df.setSecondaryEnergySource( engSrc );
			}
			else if (appliance.getGenerator() != null) {
				Generator gen = appliance.getGenerator();
				TransferSwitchType tsType = (TransferSwitchType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_TYPE, Integer.parseInt(req.getParameter("GEN_TranSwitchType"))),
						TransferSwitchType.class );
				gen.setTransferSwitchType( tsType );
				TransferSwitchManufacturer tsMfg = (TransferSwitchManufacturer) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_TRANSFER_SWITCH_MFG, Integer.parseInt(req.getParameter("GEN_TranSwitchMfg"))),
						TransferSwitchManufacturer.class );
				gen.setTransferSwitchManufacturer( tsMfg );
				gen.setPeakKWCapacity( Integer.parseInt(req.getParameter("GEN_KWCapacity")) );
				gen.setFuelCapGallons( Integer.parseInt(req.getParameter("GEN_FuelCapGal")) );
				gen.setStartDelaySeconds( Integer.parseInt(req.getParameter("GEN_StartDelaySec")) );
			}
			else if (appliance.getGrainDryer() != null) {
				GrainDryer gd = appliance.getGrainDryer();
				DryerType dType = (DryerType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DRYER_TYPE, Integer.parseInt(req.getParameter("GD_DryerType"))),
						DryerType.class );
				gd.setDryerType( dType );
				BinSize bSize = (BinSize) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_BIN_SIZE, Integer.parseInt(req.getParameter("GD_BinSize"))),
						BinSize.class );
				gd.setBinSize( bSize );
				BlowerEnergySource engSrc = (BlowerEnergySource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE, Integer.parseInt(req.getParameter("GD_BlowerEnergySrc"))),
						BlowerEnergySource.class );
				gd.setBlowerEnergySource( engSrc );
				BlowerHorsePower hp = (BlowerHorsePower) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER, Integer.parseInt(req.getParameter("GD_BlowerHorsePower"))),
						BlowerHorsePower.class );
				gd.setBlowerHorsePower( hp );
				BlowerHeatSource heatSrc = (BlowerHeatSource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HEAT_SOURCE, Integer.parseInt(req.getParameter("GD_BlowerHeatSrc"))),
						BlowerHeatSource.class );
				gd.setBlowerHeatSource( heatSrc );
			}
			else if (appliance.getStorageHeat() != null) {
				StorageHeat sh = appliance.getStorageHeat();
				StorageType stType = (StorageType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_STORAGE_TYPE, Integer.parseInt(req.getParameter("SH_StorageType"))),
						StorageType.class );
				sh.setStorageType( stType );
				sh.setPeakKWCapacity( Integer.parseInt(req.getParameter("SH_KWCapacity")) );
				sh.setHoursToRecharge( Integer.parseInt(req.getParameter("SH_RechargeHour")) );
			}
			else if (appliance.getHeatPump() != null) {
				HeatPump hp = appliance.getHeatPump();
				PumpType pType = (PumpType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_PUMP_TYPE, Integer.parseInt(req.getParameter("HP_PumpType"))),
						PumpType.class );
				hp.setPumpType( pType );
				StandbySource sbSrc = (StandbySource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_STANDBY_SOURCE, Integer.parseInt(req.getParameter("HP_StandbySrc"))),
						StandbySource.class );
				hp.setStandbySource( sbSrc );
				hp.setRestartDelaySeconds( Integer.parseInt(req.getParameter("HP_RestartDelaySec")) );
			}
			else if (appliance.getIrrigation() != null) {
				Irrigation irr = appliance.getIrrigation();
				IrrigationType irrType = (IrrigationType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_IRRIGATION_TYPE, Integer.parseInt(req.getParameter("IRR_IrrigationType"))),
						IrrigationType.class );
				irr.setIrrigationType( irrType );
				HorsePower hp = (HorsePower) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_HORSE_POWER, Integer.parseInt(req.getParameter("IRR_HorsePower"))),
						HorsePower.class );
				irr.setHorsePower( hp );
				EnergySource engSrc = (EnergySource) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_ENERGY_SOURCE, Integer.parseInt(req.getParameter("IRR_EnergySrc"))),
						EnergySource.class );
				irr.setEnergySource( engSrc );
				SoilType sType = (SoilType) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SOIL_TYPE, Integer.parseInt(req.getParameter("IRR_SoilType"))),
						SoilType.class );
				irr.setSoilType( sType );
				MeterLocation mtLoc = (MeterLocation) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_LOCATION, Integer.parseInt(req.getParameter("IRR_MeterLoc"))),
						MeterLocation.class );
				irr.setMeterLocation( mtLoc );
				MeterVoltage mtVolt = (MeterVoltage) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntryByID(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_VOLTAGE, Integer.parseInt(req.getParameter("IRR_MeterVolt"))),
						MeterVoltage.class );
				irr.setMeterVoltage( mtVolt );
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateAppliance( (StarsUpdateAppliance) StarsFactory.newStarsApp(appliance, StarsUpdateAppliance.class) );
			
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
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
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            StarsUpdateAppliance updateApp = reqOper.getStarsUpdateAppliance();
            LiteStarsAppliance liteApp = null;
            
            for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
            	LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
            	if (lApp.getApplianceID() == updateApp.getApplianceID()) {
            		liteApp = lApp;
            		break;
            	}
            }
            if (liteApp == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the appliance to be updated") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
    		
    		com.cannontech.database.data.stars.appliance.ApplianceBase app =
    				(com.cannontech.database.data.stars.appliance.ApplianceBase) StarsLiteFactory.createDBPersistent( liteApp );
    		com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
    		
    		appDB.setManufacturerID( new Integer(updateApp.getManufacturer().getEntryID()) );
    		if (updateApp.getYearManufactured().trim().length() > 0)
    			appDB.setYearManufactured( Integer.valueOf(updateApp.getYearManufactured()) );
    		appDB.setLocationID( new Integer(updateApp.getLocation().getEntryID()) );
    		appDB.setNotes( updateApp.getNotes() );
    		appDB.setModelNumber( updateApp.getModelNumber() );
    		appDB.setKWCapacity( new Integer(updateApp.getKWCapacity()) );
    		appDB.setEfficiencyRating( new Integer(updateApp.getEfficiencyRating()) );
    		
			Transaction.createTransaction(Transaction.UPDATE, appDB).execute();
			StarsLiteFactory.setLiteStarsAppliance( liteApp, app );
			
			if (updateApp.getAirConditioner() != null && liteApp instanceof LiteStarsAppAirConditioner) {
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
			else if (updateApp.getWaterHeater() != null && liteApp instanceof LiteStarsAppWaterHeater) {
				ApplianceWaterHeater appWH = new ApplianceWaterHeater();
				StarsLiteFactory.setApplianceWaterHeater( appWH, (LiteStarsAppWaterHeater) liteApp );
				appWH.setNumberOfGallonsID( new Integer(updateApp.getWaterHeater().getNumberOfGallons().getEntryID()) );
				appWH.setEnergySourceID( new Integer(updateApp.getWaterHeater().getEnergySource().getEntryID()) );
				appWH.setNumberOfElements( new Integer(updateApp.getWaterHeater().getNumberOfElements()) );
				
				if (liteApp.isExtended())
					appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.UPDATE, appWH).execute();
				else
					appWH = (ApplianceWaterHeater) Transaction.createTransaction(Transaction.INSERT, appWH).execute();
				StarsLiteFactory.setLiteAppWaterHeater( (LiteStarsAppWaterHeater) liteApp, appWH );
			}
			else if (updateApp.getDualFuel() != null && liteApp instanceof LiteStarsAppDualFuel) {
				ApplianceDualFuel appDF = new ApplianceDualFuel();
				StarsLiteFactory.setApplianceDualFuel( appDF, (LiteStarsAppDualFuel) liteApp );
				appDF.setSwitchOverTypeID( new Integer(updateApp.getDualFuel().getSwitchOverType().getEntryID()) );
				appDF.setSecondaryEnergySourceID( new Integer(updateApp.getDualFuel().getSecondaryEnergySource().getEntryID()) );
				appDF.setSecondaryKWCapacity( new Integer(updateApp.getDualFuel().getSecondaryKWCapacity()) );
				
				if (liteApp.isExtended())
					appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.UPDATE, appDF).execute();
				else
					appDF = (ApplianceDualFuel) Transaction.createTransaction(Transaction.INSERT, appDF).execute();
				StarsLiteFactory.setLiteAppDualFuel( (LiteStarsAppDualFuel) liteApp, appDF );
			}
			else if (updateApp.getGenerator() != null && liteApp instanceof LiteStarsAppGenerator) {
				ApplianceGenerator appGen = new ApplianceGenerator();
				StarsLiteFactory.setApplianceGenerator( appGen, (LiteStarsAppGenerator) liteApp );
				appGen.setTransferSwitchTypeID( new Integer(updateApp.getGenerator().getTransferSwitchType().getEntryID()) );
				appGen.setTransferSwitchMfgID( new Integer(updateApp.getGenerator().getTransferSwitchManufacturer().getEntryID()) );
				appGen.setPeakKWCapacity( new Integer(updateApp.getGenerator().getPeakKWCapacity()) );
				appGen.setFuelCapGallons( new Integer(updateApp.getGenerator().getFuelCapGallons()) );
				appGen.setStartDelaySeconds( new Integer(updateApp.getGenerator().getStartDelaySeconds()) );
				
				if (liteApp.isExtended())
					appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.UPDATE, appGen).execute();
				else
					appGen = (ApplianceGenerator) Transaction.createTransaction(Transaction.INSERT, appGen).execute();
				StarsLiteFactory.setLiteAppGenerator( (LiteStarsAppGenerator) liteApp, appGen );
			}
			else if (updateApp.getGrainDryer() != null && liteApp instanceof LiteStarsAppGrainDryer) {
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
			else if (updateApp.getStorageHeat() != null && liteApp instanceof LiteStarsAppStorageHeat) {
				ApplianceStorageHeat appSH = new ApplianceStorageHeat();
				StarsLiteFactory.setApplianceStorageHeat( appSH, (LiteStarsAppStorageHeat) liteApp );
				appSH.setStorageTypeID( new Integer(updateApp.getStorageHeat().getStorageType().getEntryID()) );
				appSH.setPeakKWCapacity( new Integer(updateApp.getStorageHeat().getPeakKWCapacity()) );
				appSH.setHoursToRecharge( new Integer(updateApp.getStorageHeat().getHoursToRecharge()) );
				
				if (liteApp.isExtended())
					appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.UPDATE, appSH).execute();
				else
					appSH = (ApplianceStorageHeat) Transaction.createTransaction(Transaction.INSERT, appSH).execute();
				StarsLiteFactory.setLiteAppStorageHeat( (LiteStarsAppStorageHeat) liteApp, appSH );
			}
			else if (updateApp.getHeatPump() != null && liteApp instanceof LiteStarsAppHeatPump) {
				ApplianceHeatPump appHP = new ApplianceHeatPump();
				StarsLiteFactory.setApplianceHeatPump( appHP, (LiteStarsAppHeatPump) liteApp );
				appHP.setPumpTypeID( new Integer(updateApp.getHeatPump().getPumpType().getEntryID()) );
				appHP.setStandbySourceID( new Integer(updateApp.getHeatPump().getStandbySource().getEntryID()) );
				appHP.setSecondsDelayToRestart( new Integer(updateApp.getHeatPump().getRestartDelaySeconds()) );
				
				if (liteApp.isExtended())
					appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.UPDATE, appHP).execute();
				else
					appHP = (ApplianceHeatPump) Transaction.createTransaction(Transaction.INSERT, appHP).execute();
				StarsLiteFactory.setLiteAppHeatPump( (LiteStarsAppHeatPump) liteApp, appHP );
			}
			else if (updateApp.getIrrigation() != null && liteApp instanceof LiteStarsAppIrrigation) {
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
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Appliance information updated successfully");
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the appliance information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
