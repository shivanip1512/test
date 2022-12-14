package com.cannontech.web.stars.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.stars.service.SwitchContextService;

public class SwitchContextServiceImpl implements SwitchContextService {
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private LoginService loginService;
    @Autowired private EnergyCompanyDao ecDao;

    @Override
    public void switchContext(final StarsYukonUser user, final HttpServletRequest request, 
            final HttpSession session, final int memberID) throws WebClientException {

        if (memberID == user.getEnergyCompanyID()) {
            return;
        }
        
        LiteStarsEnergyCompany energyCompany = this.starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());
        LiteStarsEnergyCompany member = this.starsDatabaseCache.getEnergyCompany(memberID);
        
        List<Integer> loginIDs = energyCompany.getMemberLoginIDs();
        for (int i = 0; i < loginIDs.size(); i++) {
            LiteYukonUser liteUser = this.yukonUserDao.getLiteYukonUser(loginIDs.get(i).intValue());
            if (liteUser == null) {
                continue;
            }
            
            if (ecDao.getEnergyCompany(liteUser).getId() == memberID) {
                if (loginService.internalLogin(request, session, liteUser.getUsername(), true) == null) {
                    throw new WebClientException("The member login is no longer valid");
                }
                
                request.getSession().setAttribute( ServletUtils.ATT_CONTEXT_SWITCHED, "true" );
                return;
            }
        }
        
        throw new WebClientException( "No member login assigned to '" + member.getName() + "'" );
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
    
}
