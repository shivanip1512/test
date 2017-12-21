package com.cannontech.web.stars.service;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.login.model.PasswordResetInfo;
import com.cannontech.user.YukonUserContext;

public interface PasswordResetService {
    
    /**
     * @param forgottenPasswordField - This can be an account number, email address, or username.
     */
    public PasswordResetInfo getPasswordResetInfo(String forgottenPasswordString);

    /**
     * This method will send out an email to the user to reset their password.  The passwordResetKey is a unique identifier the 
     * user can use to unlock the page to reset their password.
     */
    public void sendPasswordResetEmail(String forgottenPasswordResetUrl, LiteContact liteContract, YukonUserContext userContext);

    /**
     * This method creates the URL needed to reset a user's password.
     * 
     * @param username - the user for which the password reset URL is to be generated.
     * @param request - the HttpServletRequest
     * @param useYukonExternalUrl - when false, {@code request} will be used to construct the urlBase directly.<br>
     *          Most cases will use false, such that a simple redirect to the same urlBase is sufficient.
     *          Example: Password Reset<br>
     *          When true, urlBase will attempt to be resolved to an externally available URL.
     *          See {@link WebserverUrlResolver#getUrl(String)}. Example: Forgot Password (email sent with link)
     */
    public String getPasswordResetUrl(String username, HttpServletRequest request, boolean useYukonExternalUrl);
    
    /**
     * This method uses the supplied user and generates a password key that can be used to reset the user's password.
     */
    public String getPasswordKey(LiteYukonUser user);
    
    /**
     * This method uses the password key to get the user associated with it.
     */
    public LiteYukonUser findUserFromPasswordKey(String passwordKey);

    /**
     * This method takes a password key and invalidates it so the key can no longer be used to reset the password.
     */
    public void invalidatePasswordKey(String passwordKey);

}