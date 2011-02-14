package com.cannontech.web.admin.energyCompany.applianceCategory;

import org.apache.commons.lang.Validate;

import com.cannontech.stars.dr.appliance.model.AssignedProgram;

/**
 * This class is essentially a simple wrapper around AssignedProgram for use
 * as a backing bean.
 * 
 * If the program being edited is virtual, programIds must be empty and
 * assignedProgram.programId must be 0.
 * 
 * If multiple programs are being edited:
 *     - none of them can be virtual and virtual must be false
 *     - programIds must list the programs being edited
 *     - assignedProgram.programId is ignored
 */
public class AssignProgramBackingBean {
    private boolean virtual;
    private boolean multiple;

    // ProgramIds is only used in "multiple" mode.  When editing a single
    // program, the program id in the assigned program is sufficient.
    private Integer[] programIds;

    private AssignedProgram assignedProgram;

    public AssignProgramBackingBean() {
        assignedProgram = new AssignedProgram();
    }

    public AssignProgramBackingBean(boolean virtual, boolean multiple,
            Integer[] programIds, AssignedProgram assignedProgram) {
        this.virtual = virtual;
        this.multiple = multiple;
        this.programIds = programIds;
        this.assignedProgram = assignedProgram;

        // double check a few things that should never happen
        Validate.isTrue(!virtual || !multiple,
                        "multiple and virtual cannot both be true");
        if (virtual) {
            Validate.isTrue((programIds == null || programIds.length == 0)
                            && assignedProgram.getProgramId() == 0,
                            "cannot specify program ids for virtual programs");
            return;
        }
        if (multiple) {
            Validate.isTrue(programIds != null && programIds.length > 1,
                            "must have more than one program id for multiple");
            Validate.isTrue(assignedProgram.getProgramId() == 0,
                            "cannot specify single program id for multiple");
        } else {
            Validate.isTrue(programIds == null || programIds.length == 0,
                            "cannot specify multiple program ids if not multiple");
            Validate.isTrue(assignedProgram.getProgramId() > 0,
                            "single program ids required if not multiple");
        }
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public Integer[] getProgramIds() {
        return programIds;
    }

    public void setProgramIds(Integer[] programIds) {
        this.programIds = programIds;
    }

    public AssignedProgram getAssignedProgram() {
        return assignedProgram;
    }
}
