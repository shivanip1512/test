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
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.service.EnergyCompanyService;
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
    protected DBPersistentDao dbPersistentDao;
    protected AuthDao authDao;
    protected RoleDao roleDao;
    protected YukonListDao yukonListDao;
    protected LoginService loginService;
    protected StarsCustAccountInformationDao starsCustAccountInformationDao;
    protected StarsInventoryBaseDao starsInventoryBaseDao;
    protected StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    protected StarsSearchDao starsSearchDao;
    protected RolePropertyDao rolePropertyDao;
    protected DefaultRouteService defaultRouteService;
    protected EnergyCompanyService energyCompanyService;
	
    
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
    
    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    public void setAuthDao(final AuthDao authDao) {
        this.authDao = authDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
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
    
    public void setDefaultRouteService(DefaultRouteService defaultRouteService) {
        this.defaultRouteService = defaultRouteService;
    }
    
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
}
