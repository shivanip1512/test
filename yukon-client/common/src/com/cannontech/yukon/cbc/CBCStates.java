package com.cannontech.yukon.cbc;

/**
 */
public class CBCStates extends com.cannontech.yukon.cbc.CBCMessage {
	
	private java.util.Vector states;
/**
 * ScheduleCommand constructor comment.
 */
public CBCStates() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumberOfStates() {
	return states.size();
}
/**
 * This method was created in VisualAge.
 */
public com.cannontech.database.db.state.State getState(int index) {
	return (com.cannontech.database.db.state.State)states.get(index);
}
/**
 * This method was created in VisualAge.
 */
java.util.Vector getStates()
{
	return states;
}
/**
 * This method was created in VisualAge.
 */
void setStates(java.util.Vector allStates)
{
	states = allStates;
}
}
