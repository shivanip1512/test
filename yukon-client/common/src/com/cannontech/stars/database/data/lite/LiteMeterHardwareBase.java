package com.cannontech.stars.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteMeterHardwareBase extends LiteInventoryBase {
	
	private String meterNumber = null;
	private int meterTypeID = CtiUtilities.NONE_ZERO_ID;

	public LiteMeterHardwareBase() {
		super();
		setLiteType( LiteTypes.STARS_METERHARDWAREBASE);
	}
	
	public LiteMeterHardwareBase(int invID) {
		super();
		setLiteType( LiteTypes.STARS_METERHARDWAREBASE );
	}
	/**
	 * @return
	 */
	public int getMeterTypeID() {
		return meterTypeID;
	}

	/**
	 * @return
	 */
	public String getMeterNumber() {
		return meterNumber;
	}

	/**
	 * @param i
	 */
	public void setMeterTypeID(int i) {
		meterTypeID = i;
	}

	/**
	 * @param string
	 */
	public void setMeterNumber(String string) {
		meterNumber = string;
	}

}
