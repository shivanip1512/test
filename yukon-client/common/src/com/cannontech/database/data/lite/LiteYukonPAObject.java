package com.cannontech.database.data.lite;

/*
 */
public class LiteYukonPAObject extends LiteBase
{
	// a place holder for a LiteYukonPAObject, mostly used in option lists
	// that allow the user not to choose a LitYukonPAObject
	public static final com.cannontech.database.data.lite.LiteYukonPAObject LITEPAOBJECT_NONE = new com.cannontech.database.data.lite.LiteYukonPAObject
	(
		com.cannontech.database.data.pao.PAOGroups.INVALID,
		"(none)",
		com.cannontech.database.data.pao.PAOGroups.INVALID,
		com.cannontech.database.data.pao.PAOGroups.INVALID,
		com.cannontech.database.data.pao.PAOGroups.INVALID 
	);

	//private int yukonID = com.cannontech.database.data.pao.PAOGroups.INVALID;
	private int category = com.cannontech.database.data.pao.PAOGroups.INVALID;
	private String paoName = null;
	private int type = com.cannontech.database.data.pao.PAOGroups.INVALID;
	private int paoClass = com.cannontech.database.data.pao.PAOGroups.INVALID;

	//portID is only for devices that belong to a port
	private int portID = com.cannontech.database.data.pao.PAOGroups.INVALID;

	//childIDlist is a list of all the PAObjects owned by this PAObject
	private int[] childIDList = new int[0];
/**
 * LiteDevice
 */
public LiteYukonPAObject( int paoID) 
{
	super();

	setLiteID( paoID );
	setLiteType( LiteTypes.YUKON_PAOBJECT );
}
/**
 * LiteDevice
 */
public LiteYukonPAObject( int paoID, String name, int paoCategory, int paoType, int pClass ) 
{
	super();
	setLiteType( LiteTypes.YUKON_PAOBJECT );

	setLiteID( paoID );
	setPaoName( name );
	setCategory( paoCategory );
	setType( paoType );
	setPaoClass( pClass );
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 3:15:58 PM)
 * @return int
 * @param val java.lang.Object
 */
public boolean equals(Object val) 
{
	return ( val != null
		  		&& val instanceof LiteYukonPAObject
		  		&& super.equals(val)
		  		&&
		  		( ((LiteYukonPAObject)val).getCategory() == getCategory()
			  	  && ((LiteYukonPAObject)val).getPaoClass() == getPaoClass()
			  	  && ((LiteYukonPAObject)val).getType() == getType()
			  	  && ((LiteYukonPAObject)val).getPaoName().equals(getPaoName()) ) );
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getCategory() {
	return category;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 2:24:44 PM)
 * @return int[]
 */
public int[] getChildIDList() {
	return childIDList;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getPaoClass() {
	return paoClass;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 11:31:46 AM)
 * @return java.lang.String
 */
public java.lang.String getPaoName() {
	return paoName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 10:22:08 AM)
 * @return int
 */
public int getPortID() {
	return portID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 12:07:44 PM)
 * @return int
 */
public int getYukonID() {
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:21:01 PM)
 * @param dbalias java.lang.String
 */
public void retrieve(String dbalias)
{
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stat = null;
	java.sql.ResultSet rs = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbalias);
		stat = conn.prepareStatement(
			 "select Category, PAOName, Type, PAOClass " +
			 "from YukonPAObject where PAObjectID = ?" );
		
		stat.setInt( 1, getLiteID() );
		rs = stat.executeQuery();

		while( rs.next() )
		{
			String category = rs.getString("Category");
			
			setCategory( com.cannontech.database.data.pao.PAOGroups.getCategory(category) );
			setPaoName( rs.getString("PAOName") );
			setType( com.cannontech.database.data.pao.PAOGroups.getPAOType(category, rs.getString("Type")) );
			setPaoClass( com.cannontech.database.data.pao.PAOGroups.getPAOClass(category, rs.getString("PAOClass")) );
		}		
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( rs != null )
				rs.close();
			if( stat != null )
				stat.close();
			if( conn != null )
				conn.close();
		}
		catch(java.sql.SQLException e )
		{
			e.printStackTrace(System.out);
		}
	}


	
	try
	{
		com.cannontech.database.db.device.DeviceDirectCommSettings d = new com.cannontech.database.db.device.DeviceDirectCommSettings( new Integer(getLiteID()) );
		com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(
							com.cannontech.database.Transaction.RETRIEVE, d);
		t.execute();

		if( d.getPortID() != null )
			setPortID( d.getPortID().intValue() );
	}
	catch( com.cannontech.database.TransactionException e )
	{
		e.printStackTrace(System.out);
	}

}	
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newCategory int
 */
public void setCategory(int newCategory) {
	category = newCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 2:24:44 PM)
 * @param newChildIDList int[]
 */
public void setChildIDList(int[] newChildIDList) 
{
	//dont ever let this be set to null
	if( newChildIDList == null )
		childIDList = new int[0];
	else
		childIDList = newChildIDList;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newPaoClass int
 */
public void setPaoClass(int newPaoClass) {
	paoClass = newPaoClass;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 11:31:46 AM)
 * @param newPaoName java.lang.String
 */
public void setPaoName(java.lang.String newPaoName) {
	paoName = newPaoName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 10:22:08 AM)
 * @param newPortID int
 */
public void setPortID(int newPortID) {
	portID = newPortID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newType int
 */
public void setType(int newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 12:07:44 PM)
 * @param newYukonID int
 */
public void setYukonID(int newYukonID) 
{
	setLiteID( newYukonID );
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() 
{
	return getPaoName();
}
}
