package com.cannontech.web.stars.dr.operator.validator;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface LoginValidatorFactory {
    
    public LoginValidator getLoginValidator(LiteYukonUser residentialUser);

}
