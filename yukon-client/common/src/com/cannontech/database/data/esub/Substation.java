package com.cannontech.database.data.esub;

/**
 * Insert the type's description here.
 * Creation date: (12/18/2000 2:27:40 PM)
 * @author: 
 */
public class Substation extends com.cannontech.database.db.DBPersistent {
	private com.cannontech.database.db.esub.Substation substation;
	
	private java.util.ArrayList dcbList = new java.util.ArrayList();
	private java.util.ArrayList tcbList = new java.util.ArrayList();
	private java.util.ArrayList xfmrList = new java.util.ArrayList();
	
/**
 * Substation constructor comment.
 */
public Substation() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("NOT IMPLEMENTED!");
/*	java.sql.Connection conn = getDbConnection();

	substation.setDbConnection(conn);
	substation.add();
	substation.setDbConnection(null);

	java.util.Iterator iter = getDcbList().iterator();
	while( iter.hasNext() )
	{
		com.cannontech.database.db.esub.DCB dcb = (com.cannontech.database.db.esub.DCB) iter.next();

		dcb.setDbConnection(conn);
		dcb.add();
		dcb.delete();
	}
*/
	/*iter = getTcbList().iterator();
	while( iter.hasNext() )
	{
		com.cannontech.database.db.esub.TCB tcb = (com.cannontech.database.db.esub.TCB) iter.next();

		dcb.setDbConnection(conn);
		dcb.add();
		dcb.delete();
	}

	iter = getXfmrList().iterator();	
	while( iter.hasNext() )
	{
		com.cannontech.database.db.esub.XFMR dcb = (com.cannontech.database.db.esub.XFMR) iter.next();

		dcb.setDbConnection(conn);
		dcb.add();
		dcb.delete();
	}	*/
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("NOT IMPLEMENTED!");
	/*java.sql.Connection conn = getDbConnection();
	
	java.util.Iterator iter = getDcbList().iterator();
	while( iter.hasNext() )
	{
		com.cannontech.database.db.esub.DCB dcb = (com.cannontech.database.db.esub.DCB) iter.next();
		dcb.setDbConnection(conn);
		dcb.delete();
		dcb.setDbConnection(null);
	}
*/
	/*iter = getTcbList().iterator();
	while( iter.hasNext())
	{
		com.cannontech.database.db.esub.TCB tcb = (com.cannontech.database.db.esub.TCB) iter.next();
		tcb.setDbConnection(conn);
		tcb.delete();
		tcb.setDbConnection(null);
	}

	iter = getXfmrList().iterator();
	while( iter.hasNext() )
	{
		com.cannontech.database.db.esub.XFMR xfmr = (com.cannontech.database.db.esub.XFMR) iter.next();
		xfmr.setDbConnection(conn);
		xfmr.delete();
		xfrm.setDbConnection(null);
	}

	substation.setDbConnection(conn);
	substation.delete();
	substation.setDbConnection(null);
*/
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getDcbList() {
	return dcbList;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @return com.cannontech.database.db.esub.Substation
 */
public com.cannontech.database.db.esub.Substation getSubstation() {
	return substation;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getTcbList() {
	return tcbList;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getXfmrList() {
	return xfmrList;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	java.sql.Connection conn = getDbConnection();
	
	substation.setDbConnection(conn);
	substation.retrieve();
	substation.setDbConnection(null);
	
	com.cannontech.database.db.esub.DCB[] dcb = com.cannontech.database.db.esub.DCB.getAllDCBs(substation.getID(), conn.toString() );

	java.util.ArrayList l = new java.util.ArrayList( dcb.length );
	for( int i = 0; i < dcb.length; i++ )
		l.add(dcb[i]);
	
	setDcbList(l);

	com.cannontech.database.db.esub.TCB[] tcb = com.cannontech.database.db.esub.TCB.getAllTCBs(substation.getID(), conn.toString() );

	l = new java.util.ArrayList( tcb.length );
	for( int i = 0; i < tcb.length; i++ )
		l.add(tcb[i]);
	
	setTcbList(l);

	com.cannontech.database.db.esub.XFMR[] xfmr = com.cannontech.database.db.esub.XFMR.getAllXFMRs(substation.getID(), conn.toString() );

	l = new java.util.ArrayList( xfmr.length );
	for( int i = 0; i < xfmr.length; i++ )
		l.add(xfmr[i]);
	
	setXfmrList(l);	
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @param newDcbList java.util.ArrayList
 */
public void setDcbList(java.util.ArrayList newDcbList) {
	dcbList = newDcbList;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @param newSubstation com.cannontech.database.db.esub.Substation
 */
public void setSubstation(com.cannontech.database.db.esub.Substation newSubstation) {
	substation = newSubstation;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @param newTcbList java.util.ArrayList
 */
public void setTcbList(java.util.ArrayList newTcbList) {
	tcbList = newTcbList;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 9:57:33 AM)
 * @param newXfmrList java.util.ArrayList
 */
public void setXfmrList(java.util.ArrayList newXfmrList) {
	xfmrList = newXfmrList;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("Not yet implemented");
}
}
