package com.cannontech.database.data.lite;

/**
 * This type was created in VisualAge.
 */
public final class LiteComparators 
{
	public static java.util.Comparator litePointDeviceIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LitePoint)o1).getPaobjectID();
			int anotherVal = ((com.cannontech.database.data.lite.LitePoint)o2).getPaobjectID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator litePointPointOffsetComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LitePoint)o1).getPointOffset();
			int anotherVal = ((LitePoint)o2).getPointOffset();
			return (thisVal<anotherVal ? -1 : (thisVal == anotherVal ? 0:1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public static java.util.Comparator litePointIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LitePoint)o1).getPointID();
			int anotherVal = ((com.cannontech.database.data.lite.LitePoint)o2).getPointID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator liteYukonPAObjectPortComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o1).getPortID();
			int anotherVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o2).getPortID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};


	public static java.util.Comparator liteYukonPAObjectIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o1).getYukonID();
			int anotherVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o2).getYukonID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator liteBaseIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((com.cannontech.database.data.lite.LiteBase)o1).getLiteID();
			int anotherVal = ((com.cannontech.database.data.lite.LiteBase)o2).getLiteID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	// this method is used to compare Strings found in ANY lite class
	//   ADD ALL LIGHT CLASSES YOU WANT TO COMPARE BELOW!!!!!!!!!!!!!
	public static java.util.Comparator liteStringComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = null, anotherVal = null;
			
			if(o1 instanceof com.cannontech.database.data.lite.LiteYukonPAObject && o2 instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
			{
				thisVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o1).getPaoName();
				anotherVal = ((com.cannontech.database.data.lite.LiteYukonPAObject)o2).getPaoName();
			}
			else if(o1 instanceof LiteStateGroup && o2 instanceof LiteStateGroup)
			{
				thisVal = ((LiteStateGroup)o1).getStateGroupName();
				anotherVal = ((LiteStateGroup)o2).getStateGroupName();
			}
			else if(o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				thisVal = ((LiteYukonPAObject)o1).getPaoName();
				anotherVal = ((LiteYukonPAObject)o2).getPaoName();
			}
			else if (o1 instanceof LitePoint && o2 instanceof LitePoint)
			{
				thisVal = ((LitePoint)o1).getPointName();
				anotherVal = ((LitePoint)o2).getPointName();
			}
			else if(o1 instanceof LiteNotificationGroup && o2 instanceof LiteNotificationGroup)
			{
				thisVal = ((LiteNotificationGroup)o1).getNotificationGroupName();
				anotherVal = ((LiteNotificationGroup)o2).getNotificationGroupName();
			}
			else if(o1 instanceof LiteNotificationRecipient && o2 instanceof LiteNotificationRecipient)
			{
				thisVal = ((LiteNotificationRecipient)o1).getRecipientName();
				anotherVal = ((LiteNotificationRecipient)o2).getRecipientName();
			}
			else if(o1 instanceof LiteAlarmCategory && o2 instanceof LiteAlarmCategory)
			{
				thisVal = ((LiteAlarmCategory)o1).getCategoryName();
				anotherVal = ((LiteAlarmCategory)o2).getCategoryName();
			}
			else if(o1 instanceof LiteCustomerContact && o2 instanceof LiteCustomerContact)
			{
				thisVal = ((LiteCustomerContact)o1).getContLastName();
				anotherVal = ((LiteCustomerContact)o2).getContLastName();
			}
			else if(o1 instanceof LiteDeviceMeterNumber && o2 instanceof LiteDeviceMeterNumber )
			{
				thisVal = ((LiteDeviceMeterNumber)o1).getMeterNumber();
				anotherVal = ((LiteDeviceMeterNumber)o2).getMeterNumber();
			}
			else if(o1 instanceof com.cannontech.database.data.lite.LiteHolidaySchedule && o2 instanceof com.cannontech.database.data.lite.LiteHolidaySchedule)
			{
				thisVal = ((com.cannontech.database.data.lite.LiteHolidaySchedule)o1).getHolidayScheduleName();
				anotherVal = ((com.cannontech.database.data.lite.LiteHolidaySchedule)o2).getHolidayScheduleName();
			}
			else
			{	// unknown lite type
				throw new IllegalArgumentException("Unhandled lite types or the 2 objects being compared are not the same object types in comparator of : " + this.getClass().toString() );
			}

			
			return ( thisVal.compareToIgnoreCase(anotherVal) );			
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

}
