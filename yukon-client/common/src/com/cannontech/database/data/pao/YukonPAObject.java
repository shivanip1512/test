package com.cannontech.database.data.pao;

/**
 * This type was created in VisualAge.
 */
public abstract class YukonPAObject extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange
{
	private com.cannontech.database.db.pao.YukonPAObject yukonPAObject = null;
/**
 * IEDMeter constructor comment.
 */
public YukonPAObject() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	getYukonPAObject().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:44:28 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	getYukonPAObject().add();	
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	
	deletePoints();

	//ADD TABLES THAT HAVE A REFERENCE TO THE YukonPAObject TABLE AND THAT
	// NEED TO BE DELETED WHEN A YukonPAObject ROW IS DELETED (CASCADE DELETE)
	delete( "DynamicPAOStatistics", "PAObjectID", getPAObjectID() );
   delete( "CommErrorHistory", "PAObjectID", getPAObjectID() );
   delete( "LMControlHistory", "PAObjectID", getPAObjectID() );
   delete( "PAOOwner", "ChildID", getPAObjectID() );

	getYukonPAObject().delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:32:35 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException 
{
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:40:47 PM)
 */
private void deletePoints() throws java.sql.SQLException
{

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List points = cache.getAllPoints();
		
		for( int i = 0; i < points.size(); i++ )
		{
			com.cannontech.database.data.lite.LitePoint pt = (com.cannontech.database.data.lite.LitePoint)points.get(i);

			if( pt.getPaobjectID() == getPAObjectID().intValue() )
			{
				com.cannontech.database.data.point.PointBase chubbyPt = com.cannontech.database.data.point.PointFactory.createPoint( pt.getPointType() );
				chubbyPt.setPointID( new Integer(pt.getPointID()) );
				chubbyPt.setDbConnection( getDbConnection() );

				chubbyPt.delete();
			}

		}
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	java.util.ArrayList list = new java.util.ArrayList(10);

	//add the basic change method
	list.add( new com.cannontech.message.dispatch.message.DBChangeMsg(
					getPAObjectID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_PAO_DB,
					getPAOCategory(),
					getPAOType(),
					typeOfChange ) );


	//if we are deleteing this PAO, we need to take in account the Points that also get deleted	
	if( typeOfChange == com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_DELETE )
	{
		//get all the point ids and their types that are owned by this PAObject
		int[][] idsAndTypes = com.cannontech.database.cache.functions.PAOFuncs.getAllPointIDsAndTypesForPAObject(
					getPAObjectID().intValue() );

		//add a new message for each point
		for( int i = 0; i < idsAndTypes.length; i++ )
		{
			com.cannontech.message.dispatch.message.DBChangeMsg msg = 
					new com.cannontech.message.dispatch.message.DBChangeMsg(
						idsAndTypes[i][0],
						com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_POINT_DB,
						com.cannontech.message.dispatch.message.DBChangeMsg.CAT_POINT,
						com.cannontech.database.data.point.PointTypes.getType( idsAndTypes[i][1] ),
						typeOfChange );
					
			list.add( msg );
		}	

	}
	
	 
	com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg[list.size()];
	return (com.cannontech.message.dispatch.message.DBChangeMsg[])list.toArray( dbChange );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public Integer getPAObjectID()
{
	return getYukonPAObject().getPaObjectID();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAOCategory()
{
	return getYukonPAObject().getCategory();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAOClass()
{
	return getYukonPAObject().getPaoClass();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAODescription()
{
	return getYukonPAObject().getDescription();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public Character getPAODisableFlag()
{
	return getYukonPAObject().getDisableFlag();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAOName()
{
	return getYukonPAObject().getPaoName();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAOStatistics()
{
	return getYukonPAObject().getPaoStatistics();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public String getPAOType()
{
	return getYukonPAObject().getType();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 3:52:13 PM)
 * @return com.cannontech.database.db.pao.YukonPAObject
 */
protected com.cannontech.database.db.pao.YukonPAObject getYukonPAObject() 
{
	if( yukonPAObject == null )
		yukonPAObject = new com.cannontech.database.db.pao.YukonPAObject();

	return yukonPAObject;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException 
{
	getYukonPAObject().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);

	getYukonPAObject().setDbConnection(conn);
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
protected void setPAObjectID( Integer id )
{
	getYukonPAObject().setPaObjectID( id );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setPAOCategory( String category )
{
	getYukonPAObject().setCategory( category );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setPAOClass( String newPAOClass)
{
	getYukonPAObject().setPaoClass( newPAOClass );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
protected void setPAOName( String name )
{
	getYukonPAObject().setPaoName( name );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setPAOStatistics( String newStats )
{
	getYukonPAObject().setPaoStatistics( newStats );
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 3:52:13 PM)
 * @param newYukonPAObject com.cannontech.database.db.pao.YukonPAObject
 */
protected void setYukonPAObject(com.cannontech.database.db.pao.YukonPAObject newYukonPAObject) {
	yukonPAObject = newYukonPAObject;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
	return getYukonPAObject().getPaoName();
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException 
{
	getYukonPAObject().update();
}
}
