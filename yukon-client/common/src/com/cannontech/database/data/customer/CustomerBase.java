package com.cannontech.database.data.customer;

/**
 * This type was created in VisualAge.
 */
public abstract class CustomerBase extends com.cannontech.database.data.pao.YukonPAObject
{	
/**
 * TwoWayDevice constructor comment.
 */
public CustomerBase() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public Integer getCustomerID()
{
	return getPAObjectID();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public String getCustomerName()
{
	return getYukonPAObject().getPaoName();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public String getCustomerType()
{
	return getYukonPAObject().getType();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public Character getDisableFlag()
{
	return getYukonPAObject().getDisableFlag();
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public void setCustomerID( Integer id )
{
	setPAObjectID(id);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public void setCustomerName( String name )
{
	getYukonPAObject().setPaoName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public void setCustomerType( String type )
{
	getYukonPAObject().setType( type );
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 4:27:13 PM)
 */
public void setDisableFlag( Character flag )
{
	getYukonPAObject().setDisableFlag( flag );
}
}
