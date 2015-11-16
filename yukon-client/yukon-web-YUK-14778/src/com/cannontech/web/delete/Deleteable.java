package com.cannontech.web.delete;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author ryan
 *
 */
public class Deleteable {

	private DBPersistent dbPersistent = null;
	private String warningMsg = "(unknown item)";
	private boolean deleteAllowed = false;
	private boolean wasDeleted = false;
	private boolean deleteError = false;
    private Boolean checked = Boolean.FALSE;


	/**
	 * 
	 */
	public Deleteable() {
		super();
	}

	/**
	 * @return
	 */
	protected DBPersistent getDbPersistent() {
		return dbPersistent;
	}

	/**
	 * @param persistent
	 */
	protected void setDbPersistent(DBPersistent persistent) {
		dbPersistent = persistent;
	}

	/**
	 * @return
	 */
	public String getName() {
		
		if( getDbPersistent() != null )
			return getDbPersistent().toString();
		else
			return CtiUtilities.STRING_NONE;
	}

	/**
	 * @return
	 */
	public String getWarningMsg() {
		return warningMsg;
	}

	/**
	 * @param string
	 */
	public void setWarningMsg(String string) {
		warningMsg = string;
	}


	/**
	 * @return
	 */
	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}

	/**
	 * @param b
	 */
	public void setDeleteAllowed(boolean b) {
		deleteAllowed = b;
	}

	/**
	 * @return
	 */
	public boolean getWasDeleted() {
		return wasDeleted;
	}

	/**
	 * @param b
	 */
	public void setWasDeleted(boolean b) {
		wasDeleted = b;
	}

	/**
	 * @return
	 */
	public boolean isDeleteError() {
		return deleteError;
	}

	/**
	 * @param b
	 */
	public void setDeleteError(boolean b) {
		deleteError = b;
	}

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

}
