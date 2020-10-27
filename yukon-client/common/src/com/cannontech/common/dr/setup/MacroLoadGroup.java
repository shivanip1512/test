package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.db.macro.GenericMacro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "id" }, allowGetters = true)
public class MacroLoadGroup implements DBPersistentConverter<LMGroup> {

    private Integer id;
    private String name;
    private PaoType type;
    private List<LMPaoDto> assignedLoadGroups;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public List<LMPaoDto> getAssignedLoadGroups() {
        return assignedLoadGroups;
    }

    public void setAssignLoadGroups(List<LMPaoDto> assignedLoadGroups) {
        this.assignedLoadGroups = assignedLoadGroups;
    }

    @Override
    public void buildModel(LMGroup loadGroup) {

        MacroGroup macroGroup = (MacroGroup) loadGroup;
        List<LMPaoDto> assignedLoadGroups = new ArrayList<>();
        for (GenericMacro genericMacro : macroGroup.getMacroGroupVector()) {
            LMPaoDto lmPaoDto = new LMPaoDto();
            lmPaoDto.setId(genericMacro.getChildID());
            assignedLoadGroups.add(lmPaoDto);
        }
        setName(loadGroup.getPAOName());
        setId(loadGroup.getPAObjectID());
        setType(loadGroup.getPaoType());
        setAssignLoadGroups(assignedLoadGroups);
    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        Integer childOrder = 1;
        Vector<GenericMacro> macroGroupVector = new Vector<>();
        for (LMPaoDto dto : getAssignedLoadGroups()) {
            GenericMacro genericMacroMapping = new GenericMacro();

            genericMacroMapping.setOwnerID(getId());
            genericMacroMapping.setChildID(dto.getId());
            genericMacroMapping.setMacroType("GROUP");
            genericMacroMapping.setChildOrder(childOrder);
            childOrder++;

            macroGroupVector.add(genericMacroMapping);
        }
        ((MacroGroup) group).setMacroGroupVector(macroGroupVector);
        ((MacroGroup) group).setPAOName(getName());
    }

}
