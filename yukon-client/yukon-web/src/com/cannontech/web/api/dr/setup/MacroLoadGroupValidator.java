package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class MacroLoadGroupValidator extends SimpleValidator<MacroLoadGroup> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache dbCache;

    public MacroLoadGroupValidator() {
        super(MacroLoadGroup.class);
    }

    @Override
    protected void doValidation(MacroLoadGroup loadGroup, Errors errors) {
        // Type
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "assignedLoadGroups", key + "required",
            new Object[] { "Assigned LoadGroup" });

        // Type
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", key + "required", new Object[] { "Type" });

        // Group Name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "required", new Object[] { "Group Name" });

        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", loadGroup.getName(), 60);
        }

        if (!errors.hasFieldErrors("name")) {
            String id = ServletUtils.getPathVariable("id");
            Integer paoId = null;
            if (id != null) {
                paoId = Integer.valueOf(id);
            }
            // Check if a load group with this name already exists
            if (loadGroup.getType() != null
                && (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(paoId), loadGroup.getName())))) {
                LiteYukonPAObject unique = paoDao.findUnique(loadGroup.getName(), loadGroup.getType());
                if (unique != null) {
                    errors.rejectValue("name", key + "groupName.unique");
                }
            }
        }

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(loadGroup.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!errors.hasFieldErrors("assignedLoadGroups")) {
            if (loadGroup.getAssignedLoadGroups().isEmpty()) {
                errors.rejectValue("assignedLoadGroups", key + "assignedLoadGroup.required");
            }
        }
        if (!errors.hasFieldErrors("type")) {
            if (loadGroup.getType() != PaoType.MACRO_GROUP) {
                errors.rejectValue("type", key + "type.invalid");
            }
        }

    }

}
