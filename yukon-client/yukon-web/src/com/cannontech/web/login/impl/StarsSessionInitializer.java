package com.cannontech.web.login.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.web.login.SessionInitializer;

public class StarsSessionInitializer implements SessionInitializer {

    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private RolePropertyDao rolePropertyDao;

    @Override
    public void initSession(final LiteYukonUser user, final HttpSession session) {
        final StarsYukonUser starsUser = starsDatabaseCache.getStarsYukonUser(user);
        if (starsUser == null) {
            return;
        }

        session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, starsUser);

        if (rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, user)) {
            return;
        }

        final LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany(starsUser.getEnergyCompanyID());
        final StarsEnergyCompanySettings settings = liteEC.getStarsEnergyCompanySettings(starsUser);

        session.setAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, settings);
    }
}
