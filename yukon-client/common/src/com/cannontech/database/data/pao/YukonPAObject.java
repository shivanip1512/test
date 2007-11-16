package com.cannontech.database.data.pao;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.device.configuration.dao.DeviceConfigurationDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.database.db.NestedDBPersistentComparators;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.db.pao.PAOScheduleAssign;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public abstract class YukonPAObject extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange
{
	private com.cannontech.database.db.pao.YukonPAObject yukonPAObject = null;
	
	//contains com.cannontech.database.db.pao.PAOExclusion
	private Vector paoExclusionVector = null;

	//may have zero or more schedules, instance of PAOScheduleAssign
	private ArrayList<PAOScheduleAssign> schedules = null;

/**
 * YukonPAObject constructor comment.
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
	
	for( int i = 0; i < getPAOExclusionVector().size(); i++ )
		((DBPersistent)getPAOExclusionVector().get(i)).add();

	for( int i = 0; i < getSchedules().size(); i++ )
		((DBPersistent)getSchedules().get(i)).add();
}

/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:44:28 AM)
 * @param deviceID int
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	add();
}

/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	
	deletePoints();
    deleteDeviceConfigurationMapping();

	//ADD TABLES THAT HAVE A REFERENCE TO THE YukonPAObject TABLE AND THAT
	// NEED TO BE DELETED WHEN A YukonPAObject ROW IS DELETED (CASCADE DELETE)
	delete( "DynamicPAOStatistics", "PAObjectID", getPAObjectID() );
	delete( "CommErrorHistory", "PAObjectID", getPAObjectID() );
	delete( "LMControlHistory", "PAObjectID", getPAObjectID() );
    delete ("DynamicLMControlHistory","PAObjectID", getPAObjectID() );
	delete( "PAOOwner", "ChildID", getPAObjectID() );
    // Remove pao permissions
    PaoPermissionService paoPermissionService = (PaoPermissionService) YukonSpringHook.getBean("paoPermissionService");
    paoPermissionService.removeAllPaoPermissions(getPAObjectID());
    
    delete( "DynamicPAOInfo", "PAObjectID", getPAObjectID() );

	PAOExclusion.deleteAllPAOExclusions( getPAObjectID().intValue(), getDbConnection() );
	
	PAOScheduleAssign.deleteAllPAOExclusions( getPAObjectID().intValue(), getDbConnection() );
	
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
 * Helper method to delete the device / configuration mapping if present
 */
private void deleteDeviceConfigurationMapping(){
    DeviceConfigurationDao.removeConfigAssignmentForDevice(getPAObjectID());
}

/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:40:47 PM)
 */
private void deletePoints() throws java.sql.SQLException
{
    List<LitePoint> points = DaoFactory.getPointDao().getLitePointsByPaObjectId(getPAObjectID());
    for (LitePoint point : points) {
        com.cannontech.database.data.point.PointBase chubbyPt = com.cannontech.database.data.point.PointFactory.createPoint( point.getPointType() );
        chubbyPt.setPointID(point.getPointID());
        chubbyPt.setDbConnection( getDbConnection() );

        chubbyPt.delete();
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
		int[][] idsAndTypes = DaoFactory.getPointDao().getAllPointIDsAndTypesForPAObject(getPAObjectID());

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
	

	getPAOExclusionVector().removeAllElements();
	Vector exc =
		PAOExclusion.getAllPAOExclusions( getPAObjectID().intValue(), getDbConnection() );	
	for( int i = 0; i < exc.size(); i++ )
		getPAOExclusionVector().add( exc.elementAt(i) );	



	getSchedules().clear();
	PAOScheduleAssign[] paoScheds =
		PAOScheduleAssign.getAllPAOSchedAssignments(getPAObjectID().intValue(), getDbConnection());	
	for( int i = 0; i < paoScheds.length; i++ )
		getSchedules().add( paoScheds[i] );	
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

	for( int i = 0; i < getPAOExclusionVector().size(); i++ )
		((DBPersistent)getPAOExclusionVector().get(i)).setDbConnection( conn );

	for( int i = 0; i < getSchedules().size(); i++ )
		((DBPersistent)getSchedules().get(i)).setDbConnection( conn );
}

/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
protected void setPAObjectID( Integer id )
{
	getYukonPAObject().setPaObjectID( id );
	
	for( int i = 0; i < getPAOExclusionVector().size(); i++ )
	{
		//we must null out the PK since there is a new PAObjectID owner
		((PAOExclusion)getPAOExclusionVector().get(i)).setExclusionID( null );

		((PAOExclusion)getPAOExclusionVector().get(i)).setPaoID( id );
	}

	
	for( int i = 0; i < getSchedules().size(); i++ ) {
		//we must null out the PK since there is a new PAObjectID owner
		((PAOScheduleAssign)getSchedules().get(i)).setEventID( null );
		((PAOScheduleAssign)getSchedules().get(i)).setPaoID( id );
	}

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
public void setPAOName( String name )
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

public boolean isDisabled() {
	return CtiUtilities.isTrue( getYukonPAObject().getDisableFlag() );
}

public void setDisabled( boolean val ) {
	getYukonPAObject().setDisableFlag(
		val ? CtiUtilities.trueChar : CtiUtilities.falseChar );
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
	
	//grab all the previous PAOExclusion entries for this object
	java.util.Vector oldPAOExclusion = PAOExclusion.getAllPAOExclusions(getPAObjectID().intValue(), getDbConnection());
	
	//unleash the power of the NestedDBPersistent
	Vector exclusionVector = NestedDBPersistentComparators.NestedDBPersistentCompare(oldPAOExclusion, getPAOExclusionVector(), NestedDBPersistentComparators.paoExclusionComparator);

	//throw the PAOExclusions into the Db
	for( int i = 0; i < exclusionVector.size(); i++ )
	{
		((NestedDBPersistent)exclusionVector.elementAt(i)).setDbConnection(getDbConnection());
		((NestedDBPersistent)exclusionVector.elementAt(i)).executeNestedOp();

	}

	//compeletly remove and add the entire set of PAOScheduleAssignments
	PAOScheduleAssign.deleteAllPAOExclusions( getPAObjectID().intValue(), getDbConnection() );
	for( int i = 0; i < getSchedules().size(); i++ )
		((PAOScheduleAssign)getSchedules().get(i)).add();
}

	/**
	 * @return Vector
	 */
	public Vector getPAOExclusionVector()
	{
		if( paoExclusionVector == null )
			paoExclusionVector = new Vector();

		return paoExclusionVector;
	}

	/**
	 * @return
	 */
	public ArrayList<PAOScheduleAssign> getSchedules() {
		
		if( schedules == null )
			schedules = new ArrayList<PAOScheduleAssign>(8);

		return schedules;
	}

	/**
	 * @param assigns
	 */
	public void setSchedules(ArrayList assigns) {
		schedules = assigns;
	}

}
