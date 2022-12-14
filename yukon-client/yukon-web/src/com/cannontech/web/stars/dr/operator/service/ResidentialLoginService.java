package com.cannontech.web.stars.dr.operator.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.login.model.Login;

public interface ResidentialLoginService {

    /**
     * This method creates a brand new residential login.
     * 
     * @param loginBackingBean
     * @param user
     * @param accountId
     * @param energyCompanyId
     * @return the yukonuser id that was created
     */
    public Integer createResidentialLogin(Login loginBackingBean, LiteYukonUser user, int accountId, int energyCompanyId);

    /**
     * This method updates the username and login group for an existing residential login
     * @param loginBackingBean
     * @param userContext
     * @param residentialUser
     * @param energyCompanyId
     */
    void updateResidentialLogin(Login loginBackingBean, YukonUserContext userContext, LiteYukonUser residentialUser, int energyCompanyId);

    /**
     * This method updates the password for an existing residential login
     * @param loginBackingBean
     * @param userContext
     * @param residentialUser
     */
    void updateResidentialPassword(Login loginBackingBean, YukonUserContext userContext,
                                   LiteYukonUser residentialUser);

}