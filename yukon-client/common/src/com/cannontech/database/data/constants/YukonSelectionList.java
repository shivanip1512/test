package com.cannontech.database.data.constants;

import java.sql.SQLException;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeHelper;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class YukonSelectionList extends DBPersistent implements CTIDbChange {
	
	private com.cannontech.database.db.constants.YukonSelectionList yukonSelectionList = null;
	
	public YukonSelectionList() {
		super();
	}
	
	public void setListID(Integer listID) {
		getYukonSelectionList().setListID( listID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonSelectionList().setDbConnection( conn );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
    	getYukonSelectionList().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
    	// Delete all selection list entries
    	com.cannontech.database.db.constants.YukonListEntry.deleteAllListEntries(
    			getYukonSelectionList().getListID(), getDbConnection() );
    	
    	getYukonSelectionList().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getYukonSelectionList().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getYukonSelectionList().update();
	}

	/**
	 * Returns the yukonSelectionList.
	 * @return com.cannontech.database.db.constants.YukonSelectionList
	 */
	public com.cannontech.database.db.constants.YukonSelectionList getYukonSelectionList() {
		if (yukonSelectionList == null)
			yukonSelectionList = new com.cannontech.database.db.constants.YukonSelectionList();
		return yukonSelectionList;
	}

	/**
	 * Sets the yukonSelectionList.
	 * @param yukonSelectionList The yukonSelectionList to set
	 */
	public void setYukonSelectionList(com.cannontech.database.db.constants.YukonSelectionList yukonSelectionList) {
		this.yukonSelectionList = yukonSelectionList;
	}

    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs = {
            DbChangeHelper.newDbChange(dbChangeType, DbChangeCategory.YUKON_SELECTION_LIST, yukonSelectionList.getListID())
        };

        return msgs;
    }

}
