package com.cannontech.web.stars.dr.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.search.SearchResult;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramService;
import com.cannontech.user.YukonUserContext;

@Controller
@RequestMapping("/applianceCategory/*")
public class ApplianceCategoryController {
    private ProgramService programService;

    public static enum IconOption {
        NO_IMAGE,
        AC,
        DUAL_FUEL,
        ELECTRIC,
        GENERATION,
        GRAIN_DRYER,
        HEAT_PUMP,
        HOT_TUB,
        IRRIGATION,
        LOAD,
        POOL,
        SETBACK,
        STORAGE_HEAT,
        WATER_HEATER,
        OTHER_IMAGE
    }

    @RequestMapping
    public String edit(ModelMap model, YukonUserContext userContext,
            int applianceCategoryId) {
        SearchResult<Program> assignedPrograms =
            programService.filterPrograms(applianceCategoryId);
        model.addAttribute("assignedPrograms", assignedPrograms);
        return "stars/dr/admin/applianceCategory/edit.jsp";
    }

    @RequestMapping
    public String editDetails(ModelMap model) {
        model.addAttribute("iconOptions", IconOption.values());
        return "stars/dr/admin/applianceCategory/editDetails.jsp";
    }

    @RequestMapping
    public String editAssignedProgram() {
        return "stars/dr/admin/applianceCategory/editAssignedProgram.jsp";
    }

    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
}
