package com.cannontech.database.data.device;
/**
 * Insert the type's description here.
 * Creation date: (6/18/2002 2:40:24 PM)
 * @author: 
 */

import com.cannontech.database.db.device.*;

public class MCT_Broadcast extends CarrierBase {

	//contains com.cannontech.database.db.device.MCTBroadcastMapping
	private java.util.Vector MCTVector = null;
	
	private int mctOrderNum;

/**
 * MCT240 constructor comment.
 */
public MCT_Broadcast() {
	super();
}


/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();

	if( MCTVector != null )
	{

		
		com.cannontech.database.db.device.MCTBroadcastMapping.deleteAllBroadCastMappings(getDevice().getDeviceID(), getDbConnection());
		
		for( int j = 0; j < MCTVector.size(); j++ )
		{
			if(((MCTBroadcastMapping) MCTVector.elementAt(j)).getMctBroadcastID() == null)
				((MCTBroadcastMapping) MCTVector.elementAt(j)).setMctBroadcastID(this.getPAObjectID());
			
			((MCTBroadcastMapping) MCTVector.elementAt(j)).setOrdering(new Integer(j));
				
			((MCTBroadcastMapping) MCTVector.elementAt(j)).add();
			
		}
		
	}

	
}


/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	//nail all the MCTs mapped to this broadcast
	com.cannontech.database.db.device.MCTBroadcastMapping.deleteAllBroadCastMappings( getDevice().getDeviceID(), getDbConnection() );
	
	super.delete();

	setDbConnection(null);

	

}

	/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:11:50 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 2:21:11 PM)
 * @return int
 */
public int getMctOrderNum() {
	return mctOrderNum;
}


/**
 * Insert the method's description here.
 * Creation date: (6/21/2002 1:11:34 PM)
 * @return java.util.Vector
 */
public java.util.Vector getMCTVector() {
	if(MCTVector == null)
		MCTVector = new java.util.Vector();
	
	return MCTVector;
}


/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();

	try
	{
		com.cannontech.database.db.device.MCTBroadcastMapping[] mcts = com.cannontech.database.db.device.MCTBroadcastMapping.getAllMCTsList(getDevice().getDeviceID(), getDbConnection() );
		for( int i = 0; i < mcts.length; i++ )
		{
			mcts[i].setDbConnection(getDbConnection());
			getMCTVector().addElement( mcts[i] );
		}
	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection(conn);

	if( MCTVector != null )
	{
		for( int j = 0; j < MCTVector.size(); j++ )
			((MCTBroadcastMapping) MCTVector.elementAt(j)).setDbConnection( conn );
	}
	
}


 /**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID( deviceID );

	if(MCTVector != null)
	{
		for( int i = 0; i < MCTVector.size(); i++)
		{
			((MCTBroadcastMapping)MCTVector.elementAt(i)).setMctBroadcastID(deviceID);
		}
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2002 2:21:11 PM)
 * @param newMctOrderNum int
 */
public void setMctOrderNum(int newMctOrderNum) {
	mctOrderNum = newMctOrderNum;
}


/**
 * Insert the method's description here.
 * Creation date: (6/21/2002 1:11:34 PM)
 * @param newMCTVector java.util.Vector
 */
public void setMCTVector(java.util.Vector newMCTVector) {
	MCTVector = newMCTVector;
}


/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	
	super.update();

	if( MCTVector != null )
	{
		
		com.cannontech.database.db.device.MCTBroadcastMapping.deleteAllBroadCastMappings(getDevice().getDeviceID(), getDbConnection());
		
		for( int j = 0; j < MCTVector.size(); j++ )
		{
			if(((MCTBroadcastMapping) MCTVector.elementAt(j)).getMctBroadcastID() == null)
				((MCTBroadcastMapping) MCTVector.elementAt(j)).setMctBroadcastID(this.getPAObjectID());
			
			((MCTBroadcastMapping) MCTVector.elementAt(j)).setOrdering(new Integer(j));
				
			((MCTBroadcastMapping) MCTVector.elementAt(j)).add();
			
		}
		
	}
}

public final com.cannontech.common.util.NativeIntVector getAllMCTsIDList(Integer MCTBroadcastID ) throws java.sql.SQLException
{
	com.cannontech.common.util.NativeIntVector mctIntList = new com.cannontech.common.util.NativeIntVector(30);
	java.sql.Connection conn = null;

	try
     {
        conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		setDbConnection(conn);	
		
		mctIntList = com.cannontech.database.db.device.MCTBroadcastMapping.getAllMCTsIDList(MCTBroadcastID, conn);	

		// Lose the reference to the connection
        setDbConnection(null);
  
        return mctIntList;
        
     }
    	
     catch( java.sql.SQLException e )
     {
     	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
     	return null;
     }
     
     finally
     {   //make sure to close the connection
          try { if( conn != null ) conn.close(); } catch(java.sql.SQLException e2 ) { com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 ); };
     }

    

}
}