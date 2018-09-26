package com.cannontech.web.stars.rtu.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class RtuDnpValidationUtil extends ValidationUtils {
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache dbCache;

    public void validateName(RtuDnp rtuDnp, Errors errors, boolean isCopyOperation) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", rtuDnp.getName(), 60);
            if (!errors.hasFieldErrors("name")) {
                if (!PaoUtils.isValidPaoName(rtuDnp.getName())) {
                    errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
                }
                if (!errors.hasFieldErrors("name")) {
                    boolean idSpecified = rtuDnp.getId() != null;

                    boolean nameAvailable = paoDao.isNameAvailable(rtuDnp.getName(), rtuDnp.getPaoType());

                    if (!nameAvailable) {
                        if (!idSpecified || isCopyOperation) {
                            // For create, we must have an available name
                            errors.rejectValue("name", "yukon.web.error.nameConflict");
                        } else {
                            // For edit, we can use our own existing name
                            LiteYukonPAObject existingPao = dbCache.getAllPaosMap().get(rtuDnp.getId());
                            if (!existingPao.getPaoName().equals(rtuDnp.getName())) {
                                errors.rejectValue("name", "yukon.web.error.nameConflict");
                            }
                        }
                    }
                }
            }
        }
    }

    public void validateSlaveAddress(RtuDnp rtuDnp, Errors errors) {

        DeviceAddress deviceAddress = rtuDnp.getDeviceAddress();
        if (!errors.hasFieldErrors("deviceAddress.slaveAddress")) {
            YukonValidationUtils.checkRange(errors, "deviceAddress.slaveAddress", deviceAddress.getSlaveAddress(), 0,
                65535, true);
        }
    }

}
