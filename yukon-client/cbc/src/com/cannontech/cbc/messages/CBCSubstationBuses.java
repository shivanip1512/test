package com.cannontech.cbc.messages;

/**
 */

import com.cannontech.cbc.data.SubBus;

public class CBCSubstationBuses extends com.cannontech.cbc.messages.CBCMessage {
	
	private java.util.Vector buses;
/**
 * ScheduleCommand constructor comment.
 */
public CBCSubstationBuses() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfBuses() 
{
	return buses.size();
}
/**
 * This method was created in VisualAge.
 */
public SubBus getSubBusAt(int index) 
{
	if( buses == null || index < 0 || index >= buses.size() )
		return null;
	else
		return (SubBus)buses.get(index);
}
/**
 * This method was created in VisualAge.
 */
void setSubBuses(java.util.Vector subbuses)
{
	buses = subbuses;
}
}
