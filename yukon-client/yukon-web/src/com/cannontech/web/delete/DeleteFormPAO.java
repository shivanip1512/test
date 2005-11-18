package com.cannontech.web.delete;

import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.db.DBPersistent;

/**
 * @author ryan
 *
 */
public class DeleteFormPAO extends DeleteForm
{

	public DeleteFormPAO() {
		super();
	}
	
	public DBPersistent getDBObj( int itemID ) {		
		return PAOFactory.createPAObject( itemID );		
	}

}