package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubBus extends CapControlYukonPAOBase implements com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.capcontrol.CapControlSubstationBus capControlSubstationBus = null;

	//contains objects of type com.cannontech.database.db.capcontrol.CCFeederSubAssignment
	private java.util.Vector ccFeederListVector = null;
/**
 */
public CapControlSubBus() {
	super();
}
/**
 */
public CapControlSubBus(Integer subBusID) 
{
	super();
	setCapControlPAOID( subBusID );
}

/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getCapControlPAOID() == null )
		setCapControlPAOID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	super.add();
	getCapControlSubstationBus().add();
	
	for( int i = 0; i < getCcFeederListVector().size(); i++ )
	{
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getCcFeederListVector().elementAt(i)).add();
	}
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{

	//remove all the associations of feeders to this SubstationBus
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
					getCapControlPAOID(), null, getDbConnection() );


	//Delete from all dynamic SubBus cap control tables here
	delete("DynamicCCSubstationBus", "SubstationBusID", getCapControlPAOID() );
	
	getCapControlSubstationBus().delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @return com.cannontech.database.db.capcontrol.CapControlSubstationBus
 */
public com.cannontech.database.db.capcontrol.CapControlSubstationBus getCapControlSubstationBus() 
{
	if( capControlSubstationBus == null )
		capControlSubstationBus = new com.cannontech.database.db.capcontrol.CapControlSubstationBus();

	return capControlSubstationBus;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @return java.util.Vector
 */
public java.util.Vector getCcFeederListVector() 
{
	if( ccFeederListVector == null )
		ccFeederListVector = new java.util.Vector(20);

	return ccFeederListVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	
	getCapControlSubstationBus().retrieve();
	
	ccFeederListVector = com.cannontech.database.db.capcontrol.CCFeederSubAssignment.getCCFeedersOnSub(
		getPAObjectID(), getDbConnection() );
}
/**
 * This method was created in VisualAge.
 */
public void setCapControlPAOID(Integer subBusID) 
{
	super.setPAObjectID( subBusID );
	getCapControlSubstationBus().setSubstationBusID( subBusID );
	
	for( int i = 0; i < getCcFeederListVector().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getCcFeederListVector().elementAt(i)).setSubstationBusID( subBusID );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @param newCapControlSubstationBus com.cannontech.database.db.capcontrol.CapControlSubstationBus
 */
public void setCapControlSubstationBus(com.cannontech.database.db.capcontrol.CapControlSubstationBus newCapControlSubstationBus) {
	capControlSubstationBus = newCapControlSubstationBus;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @param newCcFeederListVector java.util.Vector
 */
public void setCcFeederListVector(java.util.Vector newCcFeederListVector) {
	ccFeederListVector = newCcFeederListVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection( conn );
	getCapControlSubstationBus().setDbConnection(conn);
	
	for (int i = 0; i < getCcFeederListVector().size(); i++)
		 ((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getCcFeederListVector().elementAt(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	
	getCapControlSubstationBus().update();
	
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
			getCapControlPAOID(), null, getDbConnection() );

	for( int i = 0; i < getCcFeederListVector().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getCcFeederListVector().elementAt(i)).add();
}
}
