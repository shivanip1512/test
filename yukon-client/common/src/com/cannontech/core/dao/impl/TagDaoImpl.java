/*
 * Created on Dec 19, 2003
 */
package com.cannontech.core.dao.impl;

import java.util.Iterator;

import com.cannontech.core.dao.TagDao;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.yukon.IDatabaseCache;

/**
 * TagFuncs - deals with lite static tag definitions
 * @author aaron
 */
public final class TagDaoImpl implements TagDao {
	
    private IDatabaseCache databaseCache;
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.TagDao#getLiteTag(int)
     */
	public LiteTag getLiteTag(int tagID) {
		synchronized(databaseCache) {
			Iterator iter = databaseCache.getAllTags().iterator();
			while(iter.hasNext()) {
				LiteTag lt = (LiteTag) iter.next();
				if(tagID == lt.getLiteID()) {
					return lt;
				}
			}
		}
		return null;
	}
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
