/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.cttp.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.cttp.CttpCmdCache;
import com.cannontech.cttp.data.CttpCmd;

/**
 * @author aaron
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestCache {

	public static void main(String[] args) {
		int tID = 1;
		System.out.println("****");
		printGroup(5);
		printGroup(6);
		printGroup(7);
		
		printCmd(1);
		
		
		ArrayList gList = new ArrayList();
		gList.add(new Integer("5"));
		gList.add(new Integer("6"));
		gList.add(new Integer("7"));
		CttpCmdCache.getInstance().addOffsetCmd(tID++, 1, 15, 6, gList);
		

		System.out.println("****");
		printGroup(5);
		printGroup(6);
		printGroup(7);
		
		printCmd(1);
		
		CttpCmdCache.getInstance().addClearCmd(tID++, 1, gList);
		

		System.out.println("****");
		printGroup(5);
		printGroup(6);
		printGroup(7);
		
		printCmd(1);
		
		CttpCmdCache.getInstance().addOffsetCmd(tID++, 2, 9, 3, gList);
		

		System.out.println("****");
		printGroup(5);
		printGroup(6);
		printGroup(7);
		
		printCmd(1);
		
		CttpCmdCache.getInstance().addClearCmd(tID++, 2, gList);
		

		System.out.println("****");
		printGroup(5);
		printGroup(6);
		printGroup(7);
		
	}
	
	private static void printGroup(int id) {
		System.out.println("Current command for group: " + id);
		CttpCmd cmd = CttpCmdCache.getInstance().getCurrentCmdForGroup(id);
		if(cmd != null) {
			if(cmd.getClearCmd().charValue() != 'Y') {
			System.out.println("Offset command");
			System.out.println("TrackingID: " + cmd.getTrackingID());
			System.out.println("Offset: " + cmd.getDegOffset());
			System.out.println("Duration: " + cmd.getDuration());
			System.out.println("TimeSent: " + cmd.getTimeSent());
			System.out.println("LastUpdate: " + cmd.getLastUpdated());
			System.out.println("Status: " + cmd.getStatus());
			}
			else {
				System.out.println("looks like a clear command, that ain't right!");
				System.out.println("TrackingID: " + cmd.getTrackingID());
			}
		}
		else {
			System.out.println("No current commands for group: " + id);
		}
	}
	private static void printCmd(int id) {
	    List gList = (List) CttpCmdCache.getInstance().getGroupIDsForCmd(id);
	    if(gList == null) {
	    	System.out.println("No groups found for tracking id: " + id);
	    	return;
	    }
	    
		System.out.println("Groups command with tracking id: " + id);
		Iterator gIter = gList.iterator();
		while(gIter.hasNext()) {
			System.out.println("Group id" +  gIter.next().toString());
		}
		
	}
}
