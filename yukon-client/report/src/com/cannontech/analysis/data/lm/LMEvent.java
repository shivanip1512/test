/*
 * Created on Nov 23, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.data.lm;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.cannontech.database.db.company.SettlementConfig;
import com.cannontech.stars.util.SettlementConfigFuncs;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LMEvent
{
	/** Defined event types */
	public static final int UNKNOWN_EVENT = 0;
	public static final int DISPATCHED_EVENT = 1;
	public static final int UF_EVENT = 2;
	public static final int EMERGENCY_EVENT = 3;
		
    private String groupName = null;
	/** Load Group ID */
	private Integer groupID = null;
	/** Actual event start dateTime */
	private Date actualStartDateTime = null;
	/** Actual event stop dateTime */
	private Date actualStopDateTime = null;
	/** The duration in minutes used for calculating the event Rounded Duration time.*/ 
	private int restoreDuration = 15;
	private int emergencyDelay = 0;
	private int dispatchedDelay = 0;
	private int ufDelay = 15;
	
	private Date roundedStartDateTime = null;
	private Date roundedStopDateTime = null;
	
	/** percentage of hour/mins (NOT actual time)*/
	private Double roundedDuration = null; 
	
	private Date settlementStartDateTime = null;
	private Date settlementStopDateTime = null;
	/** adjusted for the event type delay */
	private Date adjustedStartDateTime = null;
	/** adjusted for the event type delay*/
	private Date adjustedStopDateTime = null;
	
	// Fields set based on other fields (see contructor)
	private int interval = 15;
	private int eventType = -1;	//some undefined event type int
	private HashMap lmEventCustomerMap = new HashMap();
	
	private String UNKNOWN_EVENT_STRING = "Unknown";
	private String DISPATCHED_EVENT_STRING = "Dispatched";
	private String UF_EVENT_STRING = "Under Frequency";
	private String EMERGENCY_EVENT_STRING = "Emergency";
	
	public static void main(String[] args)
	{
		GregorianCalendar x = new GregorianCalendar();
		x.set(Calendar.HOUR_OF_DAY, 15 );
		x.set(Calendar.MINUTE, 0);
		x.set(Calendar.SECOND, 0 );
		x.set(Calendar.MILLISECOND, 0 );

		GregorianCalendar y = new GregorianCalendar();
		y.set(Calendar.HOUR_OF_DAY, 15 );
		y.set(Calendar.MINUTE, 45 );
		y.set(Calendar.SECOND, 0 );
		y.set(Calendar.MILLISECOND, 0 ); 
		
		/*LMEvent e = new LMEvent(new Integer(0), x.getTime(), y.getTime(), new Integer(-1), null);
		System.out.println("Actual  Start: " + e.getActualStartDateTime());
		System.out.println("Actual   Stop: " + e.getActualStopDateTime());
		System.out.println("Rounded Start: " + e.getRoundedStartDateTime());
		System.out.println("Rounded  Stop: " + e.getRoundedStopDateTime());
		System.out.println("Duration: " + e.getRoundedDuration());*/
	}
	/**
	 * 
	 */
	public LMEvent(String groupName_, Integer groupID_, Date actualStart_, Date actualStop_, Integer energyCompanyID_, 
                    Integer[] customerIDs, Double [] custDemandLevels, Double [] custCurtailLoads)
	{
        groupName = groupName_;
		groupID = groupID_;
		actualStartDateTime = actualStart_;
		actualStopDateTime = actualStop_;
		if( energyCompanyID_ != null)
		{
			restoreDuration = Integer.valueOf(SettlementConfigFuncs.getLiteSettlementConfig(SettlementConfig.HECO_RESTORE_DURATION_STRING));
			emergencyDelay = Integer.valueOf(SettlementConfigFuncs.getLiteSettlementConfig(SettlementConfig.HECO_EMERGENCY_DELAY_STRING)); 
			dispatchedDelay = Integer.valueOf(SettlementConfigFuncs.getLiteSettlementConfig(SettlementConfig.HECO_DISPATCHED_DELAY_STRING));
			ufDelay = Integer.valueOf(SettlementConfigFuncs.getLiteSettlementConfig(SettlementConfig.HECO_UF_DELAY_STRING));
		}

		for ( int i = 0; i < customerIDs.length; i++)
		{
			getLmEventCustomerMap().put(customerIDs[i], new LMEventCustomer(customerIDs[i], custDemandLevels[i], custCurtailLoads[i]));
		}
	}

	/**
	 * The actual lm event stop date and time.
	 * @return Date actualStartDateTime
	 */
	public Date getActualStartDateTime()
	{
		return actualStartDateTime;
	}
	
	/**
	 * The actual lm event stop date and time.
	 * @return Date actualStopDateTime
	 */
	public Date getActualStopDateTime()
	{
		return actualStopDateTime;
	}

	/**
	 * The id of the load group for the lm event. 
	 * @return groupID
	 */
	public Integer getGroupID()
	{
		return groupID;
	}

    /**
     * The name of the load group for the lm event.
     * @return
     */
    public String getGroupName() {
        return groupName;
    }
    
	/**
	 * Returns the rounded duration of an event.  Takes the rounded difference between the start and stop times 
	 * and then adds the restoreDuration interval to that duration.
	 * @return Double roundedDuration
	 */
	public Double getRoundedDuration()
	{
		if( roundedDuration == null)
		{
			double x= (getRoundedStopDateTime().getTime() - getRoundedStartDateTime().getTime()) / 3600000d;
			// Add the restore duration to rounded event time
			x = x + (getRestoreDuration()/60d);	//cast to double or we loose the decimal value
			roundedDuration = new Double(x);
		}
		return roundedDuration;
	}

	/**
	 * Returns the rounded start date and time.
	 * Takes the actual startDateTime and rounds to the closest interval BEFORE the actual start time.
	 * If the actual start time is on an exact interval, then the rounded start time is the same as the actual start.
	 * @return Date roundedStartDateTime
	 */
	public Date getRoundedStartDateTime()
	{
		if (roundedStartDateTime == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getActualStartDateTime().clone());
			//round seconds and millis away
			tempCal.set(Calendar.SECOND, 0);
			tempCal.set(Calendar.MILLISECOND, 0);
			int min = tempCal.get(Calendar.MINUTE);
			if( min < 15)
				min = 0;
			else if (min < 30)
				min = 15;
			else if (min < 45)
				min = 30;
			else
				min = 45;
			tempCal.set(Calendar.MINUTE, min);
			roundedStartDateTime = (Date)tempCal.getTime().clone();
		}
		return roundedStartDateTime;
	}

	/**
	 * Returns the rounded stop date and time.
	 * Takes the actual stopDateTime and rounds to the closest interval AFTER the actual stop time.
	 * If the actual stop time is on an exact interval, then the rounded stop time is the same as the actual stop time.
	 * @return Date roundedStopDateTime
	 */
	public Date getRoundedStopDateTime()
	{
		if( roundedStopDateTime == null)
		{
			GregorianCalendar  tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getActualStopDateTime().clone());
//			round seconds and millis away
			tempCal.set(Calendar.SECOND, 0);
			tempCal.set(Calendar.MILLISECOND, 0);
		
			int min = tempCal.get(Calendar.MINUTE);
			if( min > 45)	//inc hour of day if interval pushed from 45 to 0 (of next hour)
			{
				min = 0;
				tempCal.add(Calendar.HOUR_OF_DAY, 1);
			}
			else if( min > 30)
				min = 45;
			else if( min > 15)
				min = 30;
			else if ( min > 0 )
				min = 15;
			else
				min = 0;
			tempCal.set(Calendar.MINUTE, min);
			roundedStopDateTime = (Date)tempCal.getTime().clone();
		}
		return roundedStopDateTime;
	}
	
	/**
	 * Returns the settlement start date and time.
	 * Takes the rounded startDateTime and ADDS one interval (for example: 15 minutes) to it.
	 * @return Date settlementStartDateTime
	 */
	public Date getSettlementStartDateTime()
	{
		if (settlementStartDateTime == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getRoundedStartDateTime().clone());
			//round seconds and millis away
			tempCal.add(Calendar.MINUTE, interval);
			settlementStartDateTime = (Date)tempCal.getTime().clone();
		}
		return settlementStartDateTime;
	}

	/**
	 * Returns the settlement stop date and time.
	 * Takes the rounded stopDateTime and SUBTRACTS one interval (for example: 15 minutes) from it.
	 * @return Date settlementStopDateTime
	 */
	public Date getSettlementStopDateTime()
	{
		if (settlementStopDateTime == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getRoundedStopDateTime().clone());
			//round seconds and millis away
			tempCal.add(Calendar.MINUTE, -interval);
			settlementStopDateTime = (Date)tempCal.getTime().clone();
		}
		return settlementStopDateTime;
	}	
	
	/**
	 * Returns the adjusted StartDateTime.
	 * Takes the settlement startDateTIme and ADDS the associated eventType delay (in minutes) to it.
	 * @return Date adjustedStartDateTime
	 */
	public Date getAdjustedStartDateTime()
	{
		if (adjustedStartDateTime == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getSettlementStartDateTime().clone());
			//adjust based on settlementStartDateTime and event type delay.
			if( getEventType() == DISPATCHED_EVENT)
				tempCal.add(Calendar.MINUTE, dispatchedDelay);
			else if( getEventType() == UF_EVENT)
				tempCal.add(Calendar.MINUTE, ufDelay);
			else if( getEventType() == EMERGENCY_EVENT)
				tempCal.add(Calendar.MINUTE, emergencyDelay);
				
			adjustedStartDateTime = (Date)tempCal.getTime().clone();
		}
		return adjustedStartDateTime;
	}
	/**
	 * Returns the adjusted StopDateTime
	 * Takes the settlement stopDateTime and simply returns it.  No delays are implemented.
	 * @return Date adjustedStopDateTime
	 */
	public Date getAdjustedStopDateTime()
	{
		if (adjustedStopDateTime == null)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTime((Date)getSettlementStopDateTime().clone());
			// Adjusted stopDateTime is the same as the SettlementStopDateTime
			adjustedStopDateTime = (Date)tempCal.getTime().clone();
		}
		return adjustedStopDateTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String retStr = "GroupID: " + getGroupID() + "\tDuration: " + getRoundedDuration() + "\tEventType: " + getEventTypeString() +
			"\r\nEvent          : " + getActualStartDateTime() + " - " + getActualStopDateTime() + 
			"\r\nRoundedEvent   : " + getRoundedStartDateTime() + " - " + getRoundedStopDateTime() +
			"\r\nSettlementEvent: " + getSettlementStartDateTime() + " - " + getSettlementStopDateTime() + 
			"\r\nAdjustedEvent  : " + getAdjustedStartDateTime() + 
			"\r\n";

		return retStr;
	}
	/**
	 * Returns a string value representing the type of event.  See defined event types in LMEvent class. 
	 * @return String
	 */
	public String getEventTypeString()
	{
		switch (getEventType())
		{
			case DISPATCHED_EVENT:
				return DISPATCHED_EVENT_STRING;
			case UF_EVENT:
				return UF_EVENT_STRING;
			case EMERGENCY_EVENT:
				return EMERGENCY_EVENT_STRING;
			case UNKNOWN_EVENT:
			default :
				return UNKNOWN_EVENT_STRING;
		}
	}
	
	/**
	 * Returns the int value representing the type of event.  See defined event types in LMEvent class.
	 * @return eventType
	 */
	public int getEventType()
	{
		if( eventType < 0)
		{
			if( getGroupName().toLowerCase().indexOf("emer") > -1)
				eventType = EMERGENCY_EVENT;
			else if( groupName.toLowerCase().indexOf("under") > -1) 
				eventType = UF_EVENT;
			else if( groupName.toLowerCase().indexOf("disp") > -1)	//Do DISPATCH last, since it could very well end up in other event type strings.
				eventType = DISPATCHED_EVENT;
			else
				eventType = UNKNOWN_EVENT;
		}
		return eventType;
	}
	
	/**
	 * Returns the duration in minutes used for calculating the event Rounded Duration time.  
	 * @return restoreDuration
	 */
	private int getRestoreDuration()
	{
		return restoreDuration;
	}

	/**
	 * If (adjustedStartDateTime >= getAdjustedStopDateTime) FALSE;
	 * If ( (Emergency OR Dispatched) OR ( UFEvent AND RoundedDuration > 1) ) TRUE;
	 * Otherwise FALSE;
	 * @return
	 */
	public boolean isERIPaymentEvent()
	{
		if( getAdjustedStartDateTime().getTime() > getAdjustedStopDateTime().getTime())
			return false;
			
		if( (getEventType() == EMERGENCY_EVENT || getEventType() == DISPATCHED_EVENT) ||
			( getEventType() == UF_EVENT && getRoundedDuration().doubleValue() > 1) )
			return true;
			
		return false;	
	}
	
	/**
	 * A map of Integer(CustomerID) to LMEventCustomer Object
	 * @return lmEventCustomerMap
	 */
	public HashMap getLmEventCustomerMap()
	{
		return lmEventCustomerMap;
	}
}
