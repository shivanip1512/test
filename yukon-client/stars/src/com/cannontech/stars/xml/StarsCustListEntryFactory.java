package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;

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
	
	public static StarsCustListEntry getStarsCustListEntry(
		java.util.Hashtable selectionLists, String listName, String yukonDef) {
		StarsCustSelectionList list = (StarsCustSelectionList) selectionLists.get( listName );
		for (int i = 0; i < list.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = list.getStarsSelectionListEntry(i);
			if (entry.getYukonDefinition().equalsIgnoreCase( yukonDef ))
				return entry;
		}
		
		return null;
	}
}
