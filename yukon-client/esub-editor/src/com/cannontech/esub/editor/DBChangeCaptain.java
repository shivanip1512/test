package com.cannontech.esub.editor;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.esub.util.Util;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.ClientConnection;

/**
 * This class glues a connection to dispatch with the cache.
 * @author alauinger
 */
public class DBChangeCaptain implements DBChangeListener {

	/**
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public ClientConnection getClientConnection() {
		return Util.getConnToDispatch();
	}

	/**
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(DBChangeMsg, LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase) {
	}

}
 