package com.cannontech.dbconverter.converter;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 12:09:23 PM)
 * @author: 
 */
class PtUnitRets 
{
	private int uomid = -1;
	private String uomName = null;
/**
 * PtUnitRets constructor comment.
 */
public PtUnitRets( int id, String name )
{
	super();
	uomid = id;
	uomName = name;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 12:10:37 PM)
 * @return int
 */
public int getUomid() {
	return uomid;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 12:10:37 PM)
 * @return java.lang.String
 */
public java.lang.String getUomName() {
	return uomName;
}
}
