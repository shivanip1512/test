package com.cannontech.database.data.capcontrol;

/**
 * This type was created in VisualAge.
 */
public class CapControlFeeder extends CapControlYukonPAOBase implements com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.capcontrol.CapControlFeeder capControlFeeder = null;

	private java.util.Vector ccBankListVector = null;
/**
 */
public CapControlFeeder() {
	super();
}
/**
 */
public CapControlFeeder(Integer feedID) 
{
	super();
	setCapControlPAOID( feedID );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getPAObjectID() == null )
		setCapControlPAOID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	super.add();
	
	getCapControlFeeder().add();
	
	for( int i = 0; i < getCcBankListVector().size(); i++ )
	{
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getCcBankListVector().elementAt(i)).add();
	}
	
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{

	//remove all the associations of CapBanks to this Feeder
	com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList( 
					getCapControlPAOID(), null, getDbConnection() );

	//remove all the associations of this Feeder to any SubBus
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
					null, getCapControlPAOID(), getDbConnection() );

	//Delete from all dynamic Feeder cap control tables here
	delete("DynamicCCFeeder", "FeederID", getCapControlPAOID() );

	
	getCapControlFeeder().delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @return com.cannontech.database.db.capcontrol.CapControlFeeder
 */
public com.cannontech.database.db.capcontrol.CapControlFeeder getCapControlFeeder() 
{
	if( capControlFeeder == null )
		capControlFeeder = new com.cannontech.database.db.capcontrol.CapControlFeeder();

	return capControlFeeder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @return java.util.Vector
 */
public java.util.Vector getCcBankListVector() 
{
	if( ccBankListVector == null )
		ccBankListVector = new java.util.Vector(20);
		
	return ccBankListVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	
	getCapControlFeeder().retrieve();
	
	ccBankListVector = com.cannontech.database.db.capcontrol.CCFeederBankList.getCapBanksOnFeederList(
		getCapControlPAOID(), getDbConnection() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @param newCapControlFeeder com.cannontech.database.db.capcontrol.CapControlFeeder
 */
public void setCapControlFeeder(com.cannontech.database.db.capcontrol.CapControlFeeder newCapControlFeeder) {
	capControlFeeder = newCapControlFeeder;
}
/**
 * This method was created in VisualAge.
 */
public void setCapControlPAOID(Integer feedID)
{
	super.setPAObjectID( feedID );
	getCapControlFeeder().setFeederID( feedID );
	
	for( int i = 0; i < getCcBankListVector().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getCcBankListVector().elementAt(i)).setFeederID( feedID );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @param newCcBankListVector java.util.Vector
 */
public void setCcBankListVector(java.util.Vector newCcBankListVector) {
	ccBankListVector = newCcBankListVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection( conn );
	getCapControlFeeder().setDbConnection(conn);
	
	for (int i = 0; i < getCcBankListVector().size(); i++)
		 ((com.cannontech.database.db.capcontrol.CCFeederBankList) getCcBankListVector().elementAt(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	
	getCapControlFeeder().update();
	
	com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList( 
			getCapControlPAOID(), null, getDbConnection() );

	for( int i = 0; i < getCcBankListVector().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getCcBankListVector().elementAt(i)).add();
}
}
