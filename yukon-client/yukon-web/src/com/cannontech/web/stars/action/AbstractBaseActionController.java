package com.cannontech.web.stars.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.login.LoginService;

public abstract class AbstractBaseActionController implements Controller {
    public static final String LOGIN_URL = "/login.jsp";
    protected StarsDatabaseCache starsDatabaseCache;
    protected YukonUserDao yukonUserDao;
    protected EnergyCompanyDao energyCompanyDao;
    protected PaoDao paoDao;
    protected ContactDao contactDao;
    protected ContactNotificationDao contactNotificationDao;
    protected AuthDao authDao;
    protected RoleDao roleDao;
    protected YukonListDao yukonListDao;
    protected LoginService loginService;
    protected StarsCustAccountInformationDao starsCustAccountInformationDao;
    protected StarsInventoryBaseDao starsInventoryBaseDao;
    protected StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    protected StarsSearchDao starsSearchDao;
	
    
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public abstract String getRedirect(HttpServletRequest request) throws Exception;
    
    public abstract String getReferer(HttpServletRequest request) throws Exception;
    
    public abstract void doAction(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            StarsYukonUser user, LiteStarsEnergyCompany energyCompany) throws Exception;
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final HttpSession session = request.getSession(false);
        final StarsYukonUser user = ServletUtils.getStarsYukonUser(request);
        final LiteStarsEnergyCompany energyCompany = this.starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());

        doAction(request, response, session, user, energyCompany);
        return null;
    }
    
    @Required
    public void setStarsDatabaseCache(final StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    public void setYukonUserDao(final YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    public void setEnergyCompanyDao(final EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }
    
    public void setPaoDao(final PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setContactDao(final ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    public void setAuthDao(final AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setYukonListDao(final YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }
    
    public void setStarsCustAccountInformationDao(
            StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
    
    public void setStarsWorkOrderBaseDao(
            StarsWorkOrderBaseDao starsWorkOrderBaseDao) {
        this.starsWorkOrderBaseDao = starsWorkOrderBaseDao;
    }
    
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
		this.starsSearchDao = starsSearchDao;
	}
    
    public void setContactNotificationDao(
            ContactNotificationDao contactNotificationDao) {
        this.contactNotificationDao = contactNotificationDao;
    }
}
