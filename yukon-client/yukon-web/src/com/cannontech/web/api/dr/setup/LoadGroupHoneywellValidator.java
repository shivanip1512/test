package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.HoneywellProcessingException;
import com.cannontech.database.db.device.lm.LMGroupHoneywell;

@Service
public class LoadGroupHoneywellValidator extends LoadGroupSetupValidator<LoadGroupBase> {

    public LoadGroupHoneywellValidator() {
        super(LoadGroupBase.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupBase.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupBase loadGroup, Errors errors) {
        // Checks is maximum limit of HONEYWELL GROUP is exceeded or not
        if (loadGroup.getType() == PaoType.LM_GROUP_HONEYWELL && LMGroupHoneywell.isMaximumGroupLimitExceeded()) {
            throw new HoneywellProcessingException("Exceeded maximum limit of HONEYWELL GROUP.");
        }
    }
}
