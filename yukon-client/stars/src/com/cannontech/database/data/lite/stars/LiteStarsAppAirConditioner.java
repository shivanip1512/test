package com.cannontech.database.data.lite.stars;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsAppAirConditioner extends LiteStarsAppliance {

	private int tonnageID = CtiUtilities.NONE_ID;
	private int typeID = CtiUtilities.NONE_ID;
	
	public LiteStarsAppAirConditioner() {
		super();
	}
	
	public LiteStarsAppAirConditioner(int appID) {
		super( appID );
	}
	
	/**
	 * Returns the tonnageID.
	 * @return int
	 */
	public int getTonnageID() {
		return tonnageID;
	}

	/**
	 * Returns the typeID.
	 * @return int
	 */
	public int getTypeID() {
		return typeID;
	}

	/**
	 * Sets the tonnageID.
	 * @param tonnageID The tonnageID to set
	 */
	public void setTonnageID(int tonnageID) {
		this.tonnageID = tonnageID;
	}

	/**
	 * Sets the typeID.
	 * @param typeID The typeID to set
	 */
	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}

}
