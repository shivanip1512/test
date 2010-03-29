package com.cannontech.web.stars.dr.operator.validator;

import com.cannontech.user.YukonUserContext;

public interface OptOutValidatorFactory {
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext);

}
