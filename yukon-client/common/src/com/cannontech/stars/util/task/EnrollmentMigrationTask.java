package com.cannontech.stars.util.task;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.hardware.dao.LMHardwareConfigurationDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.notification.DRNotificationMessagingService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.user.UserUtils;

public class EnrollmentMigrationTask extends TimeConsumingTask {

    LiteStarsEnergyCompany energyCompany = null;
    int userID = UserUtils.USER_NONE_ID;
    int numEnrolled = 0;

    public EnrollmentMigrationTask() {
    }

    public EnrollmentMigrationTask(LiteStarsEnergyCompany energyCompany) {
        this.energyCompany = energyCompany;
    }

    @Override
    public String getProgressMsg() {
        if (status != STATUS_NOT_INIT) {
            return getImportProgress();
        }
        return null;
    }

    @Override
    public void run() {

        status = STATUS_RUNNING;

        try {
            LMHardwareControlGroupDao lmHardwareControlGroupDao = (LMHardwareControlGroupDao) YukonSpringHook.getBean(LMHardwareControlGroupDao.class);
            LMHardwareConfigurationDao lmHardwareConfigurationDao = (LMHardwareConfigurationDao) YukonSpringHook.getBean(LMHardwareConfigurationDao.class);
            /*
             * only for the current energy company, this will save resources at
             * a place like Xcel where we don't want to load ALL accounts all at once
             */
            // TODO: Should pull the db transactions out of the loops. Will speed things up and is much cleaner.
            StarsCustAccountInformationDao starsCustAccountInformationDao = YukonSpringHook.getBean(StarsCustAccountInformationDao.class);
            InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean(InventoryBaseDao.class);
            EnergyCompanySettingDao energyCompanySettingDao = YukonSpringHook.getBean(EnergyCompanySettingDao.class);
            DRNotificationMessagingService drNotificationMessagingService = YukonSpringHook.getBean(DRNotificationMessagingService.class);

            boolean useHardwareAddressing = energyCompanySettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING,
                                                                               energyCompany.getEnergyCompanyId());

            List<LiteAccountInfo> custAcctInfoList = starsCustAccountInformationDao.getAll(energyCompany.getEnergyCompanyId());
            for (LiteAccountInfo liteAcctInformation : custAcctInfoList) {
                LiteAccountInfo extendedAcctInformation = energyCompany.limitedExtendCustAccountInfo(liteAcctInformation);
                List<LiteStarsAppliance> appList = extendedAcctInformation.getAppliances();
                for (LiteStarsAppliance app : appList) {
                    if (app.getProgramID() > 0 && app.getInventoryID() > 0 && app.getAccountID() > 0) {
                        LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(app.getInventoryID());

                        LMHardwareControlGroup controlGroup = new LMHardwareControlGroup();
                        controlGroup.setInventoryId(app.getInventoryID());
                        int groupId = app.getAddressingGroupID();
                        if (useHardwareAddressing) {
                            if (liteHw != null && liteHw.getLMConfiguration() != null) {
                                int[] grpIDs = LMControlHistoryUtil.getControllableGroupIDs(liteHw.getLMConfiguration(), app.getLoadNumber());
                                if (grpIDs != null && grpIDs.length >= 1) {
                                    groupId = grpIDs[0];
                                }
                            }
                        } else if (groupId == 0) {
                            ProgramDao programDao = YukonSpringHook.getBean(ProgramDao.class);
                            groupId = programDao.getLoadGroupIdForProgramId(app.getProgramID());
                        }
                        controlGroup.setLmGroupId(groupId);
                        controlGroup.setProgramId(app.getProgramID());
                        controlGroup.setRelay(app.getLoadNumber());
                        controlGroup.setAccountId(app.getAccountID());
                        controlGroup.setGroupEnrollStart(new Instant(liteHw.getInstallDate()));
                        controlGroup.setType(LMHardwareControlGroup.ENROLLMENT_ENTRY);
                        controlGroup.setUserIdFirstAction(energyCompany.getUser().getUserID());
                        LMHardwareControlGroup existingEnrollment = lmHardwareControlGroupDao.findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(app.getInventoryID(),
                                                                                                                                                         app.getProgramID(),
                                                                                                                                                         app.getAccountID());
                        if (existingEnrollment == null) {
                            lmHardwareControlGroupDao.add(controlGroup);
                            drNotificationMessagingService.sendEnrollmentNotification(controlGroup);
                            numEnrolled++;
                        }

                        // update appliance/config, if groupId is different
                        if (app.getAddressingGroupID() != groupId) {
                            app.setAddressingGroupID(groupId);
                            LMHardwareConfiguration config = new LMHardwareConfiguration(app.getInventoryID(),
                                                                                         app.getApplianceID(),
                                                                                         groupId,
                                                                                         app.getLoadNumber());
                            lmHardwareConfigurationDao.update(config);
                        }
                    }
                }
            }
            status = STATUS_FINISHED;
        } catch (Exception e) {
            if (status == STATUS_CANCELED) {
                errorMsg = "Operation was canceled by user";
            } else {
                CTILogger.error(e.getMessage(), e);
                status = STATUS_ERROR;
                errorMsg = e.getMessage();
            }
        }
    }

    private String getImportProgress() {
        return numEnrolled + " enrollments migrated successfully.";
    }
}