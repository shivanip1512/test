package com.cannontech.web.stars.dr.operator.validator;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;

public interface OptOutValidatorFactory {
    
    public OptOutValidator getOptOutValidator(YukonUserContext userContext, 
                                              boolean isOperator,
                                              AccountInfoFragment accountInfoFragment);

}
