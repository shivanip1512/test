/*
 * Created on Dec 19, 2003
 */
package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteTag;

/**
 * TagFuncs - deals with lite static tag definitions
 * @author aaron
 */
public final class TagFuncs {
	
	/**
	 * Return the LiteTag for the given tagID
	 * @param tagID
	 * @return
	 */
	public static LiteTag getLiteTag(int tagID) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) {
			Iterator iter = cache.getAllTags().iterator();
			while(iter.hasNext()) {
				LiteTag lt = (LiteTag) iter.next();
				if(tagID == lt.getLiteID()) {
					return lt;
				}
			}
		}
		return null;
	}
}
