package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsApp;
import com.cannontech.database.db.stars.appliance.ApplianceBase;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsAppFactory {
	public static StarsApp newStarsApp(StarsApp app, Class type) {
		try {
			StarsApp starsApp = (StarsApp) type.newInstance();
			
			starsApp.setApplianceID( app.getApplianceID() );
			starsApp.setApplianceCategoryID( app.getApplianceCategoryID() );
			starsApp.setCategoryName( app.getCategoryName() );
			starsApp.setManufacturer( app.getManufacturer() );
			starsApp.setManufactureYear( app.getManufactureYear() );
			starsApp.setLocation( app.getLocation() );
			starsApp.setServiceCompany( app.getServiceCompany() );
			starsApp.setNotes( app.getNotes() );
			
			return starsApp;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void setApplianceBase(ApplianceBase appDB, StarsApp app) {
		appDB.setApplianceID( new Integer(app.getApplianceID()) );
		appDB.setApplianceCategoryID( new Integer(app.getApplianceCategoryID()) );
		appDB.setNotes( app.getNotes() );
	}
}
