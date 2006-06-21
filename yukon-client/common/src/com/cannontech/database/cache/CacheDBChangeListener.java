package com.cannontech.database.cache;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.conns.ConnPool;

/**
 * 
 * Creation date: (12/20/2001 1:06:33 PM)
 * 
 * @author: finessed a little by alauinger in 2006
 */
/*
 * This class helps the cache support notifying anyone interested in a database change.
 * When a DBChangeMsg comes in, it will tell the cache to handle it and the proceed
 * to tell all of the dbChangeListeners registered with it.
 * 
 * Usually this registration is doing through the cache, don't use this class directly.
 * This class is subject to change and you should probably not depend on it for new uses.
 */
public class CacheDBChangeListener implements MessageListener {
	private List<DBChangeListener> dbChangeListeners = new ArrayList<DBChangeListener>();

	/**
	 * CacheDBChangeListener constructor comment.
	 */
	public CacheDBChangeListener(ConnPool connectionPool) {
		super();
		connectionPool.getDefDispatchConn().addMessageListener(this);
	}

	/**
	 * 
	 * @param listener
	 *            com.cannontech.database.cache.DBChangeListener
	 */
	public void addDBChangeListener(DBChangeListener listener) {
		synchronized (dbChangeListeners) {
			if (!dbChangeListeners.contains(listener)) {
				dbChangeListeners.add(listener);
			}
		}
	}

	/**
	 * 
	 * @param listener
	 *            com.cannontech.database.cache.DBChangeListener
	 */
	public void removeDBChangeListener(DBChangeListener listener) {
		synchronized (dbChangeListeners) {
			dbChangeListeners.remove(listener);
		}
	}

	/**
	 * If the message received was a DBChangeMsg the ask the cache to figure out
	 * what what modified, is possible and call all of these DBChangeListeners
	 */
	public void messageReceived(MessageEvent e) {
		Object msg = e.getMessage();
		if (msg != null && msg instanceof DBChangeMsg) {
			synchronized (dbChangeListeners) {
				for (DBChangeListener listener : dbChangeListeners) {
					// handle the Cache's DBChangeMessages
					LiteBase lBase = DefaultDatabaseCache.getInstance()
							.handleDBChangeMessage((DBChangeMsg) msg);

					// do the listeners handler of DBChangeMessages
					listener.handleDBChangeMsg((DBChangeMsg) msg, lBase);
				}
			}
		}
	}
}
