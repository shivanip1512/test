package com.cannontech.stars.xml;

import com.cannontech.database.db.stars.hardware.LMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsLMHwFactory {

	public static StarsLMHw newStarsLMHw(StarsLMHw hw, Class type) {
		try {
			StarsLMHw starsHw = (StarsLMHw) type.newInstance();
			
			starsHw.setInventoryID( hw.getInventoryID() );
			starsHw.setCategory( hw.getCategory() );
			starsHw.setInstallationCompany( hw.getInstallationCompany() );
			starsHw.setReceiveDate( hw.getReceiveDate() );
			starsHw.setInstallDate( hw.getInstallDate() );
			starsHw.setRemoveDate( hw.getRemoveDate() );
			starsHw.setAltTrackingNumber( hw.getAltTrackingNumber() );
			starsHw.setVoltage( hw.getVoltage() );
			starsHw.setNotes( hw.getNotes() );
			starsHw.setInstallationNotes( hw.getInstallationNotes() );
			starsHw.setManufactureSerialNumber( hw.getManufactureSerialNumber() );
			starsHw.setLMDeviceType( hw.getLMDeviceType() );
			starsHw.setDeviceStatus( hw.getDeviceStatus() );
			starsHw.setStarsLMHardwareHistory( hw.getStarsLMHardwareHistory() );
			
			return starsHw;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
