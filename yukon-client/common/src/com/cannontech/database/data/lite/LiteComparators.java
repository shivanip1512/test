package com.cannontech.database.data.lite;

import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;

/**
 * This type was created in VisualAge.
 */
public final class LiteComparators 
{
	public static java.util.Comparator litePointDeviceIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LitePoint)o1).getPaobjectID();
			int anotherVal = ((LitePoint)o2).getPaobjectID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
/*
			if( thisVal != anotherVal )
				return( thisVal < anotherVal ? -1 : 1 );

			int val = ((LitePoint)o1).getPointName().compareToIgnoreCase(
										((LitePoint)o2).getPointName() );
			return (val < 0 ? -1 : (val > 0 ? 1 : 0) );
*/
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator litePaoPortIDComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LiteYukonPAObject)o1).getPortID();
			int anotherVal = ((LiteYukonPAObject)o2).getPortID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static java.util.Comparator litePaoTypeComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LiteYukonPAObject)o1).getType();
			int anotherVal = ((LiteYukonPAObject)o2).getType();
			
			
			if( thisVal != anotherVal )
				return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
				
			//if the types are equal, we need to sort by Name
			String thisName = ((LiteYukonPAObject)o1).getPaoName();
			String anotherName = ((LiteYukonPAObject)o2).getPaoName();
				
			return( thisName.compareToIgnoreCase(anotherName) );			
			//return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
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
			int thisVal = ((LitePoint)o1).getPointID();
			int anotherVal = ((LitePoint)o2).getPointID();
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
			int thisVal = ((LiteYukonPAObject)o1).getPortID();
			int anotherVal = ((LiteYukonPAObject)o2).getPortID();
            
            
            if( thisVal != anotherVal )
                return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
                
            //if the types are equal, we need to sort by Name
            String thisName = ((LiteYukonPAObject)o1).getPaoName();
            String anotherName = ((LiteYukonPAObject)o2).getPaoName();
                
            return( thisName.compareToIgnoreCase(anotherName) );            
            //return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));            
            
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
			int thisVal = ((LiteYukonPAObject)o1).getYukonID();
			int anotherVal = ((LiteYukonPAObject)o2).getYukonID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

   public static java.util.Comparator liteYukonImageCategoryComparator = new java.util.Comparator()
   {
      public int compare(Object o1, Object o2)
      {
         String thisVal = ((LiteYukonImage)o1).getImageCategory();
         String anotherVal = ((LiteYukonImage)o2).getImageCategory();

         return ( thisVal.compareToIgnoreCase(anotherVal) );
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
			int thisVal = ((LiteBase)o1).getLiteID();
			int anotherVal = ((LiteBase)o2).getLiteID();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

	public static Comparator<Object> liteNameComparator = new Comparator<Object>()
	{
		public int compare(Object o1, Object o2)
		{
			if( o1 == null || o2 == null )
				return -1;

			String thisVal = o1.toString();
			String anotherVal = o2.toString();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public static java.util.Comparator liteRoleCategoryComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((LiteYukonRole)o1).getCategory();
			String anotherVal = ((LiteYukonRole)o2).getCategory();

			if( !thisVal.equalsIgnoreCase(anotherVal) )
				return( thisVal.compareToIgnoreCase(anotherVal) );
				
			//if the Categories are equal, we need to sort by Role Name
			String thisName = ((LiteYukonRole)o1).getRoleName();
			String anotherName = ((LiteYukonRole)o2).getRoleName();
				
			return( thisName.compareToIgnoreCase(anotherName) );
         //return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};	
	/**
	 * Sort deviceTypeCommands by their displayOrder
	 */
	public static java.util.Comparator liteDeviceTypeCommandComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			int thisVal = ((LiteDeviceTypeCommand)o1).getDisplayOrder();
			int anotherVal = ((LiteDeviceTypeCommand)o2).getDisplayOrder();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	/**
	 * Sort Commands by their label
	 */
	public static java.util.Comparator liteCommandComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((LiteCommand)o1).getLabel();
			String anotherVal = ((LiteCommand)o2).getLabel();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
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
            
			if(o1 instanceof LiteYukonPAObject && o2 instanceof LiteYukonPAObject)
			{
				thisVal = ((LiteYukonPAObject)o1).getPaoName();
				anotherVal = ((LiteYukonPAObject)o2).getPaoName();
			}
			else if(o1 instanceof LiteStateGroup && o2 instanceof LiteStateGroup)
			{
				thisVal = ((LiteStateGroup)o1).getStateGroupName();
				anotherVal = ((LiteStateGroup)o2).getStateGroupName();
			}
			else if(o1 instanceof LiteCICustomer && o2 instanceof LiteCICustomer)
			{
				thisVal = ((LiteCICustomer)o1).getCompanyName();
				anotherVal = ((LiteCICustomer)o2).getCompanyName();
			}
			else if(o1 instanceof LiteEnergyCompany && o2 instanceof LiteEnergyCompany)
			{
				thisVal = ((LiteEnergyCompany)o1).getName();
				anotherVal = ((LiteEnergyCompany)o2).getName();
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
			else if(o1 instanceof LiteAlarmCategory && o2 instanceof LiteAlarmCategory)
			{
				thisVal = ((LiteAlarmCategory)o1).getCategoryName();
				anotherVal = ((LiteAlarmCategory)o2).getCategoryName();
			}
			else if(o1 instanceof LiteContact && o2 instanceof LiteContact)
			{
				thisVal = ((LiteContact)o1).getContLastName();
				anotherVal = ((LiteContact)o2).getContLastName();
				
				if( thisVal.equalsIgnoreCase(anotherVal) )
				{				
					//if the Last Names are equal, we need to sort by First Names
					thisVal = ((LiteContact)o1).getContFirstName();
					anotherVal = ((LiteContact)o2).getContFirstName();
				}				
			}
			else if(o1 instanceof LiteDeviceMeterNumber && o2 instanceof LiteDeviceMeterNumber )
			{
				thisVal = ((LiteDeviceMeterNumber)o1).getMeterNumber();
				anotherVal = ((LiteDeviceMeterNumber)o2).getMeterNumber();
			}
			else if(o1 instanceof LiteHolidaySchedule && o2 instanceof LiteHolidaySchedule)
			{
				thisVal = ((LiteHolidaySchedule)o1).getHolidayScheduleName();
				anotherVal = ((LiteHolidaySchedule)o2).getHolidayScheduleName();
			}
			else if(o1 instanceof LiteSeasonSchedule && o2 instanceof LiteSeasonSchedule)
			{
				thisVal = ((LiteSeasonSchedule)o1).getScheduleName();
				anotherVal = ((LiteSeasonSchedule)o2).getScheduleName();
			}
			else if(o1 instanceof LiteBaseline && o2 instanceof LiteBaseline)
			{
				thisVal = ((LiteBaseline)o1).getBaselineName();
				anotherVal = ((LiteBaseline)o2).getBaselineName();
			}
			else if(o1 instanceof LiteConfig && o2 instanceof LiteConfig)
			{
				thisVal = ((LiteConfig)o1).getConfigName();
				anotherVal = ((LiteConfig)o2).getConfigName();
			}
			else if(o1 instanceof LiteTOUSchedule && o2 instanceof LiteTOUSchedule)
			{
				thisVal = ((LiteTOUSchedule)o1).getScheduleName();
				anotherVal = ((LiteTOUSchedule)o2).getScheduleName();
			}
			else if(o1 instanceof LiteDeviceTypeCommand && o2 instanceof LiteDeviceTypeCommand)
			{
				thisVal = ((LiteDeviceTypeCommand)o1).getDeviceType();
				anotherVal = ((LiteDeviceTypeCommand)o2).getDeviceType();
			}
			else if(o1 instanceof LiteCommand && o2 instanceof LiteCommand)
			{
				thisVal = ((LiteCommand)o1).getLabel();
				anotherVal = ((LiteCommand)o2).getLabel();
				
				if( thisVal.equalsIgnoreCase(anotherVal) )
				{				
					//if the labels are equal, we need to sort by commands
					thisVal = ((LiteCommand)o1).getCommand();
					anotherVal = ((LiteCommand)o2).getCommand();
				}					
			}
			else if(o1 instanceof LiteTag && o2 instanceof LiteTag)
			{
				thisVal = ((LiteTag)o1).getTagName();
				anotherVal = ((LiteTag)o2).getTagName();
			}
			else if(o1 instanceof LiteLMConstraint && o2 instanceof LiteLMConstraint)
			{
				thisVal = ((LiteLMConstraint)o1).getConstraintName();
				anotherVal = ((LiteLMConstraint)o2).getConstraintName();
			}
			else if(o1 instanceof LiteGraphDefinition && o2 instanceof LiteGraphDefinition)
			{
				thisVal = ((LiteGraphDefinition)o1).getName();
				anotherVal = ((LiteGraphDefinition)o2).getName();
			}
			else if(o1 instanceof LiteContactNotification && o2 instanceof LiteContactNotification)
			{
				thisVal = ((LiteContactNotification)o1).getNotification();
				anotherVal = ((LiteContactNotification)o2).getNotification();
			}			
			else if(o1 instanceof LiteYukonUser && o2 instanceof LiteYukonUser)
			{
				thisVal = ((LiteYukonUser)o1).getUsername();
				anotherVal = ((LiteYukonUser)o2).getUsername();
			}
			else if(o1 instanceof LiteYukonRole && o2 instanceof LiteYukonRole)
			{
				thisVal = ((LiteYukonRole)o1).getRoleName();
				anotherVal = ((LiteYukonRole)o2).getRoleName();
			}			
			else if(o1 instanceof LiteYukonGroup && o2 instanceof LiteYukonGroup)
			{
				thisVal = ((LiteYukonGroup)o1).getGroupName();
				anotherVal = ((LiteYukonGroup)o2).getGroupName();
			}			
			else if(o1 instanceof LiteYukonRoleProperty && o2 instanceof LiteYukonRoleProperty)
			{
				thisVal = ((LiteYukonRoleProperty)o1).getKeyName();
				anotherVal = ((LiteYukonRoleProperty)o2).getKeyName();
			}			
			else
			{	// unknown lite type
				throw new IllegalArgumentException("Unhandled lite types or the 2 objects being compared are not the same object types in comparator of : " + this.getClass().toString() );
			}

            int ret;
			try
            {
			    ret = thisVal.compareToIgnoreCase(anotherVal);
            }
            catch( java.lang.NullPointerException e )
            {
                String errMsg = new String();
                errMsg += o1.getClass();
                CTILogger.debug( "Unexpected Null Value: " + errMsg + " " , e );
                throw e;
            }
            
            return ret;

        }
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

}
