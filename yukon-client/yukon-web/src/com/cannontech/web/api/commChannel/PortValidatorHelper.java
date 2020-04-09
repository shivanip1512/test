package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

public class PortValidatorHelper {

    @Autowired private PaoDao paoDao;

    private final static String key = "yukon.web.api.error.";

    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (fieldValue == null || !StringUtils.hasText(fieldValue.toString())) {
            errors.rejectValue(field, key + "required", new Object[] { fieldName }, "");
        }
    }

    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        checkIfFieldRequired("name", errors, paoName, fieldName);

        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable("portId");
            // Check if pao name already exists
            if (type != null && (paoId == null || !(paoDao.getYukonPAOName(Integer.valueOf(paoId)).equalsIgnoreCase(paoName)))) {
                LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
                if (unique != null) {
                    errors.rejectValue("name", key + "unique", new Object[] { fieldName }, "");
                }
            }
        }
    }

    public void validatePortTimingFields(Errors errors, PortTiming timing) {
    // TODO : Add validation code here.
    }
}
