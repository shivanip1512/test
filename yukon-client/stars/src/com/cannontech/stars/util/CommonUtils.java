package com.cannontech.stars.util;

import java.util.Hashtable;
import com.cannontech.stars.xml.serialize.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CommonUtils {
	// Map of energy company to selection list table
	// key: Integer energyCompanyID, value: java.util.Hashtable selectionListTable
    private static Hashtable selectionListTables = new Hashtable();

	public static Hashtable getSelectionListTable(Integer energyCompanyID) {
		Hashtable selectionListTable = (Hashtable) selectionListTables.get( energyCompanyID );
		if (selectionListTable == null)
			selectionListTable = loadSelectionListTable( energyCompanyID );
			
		return selectionListTable;
	}
	
	static Hashtable loadSelectionListTable(Integer energyCompanyID) {
        java.util.Hashtable selectionListTable = new java.util.Hashtable();
        
        // Get all selection lists
        com.cannontech.database.db.stars.CustomerSelectionList[] selectionLists =
        		com.cannontech.database.data.stars.CustomerSelectionList.getAllSelectionLists( energyCompanyID );
        
        for (int i = 0; i < selectionLists.length; i++) {
        	com.cannontech.database.db.stars.CustomerListEntry[] entries =
        			com.cannontech.database.data.stars.CustomerListEntry.getAllListEntries( selectionLists[i].getListID() );
        	StarsCustSelectionList starsList = new StarsCustSelectionList();
        	starsList.setListID( selectionLists[i].getListID().intValue() );
        	starsList.setListName( selectionLists[i].getListName() );
        	
        	for (int j = 0; j < entries.length; j++) {
        		StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
        		starsEntry.setEntryID( entries[j].getEntryID().intValue() );
        		starsEntry.setContent( entries[j].getEntryText() );
        		starsEntry.setYukonDefinition( entries[j].getYukonDefinition() );
        		starsList.addStarsSelectionListEntry( starsEntry );
        	}
        	
        	selectionListTable.put( selectionLists[i].getListName(), starsList );
        }
            
        // Get substation list
        com.cannontech.database.db.stars.Substation[] subs =
        		com.cannontech.database.data.stars.Substation.getAllSubstations( energyCompanyID );
        StarsCustSelectionList starsList = new StarsCustSelectionList();
        starsList.setListID( -1 );
        starsList.setListName( "Substation" );
        
        for (int i = 0; i < subs.length; i++) {
        	StarsSelectionListEntry starsEntry = new StarsSelectionListEntry();
        	starsEntry.setEntryID( subs[i].getSubstationID().intValue() );
        	starsEntry.setContent( subs[i].getSubstationName() );
        	starsList.addStarsSelectionListEntry( starsEntry );
        }
        selectionListTable.put( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION, starsList );
        
		selectionListTables.put( energyCompanyID, selectionListTable );
		return selectionListTable;
	}
}
