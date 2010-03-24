package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentInventory;
import com.cannontech.stars.dr.displayable.model.DisplayableEnrollment.DisplayableEnrollmentProgram;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.HardwareConfigAction;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/operator/enrollment/*")
public class OperatorEnrollmentController {
    private DisplayableEnrollmentDao displayableEnrollmentDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private PaoDao paoDao;
    private LoadGroupDao loadGroupDao;
    private AssignedProgramDao assignedProgramDao;
    private EnrollmentHelperService enrollmentHelperService;

    @RequestMapping
    public String list(ModelMap model, YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment,
                                                      model);

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
    public String edit(ModelMap model, int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment,
                                                      model);

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);
        model.addAttribute("assignedProgram", assignedProgram);
        List<DisplayablePao> loadGroups =
            loadGroupDao.getForProgram(assignedProgram.getProgramId());
        model.addAttribute("loadGroups", loadGroups);

        DisplayableEnrollmentProgram displayableEnrollmentProgram =
            displayableEnrollmentDao.getProgram(
                accountInfoFragment.getAccountId(), assignedProgramId);

        model.addAttribute("programEnrollment",
                           new ProgramEnrollment(displayableEnrollmentProgram));

        Map<Integer, DisplayableEnrollmentInventory> inventoryById = Maps.newHashMap();
        for (DisplayableEnrollmentInventory item
                : displayableEnrollmentProgram.getInventory()) {
            inventoryById.put(item.getInventoryId(), item);
        }
        model.addAttribute("inventoryById", inventoryById);

        return "operator/enrollment/edit.jsp";
    }

    @RequestMapping
    public String save(ModelMap model, int assignedProgramId,
            @ModelAttribute ProgramEnrollment programEnrollment,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {

        /*
        for (ProgramEnrollment.InventoryEnrollment enrollment
                : programEnrollment.getInventoryEnrollments()) {
            EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
            enrollmentHelper.setAccountNumber(accountInfoFragment.getAccountNumber());
            enrollmentHelper.setProgramName(programName)
            enrollmentHelper.setApplianceCategoryName(applianceCategoryName)
            enrollmentHelper.setLoadGroupName(loadGroupName)
            enrollmentHelper.setRelay(enrollment.getRelay());
            EnrollmentEnum enrollmentAction = enrollment.isEnrolled()
                ? EnrollmentEnum.ENROLL : EnrollmentEnum.UNENROLL;
            enrollmentHelperService.doEnrollment(enrollmentHelper,
                                                 enrollmentAction,
                                                 userContext.getYukonUser());
        }
        */

        return closeDialog(model);
    }

    private String closeDialog(ModelMap model) {
        model.addAttribute("popupId", "peDialog");
        return "closePopup.jsp";
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
    public void setEnrollmentHelperService(
            EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
}
