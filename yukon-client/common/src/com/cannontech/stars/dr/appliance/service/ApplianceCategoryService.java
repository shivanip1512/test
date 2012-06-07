package com.cannontech.stars.dr.appliance.service;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;
import com.cannontech.user.YukonUserContext;

public interface ApplianceCategoryService {
    public void save(ApplianceCategory applianceCategory,
            YukonUserContext userContext);

    public void delete(int applianceCategoryId, YukonUserContext userContext);

    public void assignProgram(AssignedProgram assignedProgram,
            YukonUserContext userContext);

    public void unassignProgram(int applianceCategoryId, int assignedProgramId,
            YukonUserContext userContext);

    public void moveAssignedProgramUp(int applianceCategory,
            int assignedProgramId, YukonUserContext userContext);

    public void moveAssignedProgramDown(int applianceCategory,
            int assignedProgramId, YukonUserContext userContext);
}
