package com.cannontech.web.stars.dr.operator.hardware;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.dr.appliance.dao.AssignedProgramDao;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;

@Controller
@RequestMapping("/operator/hardware/config/*")
public class OperatorHardwareConfigController {
    private DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao;
    private InventoryDao inventoryDao;
    private LoadGroupDao loadGroupDao;
    private AssignedProgramDao assignedProgramDao;

    @RequestMapping
    public String list(ModelMap model, int inventoryId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment,
                                                      model);

        populateModel(model, inventoryId, userContext);

        List<DisplayableInventoryEnrollment> enrollments =
            displayableInventoryEnrollmentDao.find(accountInfoFragment.getAccountId(),
                                                   inventoryId);
        model.addAttribute("enrollments", enrollments);

        return "operator/hardware/config/list.jsp";
    }

    @RequestMapping
    public String edit(ModelMap model, int inventoryId, int assignedProgramId,
            YukonUserContext userContext,
            AccountInfoFragment accountInfoFragment) {
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment,
                                                      model);
        populateModel(model, inventoryId, userContext);

        AssignedProgram assignedProgram =
            assignedProgramDao.getById(assignedProgramId);

        DisplayableInventoryEnrollment enrollment =
            displayableInventoryEnrollmentDao.find(accountInfoFragment.getAccountId(),
                                                   inventoryId, assignedProgramId);
        if (enrollment == null) {
            enrollment =
                new DisplayableInventoryEnrollment(assignedProgramId,
                                                   assignedProgram.getProgramId(),
                                                   assignedProgram.getName());
        }
        model.addAttribute("enrollment", enrollment);

        List<DisplayablePao> loadGroups =
            loadGroupDao.getForProgram(enrollment.getProgramId());
        model.addAttribute("loadGroups", loadGroups);

        return "operator/hardware/config/edit.jsp";
    }

    public void populateModel(ModelMap model, int inventoryId,
            YukonUserContext userContext) {
        model.addAttribute("inventoryId", inventoryId);

        HardwareSummary hardware = inventoryDao.findHardwareSummaryById(inventoryId);
        model.addAttribute("hardware", hardware);
    }

    @Autowired
    public void setDisplayableInventoryEnrollmentDao(
            DisplayableInventoryEnrollmentDao displayableInventoryEnrollmentDao) {
        this.displayableInventoryEnrollmentDao = displayableInventoryEnrollmentDao;
    }

    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Autowired
    public void setLoadGroupDao(LoadGroupDao loadGroupDao) {
        this.loadGroupDao = loadGroupDao;
    }
    
    @Autowired
    public void setAssignedProgramDao(AssignedProgramDao assignedProgramDao) {
        this.assignedProgramDao = assignedProgramDao;
    }
}
