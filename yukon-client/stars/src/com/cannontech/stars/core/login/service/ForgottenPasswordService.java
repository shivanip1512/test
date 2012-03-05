package com.cannontech.stars.core.login.service;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.user.YukonUserContext;

public interface ForgottenPasswordService {
    
    /**
     * @param forgottenPasswordField - This can be an account number, email address, or username.
     */
    public PasswordResetInfo getPasswordResetInfo(String forgottenPasswordString);

    /**
     * This method will send out an email to the user to reset their password.  The passwordResetKey is a unique identifier the 
     * user can use to unlock the page to reset their password.
     */
    public void sendPasswordResetEmail(String forgottenPasswordResetUrl, LiteContact liteContract, YukonUserContext userContext);
    
}