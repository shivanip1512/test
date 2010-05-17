package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
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
    private DisplayableEnrollmentDao displayableEnrollmentDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private PaoDao paoDao;
    private LoadGroupDao loadGroupDao;
    private AssignedProgramDao assignedProgramDao;
    private EnrollmentDao enrollmentDao;
    private EnrollmentHelperService enrollmentHelperService;
    private RolePropertyDao rolePropertyDao;

    /**
     * The main operator "enrollment" page. Lists all current enrollments and
     * has icons for adding, removing and editing these enrollments.
     */
    @RequestMapping
    public String list(ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        int accountId = accountInfoFragment.getAccountId();
        List<DisplayableEnrollmentProgram> enrollmentPrograms =
            displayableEnrollmentDao.findEnrolledPrograms(accountId);

        model.addAttribute("enrollmentPrograms", enrollmentPrograms);
        Set<Integer> loadGroupIds = Sets.newHashSet();
        for (DisplayableEnrollmentProgram enrollmentProgram : enrollmentPrograms) {
            loadGroupIds.add(enrollmentProgram.getLoadGroupId());
        }
        Map<Integer, String> loadGroupNames = paoDao.getYukonPAONames(loadGroupIds);
        model.addAttribute("loadGroupNames", loadGroupNames);

        List<HardwareConfigAction> hardwareConfigActions =
            lmHardwareControlGroupDao.getHardwareConfigActions(accountId);
        model.addAttribute("hardwareConfigActions", hardwareConfigActions);

        return "operator/enrollment/list.jsp";
    }

    @RequestMapping
    public String history(ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        int accountId = accountInfoFragment.getAccountId();
        List<HardwareConfigAction> hardwareConfigActions =
            lmHardwareControlGroupDao.getHardwareConfigActions(accountId);
        model.addAttribute("hardwareConfigActions", hardwareConfigActions);
        return "operator/enrollment/history.jsp";
    }

    /**
     * Initiate a new enrollment. This is called after the program has been
     * chosen via a picker. Does the same thing as editing an enrollment with
     * different wording ("Add Enrollment" instead of "Edit Enrollment").
     */
    @RequestMapping
    public String add(ModelMap model, int assignedProgramId,
            HttpServletResponse response, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        return edit(model, assignedProgramId, response, userContext,
                    accountInfoFragment, true, flashScope);
    }

    /**
     * Edit a program enrollment.  Displays a dialog to the user allowing them
     * to change load group used and hardware used for the selected program.
     */
    @RequestMapping
    public String edit(ModelMap model, int assignedProgramId,
            HttpServletResponse response, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        return edit(model, assignedProgramId, response, userContext,
                    accountInfoFragment, false, flashScope);
    }

    private String edit(ModelMap model, int assignedProgramId,
            HttpServletResponse response, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, boolean isAdd,
            FlashScope flashScope) {

        validateAccountEditing(userContext);

        DisplayableEnrollmentProgram displayableEnrollmentProgram =
            displayableEnrollmentDao.getProgram(
                accountInfoFragment.getAccountId(), assignedProgramId);
        ProgramEnrollment programEnrollment = new ProgramEnrollment(displayableEnrollmentProgram);
        model.addAttribute("programEnrollment", programEnrollment);

        boolean autoConfiguration = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_AUTOMATIC_CONFIGURATION, userContext.getYukonUser());
        if (isAdd && autoConfiguration
                && programEnrollment.getInventoryEnrollments().size() == 1) {
            programEnrollment.getInventoryEnrollments().get(0).setEnrolled(true);
            JSONObject actionCommand = new JSONObject();
            actionCommand.append("action", "close");
            response.addHeader("X-JSON", actionCommand.toString());
            return save(model, assignedProgramId, isAdd, programEnrollment,
                        userContext, accountInfoFragment, flashScope);
        }

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);
        List<ControllablePao> loadGroups =
            loadGroupDao.getForProgram(assignedProgram.getProgramId());
        model.addAttribute("loadGroups", loadGroups);

        Map<Integer, DisplayableEnrollmentInventory> inventoryById = Maps.newHashMap();
        for (DisplayableEnrollmentInventory item
                : displayableEnrollmentProgram.getInventory()) {
            inventoryById.put(item.getInventoryId(), item);
        }
        model.addAttribute("inventoryById", inventoryById);
        model.addAttribute("isAdd", isAdd);

        return "operator/enrollment/edit.jsp";
    }

    @RequestMapping
    public String confirmSave(ModelMap model, int assignedProgramId,
            boolean isAdd, @ModelAttribute ProgramEnrollment programEnrollment,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        validateAccountEditing(userContext);
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);

        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> conflictingEnrollments =
            getConflictingEnrollments(model, accountInfoFragment.getAccountId(),
                                      assignedProgramId, userContext);
        // For confirmation, we just need a list of conflicting programs.
        Set<Integer> conflictingAssignedProgramIds = Sets.newHashSet();
        for (com.cannontech.stars.dr.program.service.ProgramEnrollment enrollment : conflictingEnrollments) {
            conflictingAssignedProgramIds.add(enrollment.getAssignedProgramId());
        }
        List<AssignedProgram> conflictingPrograms =
            assignedProgramDao.getByIds(conflictingAssignedProgramIds);
        model.addAttribute("conflictingPrograms", conflictingPrograms);
        model.addAttribute("isAdd", isAdd);

        return "operator/enrollment/save.jsp";
    }

    @RequestMapping
    public String save(ModelMap model, int assignedProgramId, boolean isAdd,
            @ModelAttribute ProgramEnrollment programEnrollment,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        return save(model, assignedProgramId, programEnrollment,
                    isAdd ? "enrollCompleted" : "enrollmentUpdated",
                            userContext, accountInfoFragment, flashScope);
    }

    private String save(ModelMap model, int assignedProgramId,
            @ModelAttribute ProgramEnrollment programEnrollment,
            String successKey,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment,
            FlashScope flashScope) {
        validateAccountEditing(userContext);
        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> programEnrollments = Lists.newArrayList();
        programEnrollments.addAll(programEnrollment.makeProgramEnrollments(assignedProgram.getApplianceCategoryId(), assignedProgramId));
        programEnrollments.addAll(getConflictingEnrollments(model, accountInfoFragment.getAccountId(),
                                                            assignedProgramId, userContext));
        enrollmentHelperService.updateProgramEnrollments(programEnrollments,
                                                         accountInfoFragment.getAccountId(),
                                                         userContext);

        String msgKey = "yukon.web.modules.operator.enrollmentList." + successKey;
        MessageSourceResolvable message =
            new YukonMessageSourceResolvable(msgKey, assignedProgram.getDisplayName());
        flashScope.setConfirm(message);
        return closeDialog(model);
    }

    @RequestMapping
    public String confirmUnenroll(ModelMap model, int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        validateAccountEditing(userContext);

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);

        return "operator/enrollment/unenroll.jsp";
    }

    @RequestMapping
    public String unenroll(ModelMap model, int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment, FlashScope flashScope) {
        DisplayableEnrollmentProgram displayableEnrollmentProgram =
            displayableEnrollmentDao.getProgram(
                accountInfoFragment.getAccountId(), assignedProgramId);
        ProgramEnrollment programEnrollment = new ProgramEnrollment(displayableEnrollmentProgram);
        model.addAttribute("programEnrollment", programEnrollment);

        for (ProgramEnrollment.InventoryEnrollment enrollment : programEnrollment.getInventoryEnrollments()) {
            enrollment.setEnrolled(false);
        }

        return save(model, assignedProgramId, programEnrollment,
                    "unenrollCompleted", userContext, accountInfoFragment,
                    flashScope);
    }

    private List<com.cannontech.stars.dr.program.service.ProgramEnrollment> getConflictingEnrollments(ModelMap model,
            int accountId, int assignedProgramId, YukonUserContext userContext) {
        boolean multiplePerCategory =
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY,
                                          userContext.getYukonUser());

        List<com.cannontech.stars.dr.program.service.ProgramEnrollment> conflictingEnrollments = Lists.newArrayList();
        if (!multiplePerCategory) {
            // Only one program per appliance category is allowed.  Find other
            // programs in the same appliance category and make sure they
            // aren't enrolled.
            conflictingEnrollments =
                enrollmentDao.findConflictingEnrollments(accountId,
                                                         assignedProgramId);
        }

        return conflictingEnrollments;
    }

    private String closeDialog(ModelMap model) {
        model.addAttribute("popupId", "peDialog");
        return "closePopup.jsp";
    }

    private void validateAccountEditing(YukonUserContext userContext) {
        Validate.isTrue(rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING,
                                                      userContext.getYukonUser()),
                                                      "Account editing not allowed by this user.");
    }

    @Autowired
    public void setDisplayableEnrollmentDao(
            DisplayableEnrollmentDao displayableEnrollmentDao) {
        this.displayableEnrollmentDao = displayableEnrollmentDao;
    }

    @Autowired
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }
    
    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setEnrollmentHelperService(
            EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
