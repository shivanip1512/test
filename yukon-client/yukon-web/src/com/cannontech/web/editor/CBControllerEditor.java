package com.cannontech.web.editor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.yukon.cbc.CBCUtils;

/**
 * @author ryan
 *
 */
public class CBControllerEditor {
	
	private YukonPAObject deviceCBC = null;


	/**
	 * Accepts a paoID and creates the DBPersistent from it, and then retrieves the
	 * data from the DB
	 */
	public CBControllerEditor( int paoId ) {
		super();
		
		setPaoCBC( PAOFactory.createPAObject(paoId) );
		retrieveDB();
		
		//complain if the DB object is not null and it is not a Controller
		if( getPaoCBC() != null && !(getPaoCBC() instanceof ICapBankController) )
			throw new IllegalArgumentException(
				"The CapController editor only allows PAO ids that map to a Controller, paoID=" + paoId +
				" is not an instance of a ICapBankController");
	}


	/**
	 * @return
	 */
	public boolean isEditingIntegrity() {
		
		return isTwoWay() && 
			((TwoWayDevice)getPaoCBC()).getDeviceScanRateMap().containsKey(
					DeviceScanRate.TYPE_INTEGRITY);
	}

	/**
	 * @return
	 */
	public boolean isEditingException() {
		
		return isTwoWay() && 
			((TwoWayDevice)getPaoCBC()).getDeviceScanRateMap().containsKey(
					DeviceScanRate.TYPE_EXCEPTION);
	}

	/**
	 * @return
	 */
	public void setEditingIntegrity( boolean val ) {
	}
	/**
	 * @return
	 */
	public void setEditingException( boolean val ) {
	}

	/**
	 * @return
	 */
	public boolean isTwoWay() {
		return CBCUtils.isTwoWay( getPaoCBC() );
	}

	/**
	 * @return
	 */
	public YukonPAObject getPaoCBC() {
		return deviceCBC;
	}

	/**
	 * @param deviceCBC
	 */
	public void setPaoCBC(YukonPAObject deviceCBC) {
		this.deviceCBC = deviceCBC;
	}


	/**
	 * Gets the full DB object from the database so we can operate on
	 * the whole thing.
	 */
	public void retrieveDB() {
		
		if( getPaoCBC() == null ) return;
		
		try {
			setPaoCBC( (YukonPAObject)
				Transaction.createTransaction(Transaction.RETRIEVE, getPaoCBC()).execute() );
			
		}
		catch( TransactionException te ) {
			CTILogger.error( "Unable to retrieve CBC db object", te );
			return;
		}

	}

}
