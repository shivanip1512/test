package com.cannontech.database.data.fdr;

/**
 * Insert the type's description here.
 * Creation date: (5/22/2001 4:24:30 PM)
 * @author: 
 */
public class FDRInterface extends com.cannontech.database.db.DBPersistent
{
	private com.cannontech.database.db.point.fdr.FDRInterface fdrInterface = null;

	//holds instances of com.cannontech.database.db.point.fdr.FDRInterfaceOption
	private java.util.Vector interfaceOptionVector = null;
/**
 * FDRInterface constructor comment.
 */
public FDRInterface() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:39:43 PM)
 * @return com.cannontech.database.db.point.fdr.FDRInterface
 */
public com.cannontech.database.db.point.fdr.FDRInterface getFdrInterface() 
{
	if( fdrInterface == null )
		fdrInterface = new com.cannontech.database.db.point.fdr.FDRInterface();

	return fdrInterface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:25:25 PM)
 * @return java.util.Vector
 */
public java.util.Vector getInterfaceOptionVector() 
{
	if( interfaceOptionVector == null )
		interfaceOptionVector = new java.util.Vector(5);

	return interfaceOptionVector;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException {}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:39:43 PM)
 * @param newFdrInterface com.cannontech.database.db.point.fdr.FDRInterface
 */
public void setFdrInterface(com.cannontech.database.db.point.fdr.FDRInterface newFdrInterface) {
	fdrInterface = newFdrInterface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:25:25 PM)
 * @param newInterfaceOptionVector java.util.Vector
 */
public void setInterfaceOptionVector(java.util.Vector newInterfaceOptionVector) {
	interfaceOptionVector = newInterfaceOptionVector;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 5:12:09 PM)
 * @return java.lang.String
 */
public String toString() 
{
	if( getFdrInterface() != null )
			return getFdrInterface().getInterfaceName();
		
	return null;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException {}
}
