package com.cannontech.web.api.dr.macro;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

@Service
public class MacroLoadGroupValidator extends SimpleValidator<MacroLoadGroup> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;

    public MacroLoadGroupValidator() {
        super(MacroLoadGroup.class);
    }

    @Override
    protected void doValidation(MacroLoadGroup loadGroup, Errors errors) {

        lmValidatorHelper.checkIfFieldRequired("type", errors, loadGroup.getType(), "Type");
        lmValidatorHelper.validateNewPaoName(loadGroup.getName(), loadGroup.getType(), errors, "Name");

        if (!errors.hasFieldErrors("type")) {
            if (loadGroup.getType() != PaoType.MACRO_GROUP) {
                errors.rejectValue("type", key + "type.invalid", new Object[] { "macro load group" }, "");
            }
        }

        if (!errors.hasFieldErrors("assignedLoadGroups")) {
            if (CollectionUtils.isEmpty(loadGroup.getAssignedLoadGroups())) {
                errors.rejectValue("assignedLoadGroups", key + "assignedLoadGroup.required");
            }
        }

        if (CollectionUtils.isNotEmpty(loadGroup.getAssignedLoadGroups())) {
            Set<Integer> duplicateLoadGroupsIds = getDuplicateLoadGroupsIds(loadGroup.getAssignedLoadGroups());
            if (CollectionUtils.isNotEmpty(duplicateLoadGroupsIds)) {
                errors.rejectValue("assignedLoadGroups",key + "assignedLoadGroup.duplicate.notAllowed", new Object[] { duplicateLoadGroupsIds },
                    "");
            }
        }
    }

    /**
     * Returns set of duplicate load group ids.
     */
    private Set<Integer> getDuplicateLoadGroupsIds(List<LMPaoDto> assignedLoadGroups) {
       List<Integer> groupIds =assignedLoadGroups.stream()
                                                 .map(LMPaoDto::getId)
                                                 .collect(Collectors.toList());
       return lmValidatorHelper.findDuplicates(groupIds);

    }

}
