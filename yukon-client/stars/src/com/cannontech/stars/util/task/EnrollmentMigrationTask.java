package com.cannontech.stars.util.task;

import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.user.UserUtils;

public class EnrollmentMigrationTask extends TimeConsumingTask {

	LiteStarsEnergyCompany energyCompany = null;
    int userID = UserUtils.USER_DEFAULT_ID;
    
    int numEnrolled = 0;

	public EnrollmentMigrationTask() {
		
	}
	
	public EnrollmentMigrationTask (LiteStarsEnergyCompany energyCompany) {
		this.energyCompany = energyCompany;
	}

	@Override
    public String getProgressMsg() {
		if (status != STATUS_NOT_INIT) {
		    return getImportProgress();
		}
		
		return null;
	}
	
	public void run() {

		status = STATUS_RUNNING;
		
        try {
            LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean("lmHardwareControlGroupDao");
            /*
             * only for the current energy company, this will save resources at a place like Xcel where
             * we don't want to load ALL accounts all at once
			 */
            //TODO: Should pull the db transactions out of the loops.  Will speed things up and is much cleaner.
            StarsCustAccountInformationDao starsCustAccountInformationDao = 
                YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
            
            List<LiteStarsCustAccountInformation> custAcctInfoList = starsCustAccountInformationDao.getAll(energyCompany.getEnergyCompanyID());
            for(LiteStarsCustAccountInformation liteAcctInformation : custAcctInfoList) {
                LiteStarsCustAccountInformation extendedAcctInformation = energyCompany.limitedExtendCustAccountInfo(liteAcctInformation);
                List<LiteStarsAppliance> appList = extendedAcctInformation.getAppliances();
                for(LiteStarsAppliance app : appList) {
                    if(app.getProgramID() > 0 && app.getInventoryID() > 0 && app.getAccountID() > 0) {
                        LMHardwareControlGroup controlGroup = new LMHardwareControlGroup();
                        controlGroup.setInventoryId(app.getInventoryID());
                        int groupId = app.getAddressingGroupID();
                        if(groupId == 0)
                            groupId = InventoryUtils.getYukonLoadGroupIDFromSTARSProgramID(app.getProgramID());
                        controlGroup.setLmGroupId(groupId);
                        controlGroup.setRelay(app.getLoadNumber());
                        controlGroup.setAccountId(app.getAccountID());
                        LiteInventoryBase inventoryItem = energyCompany.getInventoryBrief(controlGroup.getInventoryId(), true);
                        controlGroup.setGroupEnrollStart(new Date(inventoryItem.getInstallDate()));
                        controlGroup.setType(LMHardwareControlGroup.ENROLLMENT_ENTRY);
                        controlGroup.setUserIdFirstAction(energyCompany.getUserID());
                        List<LMHardwareControlGroup> existingGroups = lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(controlGroup.getInventoryId(), groupId, app.getAccountID(), LMHardwareControlGroup.ENROLLMENT_ENTRY);
                        if(existingGroups.size() == 0) {
                            lmHardwareControlGroupDao.add(controlGroup);
                            numEnrolled++;
                        }
                    }
                }
            }
            
			status = STATUS_FINISHED;
		}
		catch (Exception e) 
        {
			if (status == STATUS_CANCELED) {
				errorMsg = "Operation was canceled by user";
			}
			else {
				CTILogger.error( e.getMessage(), e );
				status = STATUS_ERROR;

				errorMsg = e.getMessage();
			}
		}
	}
	
	private String getImportProgress() {
        return numEnrolled + " enrollments migrated successfully.";
	}
}
