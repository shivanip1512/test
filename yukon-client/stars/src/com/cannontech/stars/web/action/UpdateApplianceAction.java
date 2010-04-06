package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.appliance.service.StarsApplianceService;
import com.cannontech.stars.util.EventUtils;
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
import com.cannontech.stars.xml.serialize.Chiller;
import com.cannontech.stars.xml.serialize.DryerType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.DualStageAC;
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
			if (appliance == null) {
	            appliances = accountInfo.getUnassignedStarsAppliances();
	            for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
	                if (appliances.getStarsAppliance(i).getApplianceID() == appID) {
	                    appliance = appliances.getStarsAppliance(i);
	                    break;
	                }
	            }
			}

			if (appliance == null) return null;
			
			StarsAppliance updateApp = (StarsAppliance)
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
					updateApp.setKwCapacity( Double.parseDouble(req.getParameter("KWCapacity")) );
				else
					updateApp.deleteKWCapacity();
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("KWCapacity") + "' for KW capacity");
			}
			try {
				if (req.getParameter("EffRating").length() > 0)
					updateApp.setEfficiencyRating( Double.parseDouble(req.getParameter("EffRating")) );
				else
					updateApp.deleteEfficiencyRating();
			}
			catch (NumberFormatException e) {
				throw new WebClientException("Invalid number format '" + req.getParameter("EffRating") + "' for efficiency rating");
			}
			
			updateApp.setManufacturer( (Manufacturer)StarsFactory.newStarsCustListEntry(
					DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("Manufacturer")) ),
					Manufacturer.class) );
			
			updateApp.setLocation( (Location)StarsFactory.newStarsCustListEntry(
					DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("Location")) ),
					Location.class) );
			
			
			switch (updateApp.getApplianceCategory().getApplianceType()) {
			    case AIR_CONDITIONER:
			        AirConditioner ac = updateApp.getAirConditioner();
			        ac.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
			                                                                   DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Tonnage")) ),
			                                                                   Tonnage.class) );
			        ac.setAcType( (ACType)StarsFactory.newStarsCustListEntry(
			                                                                 DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Type")) ),
			                                                                 ACType.class) );
			        break;

			    case DUAL_STAGE:
			        DualStageAC dualStageAC = updateApp.getDualStageAC();
			        dualStageAC.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
			                                                                   DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Tonnage")) ),
			                                                                   Tonnage.class) );
			        dualStageAC.setStageTwoTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
			                                                                           DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Tonnage_StageTwo")) ),
			                                                                           Tonnage.class) );
			        dualStageAC.setAcType( (ACType)StarsFactory.newStarsCustListEntry(
			                                                                 DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Type")) ),
			                                                                 ACType.class) );
			        break;

			    case CHILLER:
			        Chiller chill = updateApp.getChiller();
			        chill.setTonnage( (Tonnage)StarsFactory.newStarsCustListEntry(
			                                                                      DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Tonnage")) ),
			                                                                      Tonnage.class) );
			        chill.setAcType( (ACType)StarsFactory.newStarsCustListEntry(
			                                                                    DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("AC_Type")) ),
			                                                                    ACType.class) );
			        break;
			        
			    case WATER_HEATER:
			        WaterHeater wh = updateApp.getWaterHeater();
			        wh.setNumberOfGallons( (NumberOfGallons)StarsFactory.newStarsCustListEntry(
			                                                                                   DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("WH_GallonNum")) ),
			                                                                                   NumberOfGallons.class) );
			        wh.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
			                                                                             DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("WH_EnergySrc")) ),
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

			        break;
			        
			    case DUAL_FUEL:
			        DualFuel df = updateApp.getDualFuel();
			        df.setSwitchOverType( (SwitchOverType)StarsFactory.newStarsCustListEntry(
			                                                                                 DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("DF_SwitchOverType")) ),
			                                                                                 SwitchOverType.class) );
			        df.setSecondaryEnergySource( (SecondaryEnergySource)StarsFactory.newStarsCustListEntry(
			                                                                                               DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("DF_SecondarySrc")) ),
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
			        
			        break;
			        
			    case GENERATOR:
			        Generator gen = updateApp.getGenerator();
			        gen.setTransferSwitchType( (TransferSwitchType)StarsFactory.newStarsCustListEntry(
			                                                                                          DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GEN_TranSwitchType")) ),
			                                                                                          TransferSwitchType.class) );
			        gen.setTransferSwitchManufacturer( (TransferSwitchManufacturer)StarsFactory.newStarsCustListEntry(
			                                                                                                          DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GEN_TranSwitchMfg")) ),
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
			        break;
			        
			    case GRAIN_DRYER:
			        GrainDryer gd = updateApp.getGrainDryer();
			        gd.setDryerType( (DryerType)StarsFactory.newStarsCustListEntry(
			                                                                       DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GD_DryerType")) ),
			                                                                       DryerType.class) );
			        gd.setBinSize( (BinSize)StarsFactory.newStarsCustListEntry(
			                                                                   DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GD_BinSize")) ),
			                                                                   BinSize.class) );
			        gd.setBlowerEnergySource( (BlowerEnergySource)StarsFactory.newStarsCustListEntry(
			                                                                                         DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerEnergySrc")) ),
			                                                                                         BlowerEnergySource.class) );
			        gd.setBlowerHorsePower( (BlowerHorsePower)StarsFactory.newStarsCustListEntry(
			                                                                                     DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerHorsePower")) ),
			                                                                                     BlowerHorsePower.class) );
			        gd.setBlowerHeatSource( (BlowerHeatSource)StarsFactory.newStarsCustListEntry(
			                                                                                     DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("GD_BlowerHeatSrc")) ),
			                                                                                     BlowerHeatSource.class) );
			        break;

			    case STORAGE_HEAT:
			        StorageHeat sh = updateApp.getStorageHeat();
			        sh.setStorageType( (StorageType)StarsFactory.newStarsCustListEntry(
			                                                                           DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("SH_StorageType")) ),
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
			        
			        break;
			        
			    case HEAT_PUMP:
			        HeatPump hp = updateApp.getHeatPump();
			        hp.setPumpType( (PumpType)StarsFactory.newStarsCustListEntry(
			                                                                     DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("HP_PumpType")) ),
			                                                                     PumpType.class) );
			        hp.setPumpSize( (PumpSize)StarsFactory.newStarsCustListEntry(
			                                                                     DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("HP_PumpSize")) ),
			                                                                     PumpSize.class) );
			        hp.setStandbySource( (StandbySource)StarsFactory.newStarsCustListEntry(
			                                                                               DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("HP_StandbySrc")) ),
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
			        
			        break;
			        
			    case IRRIGATION:
			        Irrigation irr = updateApp.getIrrigation();
			        irr.setIrrigationType( (IrrigationType)StarsFactory.newStarsCustListEntry(
			                                                                                  DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_IrrigationType")) ),
			                                                                                  IrrigationType.class) );
			        irr.setHorsePower( (HorsePower)StarsFactory.newStarsCustListEntry(
			                                                                          DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_HorsePower")) ),
			                                                                          HorsePower.class) );
			        irr.setEnergySource( (EnergySource)StarsFactory.newStarsCustListEntry(
			                                                                              DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_EnergySrc")) ),
			                                                                              EnergySource.class) );
			        irr.setSoilType( (SoilType)StarsFactory.newStarsCustListEntry(
			                                                                      DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_SoilType")) ),
			                                                                      SoilType.class) );
			        irr.setMeterLocation( (MeterLocation)StarsFactory.newStarsCustListEntry(
			                                                                                DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_MeterLoc")) ),
			                                                                                MeterLocation.class) );
			        irr.setMeterVoltage( (MeterVoltage)StarsFactory.newStarsCustListEntry(
			                                                                              DaoFactory.getYukonListDao().getYukonListEntry( Integer.parseInt(req.getParameter("IRR_MeterVolt")) ),
			                                                                              MeterVoltage.class) );
			        break;
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
			StarsAppliance updateApp = reqOper.getStarsUpdateAppliance();
    		
			StarsApplianceService starsApplianceService = YukonSpringHook.getBean("starsApplianceService", StarsApplianceService.class);
            starsApplianceService.updateStarsAppliance(updateApp, user.getEnergyCompanyID());
            
			StarsSuccess success = new StarsSuccess();
			success.setDescription("Appliance information updated successfully");
            
			respOper.setStarsSuccess( success );
            
            EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, liteAcctInfo.getAccountID(), session);
            
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
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
}
