package com.cannontech.web.stars.dr.operator.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;

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
    public Integer createResidentialLogin(LoginBackingBean loginBackingBean, LiteYukonUser user, int accountId, int energyCompanyId);

    /**
     * This method updates an existing residential login.
     * @param loginBackingBean
     * @param userContext
     * @param residentialUser
     * @param energyCompanyId
     */
    void updateResidentialLogin(LoginBackingBean loginBackingBean, YukonUserContext userContext, LiteYukonUser residentialUser, int energyCompanyId);

}