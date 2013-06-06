package com.cannontech.web.delete;


import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author ryan
 *
 */
public class DeleteFormPoint extends DeleteForm
{
	public DeleteFormPoint() {
		super();
	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	public DBPersistent getDBObj( int itemID ) {
		return LiteFactory.createDBPersistent( YukonSpringHook.getBean(PointDao.class).getLitePoint(itemID) );		
	}

    protected void checkForErrors() throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void resetForm() {
        
    }


}