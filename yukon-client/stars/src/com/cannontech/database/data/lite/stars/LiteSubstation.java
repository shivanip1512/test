/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LiteSubstation extends LiteBase {
	
	private String substationName = null;
	private int routeID = 0;
	
	public LiteSubstation() {
		super();
		setLiteType( LiteTypes.STARS_SUBSTATION );
	}
	
	public LiteSubstation(int substationID) {
		super();
		setSubstationID( substationID );
		setLiteType( LiteTypes.STARS_SUBSTATION );
	}
	
	public int getSubstationID() {
		return getLiteID();
	}
	
	public void setSubstationID(int substationID) {
		setLiteID(substationID);
	}

	/**
	 * @return
	 */
	public int getRouteID() {
		return routeID;
	}

	/**
	 * @return
	 */
	public String getSubstationName() {
		return substationName;
	}

	/**
	 * @param i
	 */
	public void setRouteID(int i) {
		routeID = i;
	}

	/**
	 * @param string
	 */
	public void setSubstationName(String string) {
		substationName = string;
	}

}
