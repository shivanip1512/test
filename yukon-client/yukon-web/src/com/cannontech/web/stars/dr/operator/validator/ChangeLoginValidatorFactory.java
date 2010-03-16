package com.cannontech.web.stars.dr.operator.validator;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface ChangeLoginValidatorFactory {
    
    public ChangeLoginValidator getChangeLoginValidator(LiteYukonUser residentialUser, YukonUserContext userContext);

}
