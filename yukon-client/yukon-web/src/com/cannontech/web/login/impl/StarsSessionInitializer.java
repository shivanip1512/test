package com.cannontech.web.login.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.web.login.SessionInitializer;

public class StarsSessionInitializer implements SessionInitializer {
    private StarsDatabaseCache starsDatabaseCache;
    
    public void initSession(final LiteYukonUser user, final HttpSession session) {
        final StarsYukonUser starsUser = starsDatabaseCache.getStarsYukonUser(user);
        if (starsUser == null) return;
        session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, starsUser);
        
        if (StarsUtils.isResidentialCustomer(user)) {
        	return;
        }

        final LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( starsUser.getEnergyCompanyID() );
        final StarsEnergyCompanySettings settings = liteEC.getStarsEnergyCompanySettings( starsUser );

        session.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, settings );
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}
