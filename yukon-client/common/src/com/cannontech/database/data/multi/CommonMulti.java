package com.cannontech.database.data.multi;

/**
 * Insert the type's description here.
 * Creation date: (12/31/2001 11:40:21 AM)
 * @author: 
 */
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;

public abstract class CommonMulti extends com.cannontech.database.db.DBPersistent 
{
	private java.util.Vector dbPersistentVector = null;

	//Support to use the SUPER HACK in the add() method
	private boolean createNewPAOIDs = true;
/**
 * CommonMulti constructor comment.
 */
protected CommonMulti() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	// ************************** BEGIN SUPER HACK *******************************
	//Had to SUPER HACK this so two or more PAObjects would not be added with the
	//  same YukonPAOid
	if( getCreateNewPAOIDs() )
	{
		int yukonPAObjectsCnt = 0;
		int ptObjects = 0;
		for( int i = 0; i < getDBPersistentVector().size(); i++ )
			if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.pao.YukonPAObject )
				yukonPAObjectsCnt++;
			else if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.point.PointBase )
				ptObjects++;


		//there must be 2 or more PAObjects for us to even care
		if( yukonPAObjectsCnt > 1 )
		{
			int[] newIDS = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectIDs(yukonPAObjectsCnt);

			int j = 0;
			for( int i = 0; i < getDBPersistentVector().size(); i++ )
			{
				//all possible instances of com.cannontech.database.data.pao.YukonPAObject MUST be below			
				if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.device.DeviceBase )
				{
					((com.cannontech.database.data.device.DeviceBase)getDBPersistentVector().get(i)).setDeviceID( new Integer(newIDS[j++]) );
				}
				else if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.route.RouteBase )
				{
					((com.cannontech.database.data.route.RouteBase)getDBPersistentVector().elementAt(i)).setRouteID( new Integer(newIDS[j++]) );
				}
				else if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.port.DirectPort )
				{
					((com.cannontech.database.data.port.DirectPort)getDBPersistentVector().elementAt(i)).setPortID( new Integer(newIDS[j++]) );
				}
				//we will tell the programmer there is some sort of a problem since we made it this far!!!!
				else  if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.pao.YukonPAObject )
					throw new IllegalArgumentException( getDBPersistentVector().elementAt(i).getClass().getName() + " not present in: " + this.getClass().getName() + ".add()");
			}	

			
			//we must reassign Point PAOBjectIDs to the new PAObjectIDs
			if( ptObjects > 0 ) //be sure we have some points
			{
				//this is the only way we can update the points PAOBjectID, by ASSUMING the first YukonPAOBject is the owner of
				// following points, then any  YukonPAOBject after is the owner of any points thereafter.
				// So order of objects in the DBPersistenVector is important!!!
				Integer currentID = null;
				for( int i = 0; i < getDBPersistentVector().size(); i++ )
				{
					if( getDBPersistentVector().elementAt(i) instanceof com.cannontech.database.data.pao.YukonPAObject )
					{
						currentID = ((com.cannontech.database.data.pao.YukonPAObject)getDBPersistentVector().elementAt(i)).getPAObjectID();
					}
					
					if ( getDBPersistentVector().get(i) instanceof com.cannontech.database.data.point.PointBase )
						((com.cannontech.database.data.point.PointBase)getDBPersistentVector().get(i)).getPoint().setPaoID( currentID );
				}
			}				

	
		}

			
	}
	// ************************** END SUPER HACK *******************************
	
	//Orginal code is below	
	for(int i = 0; i < getDBPersistentVector().size(); i++)
	{			
		((DBPersistent)getDBPersistentVector().elementAt(i)).add();
	}

}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{

	//dont allow deletes, may get ugly!!
	com.cannontech.clientutils.CTILogger.info("Delete not allowed int " + this.getClass().getName() );
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @return boolean
 */
public boolean getCreateNewPAOIDs() {
	return createNewPAOIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 1:45:25 PM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
 */
public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	java.util.ArrayList list = new java.util.ArrayList(10);

	for( int i = 0; i < getDBPersistentVector().size(); i++ )
	{
		if( getDBPersistentVector().get(i) instanceof CTIDbChange )
		{
			//add the basic change method
			com.cannontech.message.dispatch.message.DBChangeMsg[] msgs = 
				((CTIDbChange)getDBPersistentVector().get(i)).getDBChangeMsgs(typeOfChange);

			for( int j = 0; j < msgs.length; j++ )
				list.add( msgs[j] );
		}

	}
	

	if( list.size() == 0 )
		return new com.cannontech.message.dispatch.message.DBChangeMsg[0];
	else
	{ 
		com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg[list.size()];
		return (com.cannontech.message.dispatch.message.DBChangeMsg[])list.toArray( dbChange );
	}

}
/**
 * This method was created in VisualAge.
 */
protected java.util.Vector getDBPersistentVector() 
{
	if( dbPersistentVector == null )
		dbPersistentVector = new java.util.Vector();
		
	return dbPersistentVector;
}
/**
 * Finds the first object of the given class in the Vector.
 * This method checks the first element and searches the entire
 * object hierarchary of that element for the Class, 
 * then moves to the next element and repeats.
 */
public static final DBPersistent getFirstObjectOfType( Class type, CommonMulti multi )
{
	if( multi != null )
	{
		for( int i = 0; i < multi.getDBPersistentVector().size(); i++ )
		{
         Class currClass = multi.getDBPersistentVector().get(i).getClass();
         
         while( currClass != null )
         {
   			if( currClass.equals(type) )
   				return (DBPersistent)multi.getDBPersistentVector().get(i);
            else
               currClass = currClass.getSuperclass();
         }
		}

	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 4:18:09 PM)
 * @param newCreateNewPAOIDs boolean
 */
public void setCreateNewPAOIDs(boolean newCreateNewPAOIDs) {
	createNewPAOIDs = newCreateNewPAOIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	for(int i = 0;i < getDBPersistentVector().size();i++)
	{
		((DBPersistent)getDBPersistentVector().elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.Device
 */
protected void setDBPersistentVector(java.util.Vector newValue) {
	this.dbPersistentVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {

	if( getDBPersistentVector().size() > 0 )
		return ((DBPersistent)getDBPersistentVector().elementAt(0)).toString();
	else
		return null;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	/* update(): Don't do this, MultiDBPersistent's
		 are used for coping multiple Objects at one time */
		 
	com.cannontech.clientutils.CTILogger.info("************************************************");
	com.cannontech.clientutils.CTILogger.info("update(): Don't do this; MultiDBPersistents");
	com.cannontech.clientutils.CTILogger.info("are used for copying multiple objects at one time");
	com.cannontech.clientutils.CTILogger.info("************************************************");
}
}
