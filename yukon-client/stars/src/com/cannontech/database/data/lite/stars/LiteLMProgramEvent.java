package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteLMProgramEvent extends LiteLMCustomerEvent {
	
	private int programID = 0;
	
	public LiteLMProgramEvent() {
		super();
		setLiteType( LiteTypes.STARS_LMPROGRAM_EVENT );
	}
	
	public LiteLMProgramEvent(int eventID) {
		super( eventID );
		setLiteType( LiteTypes.STARS_LMPROGRAM_EVENT );
	}

	/**
	 * @return
	 */
	public int getProgramID() {
		return programID;
	}

	/**
	 * @param i
	 */
	public void setProgramID(int i) {
		programID = i;
	}

}
