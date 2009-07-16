package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.appliance.ApplianceAirConditioner;
import com.cannontech.database.db.stars.appliance.ApplianceDualFuel;
import com.cannontech.database.db.stars.appliance.ApplianceGenerator;
import com.cannontech.database.db.stars.appliance.ApplianceGrainDryer;
import com.cannontech.database.db.stars.appliance.ApplianceHeatPump;
import com.cannontech.database.db.stars.appliance.ApplianceIrrigation;
import com.cannontech.database.db.stars.appliance.ApplianceStorageHeat;
import com.cannontech.database.db.stars.appliance.ApplianceWaterHeater;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteAppliance;
import com.cannontech.stars.xml.serialize.StarsDeleteApplianceResponse;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
public class DeleteApplianceAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsDeleteAppliance delApp = new StarsDeleteAppliance();
			delApp.setApplianceID( Integer.parseInt(req.getParameter("AppID")) );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteAppliance( delApp );
			
			return SOAPUtil.buildSOAPMessage( operation );
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
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			StarsDeleteAppliance delApp = reqOper.getStarsDeleteAppliance();
        	
			LiteStarsAppliance liteApp = null;
			boolean unenrollProgram = false;
        	
			for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
				LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
				if (lApp.getApplianceID() == delApp.getApplianceID()) {
					liteApp = lApp;
					break;
				}
			}
			if (liteApp == null) {
			    for (LiteStarsAppliance lApp : liteAcctInfo.getUnassignedAppliances()) {
	                if (lApp.getApplianceID() == delApp.getApplianceID()) {
                        liteApp = lApp;
                        break;
                    }
			    }
			}
			if (liteApp == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the appliance information") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
        	
			if (liteApp.getProgramID() > 0) {
				unenrollProgram = false;
				for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
					LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
					if (lApp.getApplianceCategoryID() == liteApp.getApplianceCategoryID() &&
						lApp.getInventoryID() == liteApp.getInventoryID() &&
						lApp.getProgramID() == liteApp.getProgramID() &&
						lApp.getAddressingGroupID() == liteApp.getAddressingGroupID()) {
						
						unenrollProgram = true;
						break;
					}
				}
			}
        	
			if (liteApp.getAirConditioner() != null) {
				ApplianceAirConditioner app = new ApplianceAirConditioner();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getWaterHeater() != null) {
				ApplianceWaterHeater app = new ApplianceWaterHeater();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getDualFuel() != null) {
				ApplianceDualFuel app = new ApplianceDualFuel();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getGenerator() != null) {
				ApplianceGenerator app = new ApplianceGenerator();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getGrainDryer() != null) {
				ApplianceGrainDryer app = new ApplianceGrainDryer();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getStorageHeat() != null) {
				ApplianceStorageHeat app = new ApplianceStorageHeat();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getHeatPump() != null) {
				ApplianceHeatPump app = new ApplianceHeatPump();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
			else if (liteApp.getIrrigation() != null) {
				ApplianceIrrigation app = new ApplianceIrrigation();
				app.setApplianceID( new Integer(liteApp.getApplianceID()) );
				Transaction.createTransaction(Transaction.DELETE, app).execute();
			}
        	
    		
			if (unenrollProgram) {
				LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );

                LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
                LoadGroup loadGroup = loadGroupDao.getById(liteApp.getAddressingGroupID());
                String loadGroupName = loadGroup.getLoadGroupName();

                ProgramDao programDao = YukonSpringHook.getBean("starsProgramDao", ProgramDao.class);
                Program program = programDao.getByProgramId(liteApp.getProgramID());
                String programName = program.getProgramName();

                LMHardwareBaseDao lmHardwareBaseDao = YukonSpringHook.getBean("hardwareBaseDao", LMHardwareBaseDao.class);
                LMHardwareBase hardwareBase = lmHardwareBaseDao.getById(liteApp.getInventoryID());
                String serialNumber = hardwareBase.getManufacturerSerialNumber(); 
                
                EnrollmentHelper enrollmentHelper = new EnrollmentHelper(liteAcctInfo.getCustomerAccount().getAccountNumber(),
                                                                         loadGroupName, 
                                                                         programName, 
                                                                         serialNumber);
                
                
                EnrollmentHelperService enrollmentHelperService = YukonSpringHook.getBean("enrollmentService", EnrollmentHelperService.class);
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, user.getYukonUser());
				
				StarsDeleteApplianceResponse resp = new StarsDeleteApplianceResponse();
				resp.setStarsLMPrograms( StarsLiteFactory.createStarsLMPrograms(liteAcctInfo, energyCompany) );
				respOper.setStarsDeleteApplianceResponse( resp );
			}
			else {
				StarsSuccess success = new StarsSuccess();
				success.setDescription("Appliance deleted successfully");
				respOper.setStarsSuccess( success );
			}
            
			com.cannontech.database.data.stars.appliance.ApplianceBase app = new com.cannontech.database.data.stars.appliance.ApplianceBase();
			app.setApplianceID( new Integer(delApp.getApplianceID()) );
			Transaction.createTransaction(Transaction.DELETE, app).execute();
			
			liteAcctInfo.getAppliances().remove( liteApp );
			
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot delete the appliance") );
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
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsDeleteAppliance delApp = reqOper.getStarsDeleteAppliance();
			
			StarsAppliances appliances = accountInfo.getStarsAppliances();
			for (int i = 0; i < appliances.getStarsApplianceCount(); i++) {
				StarsAppliance app = appliances.getStarsAppliance(i);
				if (app.getApplianceID() == delApp.getApplianceID()) {
					appliances.removeStarsAppliance(i);
					break;
				}
			}
			
			StarsDeleteApplianceResponse resp = operation.getStarsDeleteApplianceResponse();
			if (resp != null)
				accountInfo.setStarsLMPrograms( resp.getStarsLMPrograms() );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
