package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCustListEntry;

/**
 * <p>Title: StarsCustListEntryFactory.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Sep 9, 2002 5:43:00 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class StarsCustListEntryFactory {
	
	public static StarsCustListEntry newStarsCustListEntry(StarsCustListEntry entry, Class type) {
		try {
			StarsCustListEntry newEntry = (StarsCustListEntry) type.newInstance();
			
			newEntry.setEntryID( entry.getEntryID() );
			newEntry.setContent( entry.getContent() );
			
			return newEntry;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
