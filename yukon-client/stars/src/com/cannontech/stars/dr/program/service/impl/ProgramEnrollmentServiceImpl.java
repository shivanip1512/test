package com.cannontech.stars.dr.program.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.service.ControlHistoryService;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.dr.util.ControlGroupUtil;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.user.YukonUserContext;

public class ProgramEnrollmentServiceImpl implements ProgramEnrollmentService {
    private static final Logger log = YukonLogManager.getLogger(ProgramEnrollmentServiceImpl.class);
    private ProgramDao programDao;
    private EnrollmentDao enrollmentDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ControlHistoryService controlHistoryService;
    private ECMappingDao ecMappingDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;

    @Override
    public ProgramEnrollmentResultEnum applyEnrollmentRequests(final CustomerAccount customerAccount,
            final List<ProgramEnrollment> requests, final YukonUserContext yukonUserContext) {

        final int customerAccountId = customerAccount.getAccountId();
        LiteYukonUser user = yukonUserContext.getYukonUser();

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(customerAccount);
        LiteStarsCustAccountInformation liteCustomerAccount = 
            starsCustAccountInformationDao.getById(customerAccountId, 
                                                   energyCompany.getEnergyCompanyID());

        String progEnrBefore = toProgramNameString(liteCustomerAccount.getPrograms(), "(None)");

        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = Boolean.parseBoolean(trackHwAddr);

        StarsInventories starsInvs = new StarsInventories();
        StarsOperation operation = null;
        
        try {
            operation = createStarsOperation(customerAccount, requests, energyCompany, liteCustomerAccount);
            StarsProgramSignUp programSignUp = operation.getStarsProgramSignUp();

            List<LiteStarsLMHardware> hwsToConfig = 
                ProgramSignUpAction.updateProgramEnrollment(programSignUp,
                                                            liteCustomerAccount,
                                                            null,
                                                            energyCompany,
                                                            user);

            // Send out the config/disable command
            for (final LiteStarsLMHardware liteHw : hwsToConfig) {
                boolean toConfig = UpdateLMHardwareConfigAction.isToConfig(liteHw, liteCustomerAccount);

                if (toConfig) {
                    // Send the reenable command if hardware status is unavailable,
                    // whether to send the config command is controlled by the AUTOMATIC_CONFIGURATION role property
                    if (!useHardwareAddressing
                            && (StarsUtils.isOperator(user) && DaoFactory.getAuthDao().checkRoleProperty( user, ConsumerInfoRole.AUTOMATIC_CONFIGURATION )
                                    || StarsUtils.isResidentialCustomer(user) && DaoFactory.getAuthDao().checkRoleProperty(user, ResidentialCustomerRole.AUTOMATIC_CONFIGURATION))) {
                        YukonSwitchCommandAction.sendConfigCommand( energyCompany, liteHw, false, null );
                    }
                    else if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
                        YukonSwitchCommandAction.sendEnableCommand( energyCompany, liteHw, null );
                    }
                } else {
                    // Send disable command to hardware
                    YukonSwitchCommandAction.sendDisableCommand(energyCompany, liteHw, null );
                }

              StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteHw, energyCompany);
              starsInvs.addStarsInventory( starsInv );
            }
        } catch (InvalidParameterException e) {
            log.error(e);
            return ProgramEnrollmentResultEnum.NOT_CONFIGURED_CORRECTLY;
        } catch (WebClientException e2) {
            log.error(e2);
            return ProgramEnrollmentResultEnum.FAILURE;
        }

        // Log activity
        String progEnrNow = toProgramNameString(liteCustomerAccount.getPrograms(), "(Not Enrolled)");

        final StringBuilder sb = new StringBuilder();
        sb.append("Program Enrolled Before: ");
        sb.append(progEnrBefore);
        sb.append("; Now: ");
        sb.append(progEnrNow);
        String logMessage = sb.toString();

        ActivityLogger.logEvent(user.getUserID(),
                                customerAccountId,
                                energyCompany.getLiteID(),
                                customerAccount.getCustomerId(),
                                ActivityLogActions.PROGRAM_ENROLLMENT_ACTION,
                                logMessage);
        
        ProgramEnrollmentResultEnum result = (useHardwareAddressing) ?
                ProgramEnrollmentResultEnum.SUCCESS_HARDWARE_CONFIG : ProgramEnrollmentResultEnum.SUCCESS;
        return result;
    }
    
    private StarsOperation createStarsOperation(CustomerAccount customerAccount,
            List<ProgramEnrollment> requests, LiteStarsEnergyCompany energyCompany,
            LiteStarsCustAccountInformation liteCustomerAccount) {

        final StarsProgramSignUp programSignUp = new StarsProgramSignUp();
        programSignUp.setEnergyCompanyID(energyCompany.getEnergyCompanyID());
        programSignUp.setAccountNumber(customerAccount.getAccountNumber());

        StarsSULMPrograms programs = new StarsSULMPrograms();
        for (final ProgramEnrollment request : requests) {
            SULMProgram program = new SULMProgram();
            program.setProgramID(request.getProgramId());
            program.setApplianceCategoryID(request.getApplianceCategoryId());
            if (request.getLmGroupId() != 0) {
                program.setAddressingGroupID(request.getLmGroupId());
            } else {
                program.setAddressingGroupID(ServerUtils.ADDRESSING_GROUP_NOT_FOUND);
            }
            program.setInventoryID(request.getInventoryId());
            program.setLoadNumber(request.getRelay());
            programs.addSULMProgram(program);
        }
        programSignUp.setStarsSULMPrograms(programs);

        /*Going to need to do some guesswork since consumers aren't allowed
         * to choose load groups. 
         * --If the program has more than one group, we will take the first one in the list.  Could be
         * A DANGEROUS ASSUMPTION.  TODO: Track groups better.
         * --At this point, we will need to require that the switch or stat has been configured or enrolled 
         * previously from the operator side.  If it has not, there may not be a groupID set.
         */
        for (int j = 0; j < programSignUp.getStarsSULMPrograms().getSULMProgramCount(); j++) {
            SULMProgram program = programSignUp.getStarsSULMPrograms().getSULMProgram(j);
            int groupId = program.getAddressingGroupID();
            if (groupId == ServerUtils.ADDRESSING_GROUP_NOT_FOUND) {
                int programId = program.getProgramID();
                LiteLMProgramWebPublishing webProg = energyCompany.getProgram(programId);
                List<ProgramEnrollment> activeProgramEnrollments = enrollmentDao.getActiveEnrollmentByAccountId(customerAccount.getAccountId());
                
                int grpID = webProg.getGroupIDs()[0];
                for (ProgramEnrollment programEnrollment : activeProgramEnrollments) {
                    if (programEnrollment.getProgramId() == webProg.getProgramID() && 
                        programEnrollment.getApplianceCategoryId() == webProg.getApplianceCategoryID()) {
                        grpID = programEnrollment.getLmGroupId();
                    }
                }
                
                if (grpID > 0) {
                    programSignUp.getStarsSULMPrograms().getSULMProgram(j).setAddressingGroupID(grpID);
                } else {
                    throw new InvalidParameterException("Program not defined correctly");
                }
            }
        }

        StarsOperation operation = new StarsOperation();
        operation.setStarsProgramSignUp(programSignUp);
        return operation;
    }

    private String toProgramNameString(List<LiteStarsLMProgram> programs, String defaultValue) {
        if (programs == null || programs.isEmpty()) return defaultValue;
        final StringBuilder sb = new StringBuilder();
        for (final LiteStarsLMProgram program : programs) {
            String programName = StarsUtils.getPublishedProgramName(program.getPublishedProgram());
            sb.append(programName);
            sb.append(",");
        }
        String result = StringUtils.chop(sb.toString());
        return result;
    }

    @Override
    public void removeNonEnrolledPrograms(final List<Program> programs, 
            final Map<Integer, List<ControlHistory>> controlHistoryMap) {
        Validate.notNull(programs, "programs parameter cannot be null");
        Validate.notNull(controlHistoryMap, "controlHistoryMap parameter cannot be null");

        final List<Program> removeList = new ArrayList<Program>(0);

        for (final Program program : programs) {
            List<ControlHistory> controlHistoryList = controlHistoryMap.get(program.getProgramId());
            boolean containsOnlyNotEnrolledHistory = controlHistoryService.containsOnlyNotEnrolledHistory(controlHistoryList);
            if (containsOnlyNotEnrolledHistory) removeList.add(program);
        }

        programs.removeAll(removeList);
    }

    @Override
    public boolean isProgramEnrolled(final int customerAccountId, final int inventoryId, final int programId) {
        final List<LMHardwareControlGroup> entryList = new ArrayList<LMHardwareControlGroup>();
        final List<Integer> groupIds = programDao.getGroupIdsByProgramId(programId);

        for (final Integer groupId : groupIds) {
            List<LMHardwareControlGroup> enrolledEntryList =
                lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(inventoryId,
                                                                                        groupId,
                                                                                        customerAccountId,
                                                                                        LMHardwareControlGroup.ENROLLMENT_ENTRY);
            entryList.addAll(enrolledEntryList);
        }

        boolean isProgramEnrolled = ControlGroupUtil.isEnrolled(entryList, new Date());
        return isProgramEnrolled;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setControlHistoryService(
            ControlHistoryService controlHistoryService) {
        this.controlHistoryService = controlHistoryService;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

}
