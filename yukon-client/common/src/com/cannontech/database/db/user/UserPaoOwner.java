package com.cannontech.database.db.user;

import com.cannontech.database.db.device.Device;
import java.util.ArrayList;
import com.cannontech.common.util.NativeIntVector;

/**
 * This table maps users to PAObjects. The mapping shows that a user has access
 * to the specified PAObject.
 * 
 *   A User can have 0..M PAObjects
 */
public class UserPaoOwner extends com.cannontech.database.db.DBPersistent 
{	
	private Integer userID = null;
	private Integer paoID = new Integer(Device.SYSTEM_DEVICE_ID);
	
	public final static String SETTER_COLUMNS[] = 
	{ 
		"PaoID"
	};

	public final static String CONSTRAINT_COLUMNS[] = { "UserID" };

	public final static String TABLE_NAME = "UserPaoOwner";

/**
 * UserPaoOwner constructor comment.
 */
public UserPaoOwner() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getUserID(), getPaoID() };

	add( TABLE_NAME, addValues );
}

public void addForUser(Integer userID, NativeIntVector paoIDs) throws java.sql.SQLException 
{
	for(int x = 0; x < paoIDs.size(); x++)
	{
		Object addValues[] = { userID, new Integer(paoIDs.elementAt(x)) };
		add( TABLE_NAME, addValues );
	}
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getUserID() );
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getUserID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{		
		setPaoID( (Integer) results[0] );
	}
	//do not throw an exception since we are not a 1-1 relationship
	
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object addValues[] = { getUserID(), getPaoID() };
    add( TABLE_NAME, addValues );
}

    /**
     * @return Returns the paoID.
     */
    public Integer getPaoID()
    {
        return paoID;
    }
    /**
     * @param paoID The paoID to set.
     */
    public void setPaoID(Integer paoID)
    {
        this.paoID = paoID;
    }
    /**
     * @return Returns the userID.
     */
    public Integer getUserID()
    {
        return userID;
    }
    /**
     * @param userID The userID to set.
     */
    public void setUserID(Integer userID)
    {
        this.userID = userID;
    }
    
	public static synchronized NativeIntVector getUserOwnedPaos( int userID_, java.sql.Connection conn )
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		NativeIntVector theIDs = new NativeIntVector(10);
		
		try 
		{		
			pstmt = conn.prepareStatement(
				"select PaoID " +
				"from " + TABLE_NAME + " " +
				"where UserID = ?" );
		    	
			pstmt.setInt( 1, userID_ );
			 
			rset = pstmt.executeQuery();
				
			//get the first returned result
			while( rset.next() )
			{
				theIDs.add( rset.getInt("PaoID") );
			}
		    
		}
		catch (java.sql.SQLException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if ( pstmt != null) pstmt.close();
			}
			catch (java.sql.SQLException e2) 
			{
				e2.printStackTrace();
			}
		}
		
		return theIDs;
	}
}
