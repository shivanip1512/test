package com.cannontech.stars.xml;

import com.cannontech.database.db.web.YukonWebConfiguration;
import com.cannontech.stars.xml.serialize.StarsWebConfig;

/**
 * <p>Title: StarsWebConfigFactory.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Sep 4, 2002 5:33:51 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class StarsWebConfigFactory {
	
	public static StarsWebConfig newStarsWebConfig(YukonWebConfiguration config) {
		StarsWebConfig starsConfig = new StarsWebConfig();
		
		starsConfig.setAlternateDisplayName( config.getAlternateDisplayName() );
		starsConfig.setDescription( config.getDescription() );
		starsConfig.setLogoLocation( config.getLogoLocation() );
		starsConfig.setURL( config.getURL() );
		
		return starsConfig;
	}
	
	public static void setWebConfig(YukonWebConfiguration config, StarsWebConfig starsConfig) {
		config.setAlternateDisplayName( starsConfig.getAlternateDisplayName() );
		config.setDescription( starsConfig.getDescription() );
		config.setLogoLocation( starsConfig.getLogoLocation() );
		config.setURL( starsConfig.getURL() );
	}
}
