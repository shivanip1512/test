package com.cannontech.database.data.port;

import java.sql.SQLException;

import com.cannontech.database.db.pao.PAOowner;

/**
 * This type was created in VisualAge.
 */
public class PooledPort extends DirectPort 
{	
	//contains instances of com.cannontech.database.db.pao.PAOowner
	private java.util.Vector portVector = null;

	/**
	 * PooledPort constructor comment.
	 */
	public PooledPort() {
		super();
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	public void add() throws java.sql.SQLException
	{
		super.add();
	
		for( int i = 0; i < getPortVector().size(); i++ )
			((PAOowner) getPortVector().get(i)).add();
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException
	{	
		PAOowner.deleteAllPAOowners( getPAObjectID(), getDbConnection() );
	
		super.delete();
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.util.Vector
	 */
	public java.util.Vector getPortVector()
	{	
		if( portVector == null )
			portVector = new java.util.Vector();
		
		return portVector;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.util.Vector
	 */
	public void setPortVector( java.util.Vector portVect_ )
	{	
		portVector = portVect_;
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		super.retrieve();
	
		try
		{
			
			PAOowner pArray[] = PAOowner.getAllPAOownerChildren( getPAObjectID(), getDbConnection() );

			for( int i = 0; i < pArray.length; i++ )
			{
				getPortVector().addElement( pArray[i] );
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
	
		for( int i = 0; i < getPortVector().size(); i++ )
			((PAOowner) getPortVector().get(i)).setDbConnection(conn);
	}

	
	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException
	{
		super.update();
	
		PAOowner.deleteAllPAOowners( getPAObjectID(), getDbConnection() );
		
		for( int i = 0; i < getPortVector().size(); i++ )
			((PAOowner) getPortVector().get(i)).add();
	}

}
