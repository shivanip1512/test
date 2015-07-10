package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentException;
import com.cannontech.stars.dr.enrollment.exception.EnrollmentSystemConfigurationException;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/operator/enrollment/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_ENROLLMENT)
public class OperatorEnrollmentController {
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private DisplayableEnrollmentDao displayableEnrollmentDao;
    @Autowired private LMHardwareControlGroupDao controlGroupDao;
    @Autowired private PaoDao paoDao;
    @Autowired private LoadGroupDao loadGroupDao;
    @Autowired private StaticLoadGroupMappingDao staticLoadGroupMappingDao;
    @Autowired private AssignedProgramDao assignedProgramDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private EnrollmentHelperService enrollmentHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private EnergyCompanySettingDao ecSettingDao;

    /**
     * The main operator "enrollment" page. Lists all current enrollments and
     * has icons for adding, removing and editing these enrollments.
     */
    @RequestMapping("list")
    public String list(ModelMap model, AccountInfoFragment account) {

        AccountInfoFragmentHelper.setupModelMapBasics(account, model);
        
        int accountId = account.getAccountId();
        List<DisplayableEnrollmentProgram> enrollmentPrograms = displayableEnrollmentDao.findEnrolledPrograms(accountId);
        model.addAttribute("enrollmentPrograms", enrollmentPrograms);
        
        List<Integer> alreadyEnrolled = new ArrayList<>();
        for (DisplayableEnrollmentProgram enrollment : enrollmentPrograms) {
            alreadyEnrolled.add(enrollment.getProgram().getProgramId());
        }
        model.addAttribute("alreadyEnrolled", alreadyEnrolled);
        
        Set<Integer> loadGroupIds = Sets.newHashSet();
        for (DisplayableEnrollmentProgram enrollmentProgram : enrollmentPrograms) {
            loadGroupIds.addAll(enrollmentProgram.getLoadGroupIds());
        }
        Map<Integer, String> loadGroupNames = paoDao.getYukonPAONames(loadGroupIds);
        model.addAttribute("loadGroupNames", loadGroupNames);

        List<HardwareConfigAction> hardwareConfigActions = controlGroupDao.getHardwareConfigActions(accountId);
        model.addAttribute("hardwareConfigActions", hardwareConfigActions);
        model.addAttribute("energyCompanyId", account.getEnergyCompanyId());

        return "operator/enrollment/list.jsp";
    }

    @RequestMapping("history")
    public String history(ModelMap model, AccountInfoFragment accountInfoFragment) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        int accountId = accountInfoFragment.getAccountId();
        List<HardwareConfigAction> hardwareConfigActions = controlGroupDao.getHardwareConfigActions(accountId);
        model.addAttribute("hardwareConfigActions", hardwareConfigActions);
        
        return "operator/enrollment/history.jsp";
    }

    /**
     * Initiate a new enrollment. This is called after the program has been
     * chosen via a picker. Does the same thing as editing an enrollment with
     * different wording ("Add Enrollment" instead of "Edit Enrollment").
     */
    @RequestMapping("add")
    public String add(ModelMap model, int assignedProgramId, LiteYukonUser user, AccountInfoFragment account) {
        return edit(model, assignedProgramId, user, account, true);
    }

    /**
     * Edit a program enrollment.  Displays a dialog to the user allowing them
     * to change load group used and hardware used for the selected program.
     */
    @RequestMapping("edit")
    public String edit(ModelMap model, int assignedProgramId, LiteYukonUser user, AccountInfoFragment account) {
        return edit(model, assignedProgramId, user, account, false);
    }

    private String edit(ModelMap model, 
            int assignedProgramId,
            LiteYukonUser user,
            AccountInfoFragment account, 
            boolean isAdd) {

        validateAccountEditing(user);

        DisplayableEnrollmentProgram displayable = 
            displayableEnrollmentDao.getProgram(account.getAccountId(), assignedProgramId);
        
        model.addAttribute("programEnrollment", new ProgramEnrollment(displayable));

        AccountInfoFragmentHelper.setupModelMapBasics(account, model);

        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);
        List<LoadGroup> loadGroups = null;
        
        EnergyCompany ec = ecDao.getEnergyCompanyByAccountId(account.getAccountId());
        model.addAttribute("energyCompanyId", ec.getId());
        boolean trackHardwareAddressingEnabled =
                ecSettingDao.getBoolean(EnergyCompanySettingType.TRACK_HARDWARE_ADDRESSING, ec.getId());

        String batchedSwitchCommandToggle =
            globalSettingDao.getString(GlobalSettingType.BATCHED_SWITCH_COMMAND_TOGGLE);
        boolean useStaticLoadGroups =
            StarsUtils.BATCH_SWITCH_COMMAND_MANUAL.equals(batchedSwitchCommandToggle)
                && VersionTools.staticLoadGroupMappingExists();
        if (useStaticLoadGroups) {
            List<Integer> loadGroupIds =
                staticLoadGroupMappingDao.getLoadGroupIdsForApplianceCategory(assignedProgram.getApplianceCategoryId());
            loadGroups = loadGroupDao.getByIds(loadGroupIds);
        } else if (!trackHardwareAddressingEnabled) {
            loadGroups = loadGroupDao.getByProgramId(assignedProgram.getProgramId());
        }
        model.addAttribute("loadGroups", loadGroups);

        Map<Integer, DisplayableEnrollmentInventory> inventoryById = Maps.newHashMap();
        for (DisplayableEnrollmentInventory item : displayable.getInventory()) {
            inventoryById.put(item.getInventoryId(), item);
        }
        model.addAttribute("inventoryById", inventoryById);
        model.addAttribute("isAdd", isAdd);

        return "operator/enrollment/edit.jsp";
    }

    @RequestMapping("confirmSave")
    public String confirmSave(ModelMap model, 
            int assignedProgramId,
            boolean isAdd, 
            @ModelAttribute ProgramEnrollment programEnrollment,
            LiteYukonUser user,
            AccountInfoFragment account) {

        AccountInfoFragmentHelper.setupModelMapBasics(account, model);
        validateAccountEditing(user);
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);

        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> conflictingEnrollments =
            getConflictingEnrollments(account.getAccountId(), assignedProgramId, user);

        // For confirmation, we just need a list of conflicting programs.
        Set<Integer> conflictingAssignedProgramIds = Sets.newHashSet();
        for (com.cannontech.stars.dr.program.service.ProgramEnrollment enrollment : conflictingEnrollments) {
            conflictingAssignedProgramIds.add(enrollment.getAssignedProgramId());
        }
        
        List<AssignedProgram> conflictingPrograms = assignedProgramDao.getByIds(conflictingAssignedProgramIds);
        model.addAttribute("conflictingPrograms", conflictingPrograms);
        model.addAttribute("isAdd", isAdd);

        return "operator/enrollment/save.jsp";
    }

    @RequestMapping("save")
    public void save(HttpServletResponse resp,
            int assignedProgramId, 
            boolean isAdd,
            @ModelAttribute ProgramEnrollment programEnrollment,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, 
            FlashScope flashScope) {
        
        String saveTypeKey = isAdd ? "enrollCompleted" : "enrollmentUpdated";
        save(assignedProgramId, programEnrollment, saveTypeKey, userContext, accountInfoFragment, flashScope);
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private void save(int assignedProgramId,
            ProgramEnrollment programEnrollment,
            String saveTypeKey,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment,
            FlashScope flashScope) {
        
        // Log enrollment/unenrollment attempts
        accountEventLogService.enrollmentModificationAttempted(userContext.getYukonUser(), 
                                                               accountInfoFragment.getAccountNumber(),
                                                               EventSource.OPERATOR);
        LiteYukonUser user = userContext.getYukonUser();
        validateAccountEditing(user);
        
        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> programEnrollments = Lists.newArrayList();
        programEnrollments.addAll(programEnrollment.makeProgramEnrollments(assignedProgram.getApplianceCategoryId(), assignedProgramId));
        programEnrollments.addAll(getConflictingEnrollments(accountInfoFragment.getAccountId(),
                                                            assignedProgramId, user));
        try {
            enrollmentHelper.updateProgramEnrollments(programEnrollments, accountInfoFragment.getAccountId(), userContext);
            
            String msgKey = "yukon.web.modules.operator.enrollmentList." + saveTypeKey;
            MessageSourceResolvable message = new YukonMessageSourceResolvable(msgKey, assignedProgram.getDisplayName());
            flashScope.setConfirm(message);   
        } catch (EnrollmentSystemConfigurationException e) {
            //This error will happen if the communication medium was not setup in Yukon.
            //The device will have been enrolled in Yukon, just not communicated with.
            //As far as I know it is only possible in Zigbee integrations
            MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
            String reason = messageSourceAccessor.getMessage(e.getKey());
            
            String msgKey = "yukon.web.modules.operator.enrollmentList.withError." + saveTypeKey;
            MessageSourceResolvable message = new YukonMessageSourceResolvable(msgKey, assignedProgram.getDisplayName(),reason);
            flashScope.setWarning(message);
        } catch (EnrollmentException e2) {
            String msgKey = "yukon.web.modules.operator.enrollmentList.failed";
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            MessageSourceResolvable message = new YukonMessageSourceResolvable(msgKey, assignedProgram.getDisplayName(), accessor.getMessage(e2.getKey()));
            flashScope.setError(message);
        }

    }

    @RequestMapping("confirmUnenroll")
    public String confirmUnenroll(ModelMap model, int assignedProgramId, LiteYukonUser user) {
        
        validateAccountEditing(user);

        AssignedProgram assignedProgram = assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);

        return "operator/enrollment/unenroll.jsp";
    }

    @RequestMapping("unenroll")
    public void unenroll(HttpServletResponse resp,
            ModelMap model, 
            int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment account, 
            FlashScope flashScope) {
        
        DisplayableEnrollmentProgram displayable =
            displayableEnrollmentDao.getProgram(account.getAccountId(), assignedProgramId);
        ProgramEnrollment pe = new ProgramEnrollment(displayable);
        model.addAttribute("programEnrollment", pe);

        for (ProgramEnrollment.InventoryEnrollment enrollment : pe.getInventoryEnrollments()) {
            enrollment.setEnrolled(false);
        }

        save(assignedProgramId, pe, "unenrollCompleted", userContext, account, flashScope);
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }

    private List<com.cannontech.stars.dr.program.service.ProgramEnrollment> getConflictingEnrollments(
            int accountId, 
            int assignedProgramId, 
            LiteYukonUser user) {
        
        boolean multiplePerCategory =
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY, user);

        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> conflictingEnrollments = Lists.newArrayList();
        if (!multiplePerCategory) {
            // Only one program per appliance category is allowed.  Find other
            // programs in the same appliance category and make sure they
            // aren't enrolled.
            conflictingEnrollments = enrollmentDao.findConflictingEnrollments(accountId, assignedProgramId);
        }

        return conflictingEnrollments;
    }

    private void validateAccountEditing(LiteYukonUser user) {
        boolean allowEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, user);
        Validate.isTrue(allowEditing, "Account editing not allowed by this user.");
    } 
}