package com.cannontech.web.editor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;

/**
 * @author ryan
 *
 */
public class CBControllerEditor {
	
	private DeviceCBC deviceCBC = null;

	/**
	 * 
	 */
	public CBControllerEditor( DeviceCBC devCBC ) {
		super();
		setDeviceCBC( devCBC );
	}

	/**
	 * @return
	 */
	public DeviceCBC getDeviceCBC() {
		return deviceCBC;
	}

	/**
	 * @param deviceCBC
	 */
	public void setDeviceCBC(DeviceCBC deviceCBC) {
		this.deviceCBC = deviceCBC;
	}


	/**
	 * Gets the full DB object from the database so we can operate on
	 * the whole thing. Sets our fields to have the same values as the given
	 * DB object.
	 */
	public DBPersistent retrieveDB() {
		
		if( getDeviceCBC() == null ) return null;
		
		DBPersistent dbObj = 
			PAOFactory.createPAObject(
				getDeviceCBC().getDeviceID().intValue() );

		try {
			dbObj = Transaction.createTransaction( Transaction.RETRIEVE, dbObj ).execute();
			
			//set our feilds to what the user has set
			((ICapBankController)dbObj).setAddress( getDeviceCBC().getSerialNumber() );
			((ICapBankController)dbObj).setCommID( getDeviceCBC().getRouteID() );
			
			return dbObj;
		}
		catch( TransactionException te ) {
			CTILogger.error( "Unable to retrieve CBC db object", te );
			return null;
		}

	}

}
