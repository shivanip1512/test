package com.cannontech.web.stars.dr.consumer;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEventLoggingData;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.WebSecurityChecker;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT)
@Controller
public class EnrollmentController extends AbstractConsumerController {
    private AccountEventLogService accountEventLogService;
    private DisplayableEnrollmentDao displayableEnrollmentDao;
    private EnrollmentDao enrollmentDao;
    private EnrollmentHelperService enrollmentHelperService;
    private WebSecurityChecker webSecurityChecker;
    
    @RequestMapping(value = "/consumer/enrollment", method = RequestMethod.GET)
    public String view(@ModelAttribute CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        List<DisplayableEnrollment> enrollments = 
            displayableEnrollmentDao.find(customerAccount.getAccountId());
        map.addAttribute("enrollments", enrollments);
        
        return "consumer/enrollment/enrollment.jsp";
    }

    @RequestMapping(value = "/consumer/enrollment/enroll", method=RequestMethod.GET)
    public String enroll(ModelMap model,
            @ModelAttribute CustomerAccount customerAccount,
            int assignedProgramId, HttpSession session,
            YukonUserContext userContext) {
        DisplayableEnrollmentProgram displayableEnrollmentProgram = 
            displayableEnrollmentDao.getFilteredDisplayableEnrollmentProgram(customerAccount.getAccountId(), assignedProgramId);

        boolean perDeviceEnrollment =
            rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_ENROLLMENT_PER_DEVICE,
                                          userContext.getYukonUser());
        if (perDeviceEnrollment && displayableEnrollmentProgram.getInventory().size() > 1) {
            model.addAttribute("displayableEnrollmentProgram", displayableEnrollmentProgram);
            return "consumer/enrollment/selectHardware.jsp";
        }

        List<ProgramEnrollment> updatedEnrollments = Lists.newArrayList();
        for (DisplayableEnrollmentInventory enrollment : displayableEnrollmentProgram.getInventory()) {
            ProgramEnrollment programEnrollment =
                makeProgramEnrollment(displayableEnrollmentProgram, enrollment, true);
            updatedEnrollments.add(programEnrollment);

            // Log Attempt
            EnrollmentEventLoggingData eventLoggingData = enrollmentHelperService.getEventLoggingData(programEnrollment);
            
            accountEventLogService.enrollmentAttemptedByConsumer(userContext.getYukonUser(), 
                                                                 customerAccount.getAccountNumber(), 
                                                                 eventLoggingData.getManufacturerSerialNumber(), 
                                                                 eventLoggingData.getProgramName(), 
                                                                 eventLoggingData.getLoadGroupName());

        }


        
        return saveChanges(model, assignedProgramId,
                           customerAccount.getAccountId(), updatedEnrollments,
                           session, userContext);
    }

    @RequestMapping(value = "/consumer/enrollment/enrollSelectedHardware", method=RequestMethod.POST)
    public String enrollSelectedHardware(ModelMap model,
            @ModelAttribute CustomerAccount customerAccount,
            int assignedProgramId, Integer[] inventoryIds, HttpSession session,
            YukonUserContext userContext) {
        webSecurityChecker.checkRoleProperty(YukonRoleProperty.RESIDENTIAL_ENROLLMENT_PER_DEVICE);

        DisplayableEnrollmentProgram displayableEnrollmentProgram =
            displayableEnrollmentDao.getProgram(
                customerAccount.getAccountId(), assignedProgramId);
        if (inventoryIds == null || inventoryIds.length == 0) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.dr.consumer.selectHardware.atLeastOneItem");
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("displayableEnrollmentProgram", displayableEnrollmentProgram);
            return "consumer/enrollment/selectHardware.jsp";
        }

        Set<Integer> enrolledInventoryIds = Sets.newHashSet(inventoryIds);

        List<ProgramEnrollment> updatedEnrollments = Lists.newArrayList();
        for (DisplayableEnrollmentInventory enrollment : displayableEnrollmentProgram.getInventory()) {
            ProgramEnrollment programEnrollment =
                makeProgramEnrollment(displayableEnrollmentProgram, enrollment,
                                      enrolledInventoryIds.contains(enrollment.getInventoryId()));
            updatedEnrollments.add(programEnrollment);
        }

        return saveChanges(model, assignedProgramId,
                           customerAccount.getAccountId(), updatedEnrollments,
                           session, userContext);
    }

    @RequestMapping(value = "/consumer/enrollment/unenroll", method=RequestMethod.GET)
    public String unenroll(ModelMap model,
            @ModelAttribute CustomerAccount customerAccount,
            int assignedProgramId, HttpSession session,
            YukonUserContext userContext) {
        DisplayableEnrollmentProgram displayableEnrollmentProgram =
            displayableEnrollmentDao.getProgram(
                customerAccount.getAccountId(), assignedProgramId);

        List<ProgramEnrollment> updatedEnrollments = Lists.newArrayList();
        for (DisplayableEnrollmentInventory enrollment : displayableEnrollmentProgram.getInventory()) {
            ProgramEnrollment programEnrollment =
                makeProgramEnrollment(displayableEnrollmentProgram, enrollment, false);
            updatedEnrollments.add(programEnrollment);

            // Log Attempt
            EnrollmentEventLoggingData eventLoggingData = enrollmentHelperService.getEventLoggingData(programEnrollment);
            
            accountEventLogService.unenrollmentAttemptedByConsumer(userContext.getYukonUser(), 
                                                                   customerAccount.getAccountNumber(),
                                                                   eventLoggingData.getManufacturerSerialNumber(), 
                                                                   eventLoggingData.getProgramName(), 
                                                                   eventLoggingData.getLoadGroupName());
        }

        
        return saveChanges(model, assignedProgramId,
                           customerAccount.getAccountId(), updatedEnrollments,
                           session, userContext);
    }

    private ProgramEnrollment makeProgramEnrollment(
            DisplayableEnrollmentProgram displayableEnrollmentProgram,
            DisplayableEnrollmentInventory enrollment, boolean isEnrolled) {
        ProgramEnrollment retVal = new ProgramEnrollment();
        retVal.setApplianceCategoryId(displayableEnrollmentProgram.getApplianceCategory().getApplianceCategoryId());
        retVal.setAssignedProgramId(displayableEnrollmentProgram.getProgram().getProgramId());
        retVal.setRelay(enrollment.getRelay());
        retVal.setInventoryId(enrollment.getInventoryId());
        retVal.setEnroll(isEnrolled);
        retVal.setLmGroupId(displayableEnrollmentProgram.getLoadGroupId());
        return retVal;
    }

    private String saveChanges(ModelMap model, int assignedProgramId,
            int accountId, List<ProgramEnrollment> updatedEnrollments, HttpSession session,
            YukonUserContext userContext) {
        List<ProgramEnrollment> programEnrollments = Lists.newArrayList();
        
        programEnrollments.addAll(updatedEnrollments);
        programEnrollments.addAll(getConflictingEnrollments(model, accountId, assignedProgramId, userContext));
        enrollmentHelperService.updateProgramEnrollments(programEnrollments, accountId, userContext);

        model.addAttribute("enrollmentResult", ProgramEnrollmentResultEnum.SUCCESS);

        EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(),
                                 EventUtils.EVENT_CATEGORY_ACCOUNT,
                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED,
                                 accountId,
                                 session);

        return "consumer/enrollment/enrollmentResult.jsp";
    }

    private List<ProgramEnrollment> getConflictingEnrollments(ModelMap model,
            int accountId, int assignedProgramId, YukonUserContext userContext) {
        boolean multiplePerCategory = rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY,
                                                                    userContext.getYukonUser());

        List<ProgramEnrollment> conflictingEnrollments = Lists.newArrayList();
        if (!multiplePerCategory) {
            // Only one program per appliance category is allowed. Find other
            // programs in the same appliance category and make sure they
            // aren't enrolled.
            conflictingEnrollments = enrollmentDao.findConflictingEnrollments(accountId,
                                                                              assignedProgramId);
        }

        return conflictingEnrollments;
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setDisplayableEnrollmentDao(DisplayableEnrollmentDao displayableEnrollmentDao) {
        this.displayableEnrollmentDao = displayableEnrollmentDao;
    }

    @Autowired
    public void setWebSecurityChecker(WebSecurityChecker webSecurityChecker) {
        this.webSecurityChecker = webSecurityChecker;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
}
