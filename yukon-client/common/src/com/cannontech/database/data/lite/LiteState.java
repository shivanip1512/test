package com.cannontech.database.data.lite;

/*
 */
public class LiteState extends LiteBase
{
	String stateText = null;
/**
 * LiteDevice
 */
public LiteState( int rawSt ) 
{
	super();
	setLiteID(rawSt);
	setLiteType(LiteTypes.STATE);
}
/**
 * LiteDevice
 */
public LiteState( int rawSt, String stText ) 
{
	super();
	setLiteID(rawSt);
	stateText = stText;
	setLiteType(LiteTypes.STATE);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getStateRawState() 
{
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getStateText() {
	return stateText;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateRawState(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateText(String newValue) {
	this.stateText = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return stateText;
}
}
