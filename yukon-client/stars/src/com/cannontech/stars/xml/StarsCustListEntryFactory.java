package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.database.data.lite.stars.LiteCustomerSelectionList;

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
	
	public static StarsSelectionListEntry getStarsCustListEntry(StarsCustSelectionList list, String yukonDef) {
		for (int i = 0; i < list.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = list.getStarsSelectionListEntry(i);
			if (entry.getYukonDefinition() != null && entry.getYukonDefinition().equalsIgnoreCase( yukonDef ))
				return entry;
		}
		
		return null;
	}
	
	public static StarsSelectionListEntry getStarsCustListEntry(StarsCustSelectionList list, int entryID) {
		for (int i = 0; i < list.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = list.getStarsSelectionListEntry(i);
			if (entry.getEntryID() == entryID)
				return entry;
		}
		
		return null;
	}
	
	public static StarsSelectionListEntry getStarsCustListEntry(LiteCustomerSelectionList list, String yukonDef) {
		StarsSelectionListEntry[] entries = list.getListEntries();
		for (int i = 0; i < entries.length; i++)
			if (entries[i].getYukonDefinition() != null && entries[i].getYukonDefinition().equalsIgnoreCase( yukonDef ))
				return entries[i];
				
		return null;
	}
	
	public static StarsSelectionListEntry getStarsCustListEntry(LiteCustomerSelectionList list, int entryID) {
		StarsSelectionListEntry[] entries = list.getListEntries();
		for (int i = 0; i < entries.length; i++)
			if (entries[i].getEntryID() == entryID)
				return entries[i];
				
		return null;
	}
}
