/*
 * Created on May 18, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db;

import java.util.Vector;
import com.cannontech.database.Transaction;
import com.cannontech.clientutils.CTILogger;

import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.device.DeviceVerification;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.contact.ContactNotification;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class NestedDBPersistentComparators 
{
	public static java.util.Comparator lmControlAreaTriggerComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			//if the ID is null, then one can safely assume it is new
			Integer thisVal = ((LMControlAreaTrigger)o1).getTriggerID();
			if(thisVal == null)
				thisVal = new Integer(-2);
			Integer anotherVal = ((LMControlAreaTrigger)o2).getTriggerID();
			if(anotherVal == null)
				anotherVal = new Integer(-3);
			return (thisVal.intValue()<anotherVal.intValue() ? -1 : (thisVal.intValue() == anotherVal.intValue() ? 0:1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator lmDirectGearComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			//if the ID is null, then one can safely assume it is new
			Integer thisVal = ((LMProgramDirectGear)o1).getGearID();
			if(thisVal == null)
				thisVal = new Integer(-2);
			Integer anotherVal = ((LMProgramDirectGear)o2).getGearID();
			if(anotherVal == null)
				anotherVal = new Integer(-1);
			return (thisVal.intValue()<anotherVal.intValue() ? -1 : (thisVal.intValue()==anotherVal.intValue() ? 0 : 1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator lmControlAreaProgramComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LMControlAreaProgram)o1).getLmProgramDeviceID().intValue();
			int anotherVal = ((LMControlAreaProgram)o2).getLmProgramDeviceID().intValue();
			return (thisVal<anotherVal ? -1 : (thisVal == anotherVal ? 0:1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public static java.util.Comparator lmControlScenarioProgramComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LMControlScenarioProgram)o1).getProgramID().intValue();
			int anotherVal = ((LMControlScenarioProgram)o2).getProgramID().intValue();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator deviceVerificationComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisTransID = ((DeviceVerification)o1).getTransmitterID().intValue();
			int anotherTransID = ((DeviceVerification)o2).getTransmitterID().intValue();
			int thisReceiverID = ((DeviceVerification)o1).getReceiverID().intValue();
			int anotherReceiverID = ((DeviceVerification)o2).getReceiverID().intValue();
			return (thisTransID == anotherTransID ? (thisReceiverID == anotherReceiverID ? 0 : 1) : -1);
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator paoExclusionComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			//if the ID is null, then one can safely assume it is new
			Integer thisVal = ((PAOExclusion)o1).getExclusionID();
			if(thisVal == null)
				thisVal = new Integer(-1);
			Integer anotherVal = ((PAOExclusion)o2).getExclusionID();
			if(anotherVal == null)
				anotherVal = new Integer(-1);
			return (thisVal.intValue() < anotherVal.intValue() ? -1 : (thisVal.intValue()==anotherVal.intValue() ? 0 : 1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public static java.util.Comparator contactNotificationComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			//if the ID is null, then one can safely assume it is new
			Integer thisVal = ((ContactNotification)o1).getContactNotifID();
			if(thisVal == null)
				thisVal = new Integer(-1);
			Integer anotherVal = ((ContactNotification)o2).getContactNotifID();
			if(anotherVal == null)
				anotherVal = new Integer(-2);
			return (thisVal.intValue()<anotherVal.intValue() ? -1 : (thisVal.intValue()==anotherVal.intValue() ? 0 : 1));
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public static boolean areNestedObjectsEqual(Object obj1, Object obj2, java.util.Comparator comparator)
	{
		return (comparator.compare(obj1, obj2) == 0);
	}
	
	/**
	* Insert the type's description here.
	* Creation date: (1/12/2004 12:54:55 PM)
	* @author: jdayton
	*/
	public static Vector NestedDBPersistentCompare(Vector oldList, Vector newList, java.util.Comparator comparator)
	{
		if(comparator == null)
			CTILogger.error("NestedDBPersistentCompare requires a valid nested db persistent Comparator.");
		
		Vector tempVect = new Vector();
	
		//checks for old or unused objects to update or delete
		for( int j = 0; j < oldList.size(); j++ )
		{
			NestedDBPersistent oldNest = (NestedDBPersistent)oldList.get(j);
			boolean fnd = false;
		
			for( int i = 0; i < newList.size(); i++ )
			{
				NestedDBPersistent newNest = (NestedDBPersistent)newList.get(i);
			
				if( areNestedObjectsEqual(oldNest, newNest, comparator) )
				{
					// item in OLD list & NEW list, update
					newNest.setOpCode( Transaction.UPDATE );
					tempVect.add( newNest );
					fnd = true;
					break;
				}
			}

			if( !fnd )
			{
				// item in OLD list only, delete
				oldNest.setOpCode( Transaction.DELETE );
				tempVect.add( oldNest );
			}
		}
	
		//checks for brand new objects to add
		for( int x = 0; x < newList.size(); x++ )
		{
			NestedDBPersistent newNest = (NestedDBPersistent)newList.get(x);
			boolean inOld = false;
			
			for( int i = 0; i < oldList.size(); i++ )
			{
				NestedDBPersistent oldNest = (NestedDBPersistent)oldList.get(i);
			
				if( areNestedObjectsEqual(newNest, oldNest, comparator) )
				{
					inOld = true;
					break;
				}
			}
			if(!inOld)
			{
				// item in NEW list, add
				newNest.setOpCode( Transaction.INSERT );
				tempVect.add( newNest );
			}
		}
		
		return tempVect;
}
	
	
	
	   
}