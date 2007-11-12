package com.cannontech.web.login.impl;

import javax.servlet.http.HttpSession;

import com.cannontech.common.version.VersionTools;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.web.login.SessionInitializer;

public class StarsSessionInitializer implements SessionInitializer {
    private StarsDatabaseCache starsDatabaseCache;
    
    public void initSession(final LiteYukonUser user, final HttpSession session) {
        if (!isStarsEnabled()) return;
        
        final StarsYukonUser starsUser = starsDatabaseCache.getStarsYukonUser(user);
        if (starsUser == null) return;

        final LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( starsUser.getEnergyCompanyID() );
        final StarsEnergyCompanySettings settings = liteEC.getStarsEnergyCompanySettings( starsUser );

        session.setAttribute(ServletUtils.ATT_STARS_YUKON_USER, starsUser);
        session.setAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS, settings );
    }
    
    private boolean isStarsEnabled() {
        try{
            boolean starsExists = VersionTools.starsExists();
            return starsExists;
        }catch (Exception e) {
            return false;
        }
    }
    
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}
