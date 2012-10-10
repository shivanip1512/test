package com.cannontech.web.stars.dr.operator.validator;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface LoginValidatorFactory {
    
    public LoginUsernameValidator getUsernameValidator(LiteYukonUser liteYukonUser);

    public LoginPasswordValidator getPasswordValidator(LiteYukonUser liteYukonUser);

}
