package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (12/13/99 1:39:30 PM)
 * @author: 
 */
import java.sql.Connection;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

public class GraphDataSeries extends com.cannontech.database.db.DBPersistent
{
	private java.lang.Integer graphDataSeriesID = null;
	private Integer type = new Integer(GDSTypes.BASIC_GRAPH_TYPE);
	private java.lang.Integer graphDefinitionID = null;
	private java.lang.Integer pointID = null;	
	private java.lang.String label = " ";
	private java.lang.Character axis = new Character('L');
	private java.lang.Integer color = null;
	private Double multiplier = new Double(1.0);
	private Integer renderer = new Integer(GraphRenderers.LINE); 
	private String moreData = com.cannontech.common.util.CtiUtilities.STRING_NONE;
	public static final String tableName = "GraphDataSeries";
	
	//These come from the device and point unit tables
	//respectively..... it saves a database hit in
	//certain circumstances
	private java.lang.String deviceName;

	/**
	 * GraphDataSeries constructor comment.
	 */
	public GraphDataSeries() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		synchronized( com.cannontech.database.db.graph.GraphDataSeries.class )
		{
			if( getGraphDataSeriesID() == null )
			{
				setGraphDataSeriesID(new Integer(getNextID( getDbConnection()) ));
			}
		
			Object[] addValues =
			{
				getGraphDataSeriesID(), 
				getGraphDefinitionID(),
				getPointID(),			
				getLabel(),
				getAxis(), 
				getColor(),
				getType(),
				getMultiplier(),
				getRenderer(),
				getMoreData()
			};
			
			add( tableName, addValues );
		}
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException
	{
		delete( tableName, "GraphDataSeriesID", getGraphDataSeriesID() );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/9/99 1:46:08 PM)
	 * @param userID java.lang.Long
	 */
	public static void deleteAllGraphDataSeries(Integer graphDefinitionID) 
	{	
		deleteAllGraphDataSeries( graphDefinitionID, "yukon" );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/9/99 1:46:08 PM)
	 * @param userID java.lang.Long
	 */
	public static void deleteAllGraphDataSeries(Integer graphDefinitionID, String databaseAlias) 
	{	
		String sqlString = "DELETE FROM GraphDataSeries WHERE GraphDefinitionID= " + graphDefinitionID.toString();
	
		com.cannontech.database.SqlStatement sql = new com.cannontech.database.SqlStatement(sqlString, databaseAlias);
	
		try
		{
			sql.execute();
		}
		catch( com.cannontech.common.util.CommandExecutionException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	
		return;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/9/99 1:46:08 PM)
	 * @param userID java.lang.Long
	 */
	public static GraphDataSeries[] getAllGraphDataSeries(Integer graphDefinitionID) 
	{	
		return getAllGraphDataSeries( graphDefinitionID, CtiUtilities.getDatabaseAlias() );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/9/99 1:46:08 PM)
	 * @param userID java.lang.Long
	 */
	public static GraphDataSeries[] getAllGraphDataSeries(Integer graphDefinitionID, String databaseAlias) 
	{
		
	//	String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, gds.COLOR, pao.PAONAME, pu.UOMID FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p, POINTUNIT pu WHERE gds.GRAPHDEFINITIONID = " + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID AND pu.PointID = p.POINTID ORDER BY p.POINTOFFSET";
		//Remove PointUnit table in order to get Status points visible.  Status points have no pointunit.
	
			String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, gds.COLOR, pao.PAONAME, gds.MULTIPLIER, gds.RENDERER, gds.MOREDATA FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p WHERE gds.GRAPHDEFINITIONID = " + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID ORDER BY p.POINTOFFSET";
//			String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, gds.COLOR, pao.PAONAME, gds.MULTIPLIER FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p WHERE gds.GRAPHDEFINITIONID = " + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID ORDER BY p.POINTOFFSET";
	
		com.cannontech.database.SqlStatement sql = new com.cannontech.database.SqlStatement(sqlString, databaseAlias);
	
		try
		{
			sql.execute();
		}
		catch( com.cannontech.common.util.CommandExecutionException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	
		java.util.Vector temp = new java.util.Vector();
		
		for( int i = 0; i < sql.getRowCount(); i++ )
		{
			GraphDataSeries dataSeries = new GraphDataSeries();
	
			java.math.BigDecimal gdsID = (java.math.BigDecimal) sql.getRow(i)[0];
			java.math.BigDecimal type = (java.math.BigDecimal) sql.getRow(i)[1];
			java.math.BigDecimal pID = (java.math.BigDecimal) sql.getRow(i)[2];		
			String label = (String) sql.getRow(i)[3];
			String axis = (String) sql.getRow(i)[4];
			java.math.BigDecimal color = (java.math.BigDecimal) sql.getRow(i)[5];
			String deviceName = (String) sql.getRow(i)[6];
			Number mult = (Number)sql.getRow(i)[7];
			Integer rend = (Integer)sql.getRow(i)[8];
			String moreData = (String)sql.getRow(i)[9];
		
			dataSeries.setGraphDataSeriesID ( new Integer( gdsID.intValue() ) );		
			dataSeries.setGraphDefinitionID(graphDefinitionID);
			dataSeries.setType(new Integer(type.intValue()));
			dataSeries.setPointID( new Integer( pID.intValue() ) );
			dataSeries.setLabel(label);		
			dataSeries.setAxis( new Character( axis.charAt(0)) );
			dataSeries.setColor( new Integer( color.intValue() ) );
			dataSeries.setDeviceName(deviceName);
			dataSeries.setMultiplier(new Double(mult.doubleValue()));
			dataSeries.setRenderer(new Integer(rend.intValue()));
			dataSeries.setMoreData(moreData);
				
			temp.addElement( dataSeries );
		}
	 
		GraphDataSeries[] returnVal = new GraphDataSeries[temp.size()];
		temp.copyInto( returnVal );
	
		return returnVal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/4/00 3:00:40 PM)
	 * @return java.lang.String
	 */
	public Character getAxis() {
		return axis;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/4/00 3:01:09 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getColor() {
		return color;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/6/00 2:49:32 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getDeviceName() {
		return deviceName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:17 PM)
	 * @return java.lang.Long
	 */
	public java.lang.Integer getGraphDataSeriesID() {
		return graphDataSeriesID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:28 PM)
	 * @return java.lang.Long
	 */
	public java.lang.Integer getGraphDefinitionID() {
		return graphDefinitionID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/28/00 2:21:02 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getLabel() {
		return label;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Long
	 */
	public Double getMultiplier() {
		
		return multiplier;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Long
	 */
	public static synchronized int getNextID() {
		return getNextID(PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias() ));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Long
	 */
	public static synchronized int getNextID(Connection conn)
	{
		int retVal = 0;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
			
		try
		{		
			if( conn == null )
			{
				throw new IllegalStateException("Database connection can not be (null).");
			}
			else
			{
				pstmt = conn.prepareStatement("select max(graphdataseriesID) AS maxid from graphdataseries");
				rset = pstmt.executeQuery();							
	
				// Just one please
				if( rset.next() )
					retVal = rset.getInt("maxid") + 1;
			}		
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
		return retVal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:37 PM)
	 * @return java.lang.Long
	 */
	public java.lang.Integer getPointID() {
		return pointID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/30/2001 2:09:16 PM)
	 * @return java.lang.String
	 */
	public java.lang.Integer getType() {
		return type;
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		String[] selectColumns = 
		{
			"GraphDefinitionID",		 
			"PointID", 	 
			"Label", 
			"Axis", 
			"Color",
			"Type",
			"Multiplier",
			"Renderer",
			"MoreData"
		};
		
		String[] constraintColumns = {"GraphDataSeriesID"};
		Object[] constraintValues = {getGraphDataSeriesID()};
		
		Object results[] = retrieve(selectColumns, tableName, constraintColumns, constraintValues);
		
		if (results.length == selectColumns.length)
		{
			setGraphDefinitionID((Integer) results[0]);
			setPointID((Integer) results[1]);
			setLabel( (String) results[2] );
			setAxis(  new Character( ((String) results[3]).charAt(0) ));
			setColor( (Integer) results[4] );
			setType( (Integer) results[5] );
			setMultiplier((Double)results[6]);
			setRenderer((Integer) results[7]);
			setMoreData((String)results[8]);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/4/00 3:00:40 PM)
	 * @param newAxis java.lang.String
	 */
	public void setAxis(Character newAxis) {
		axis = newAxis;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/4/00 3:01:09 PM)
	 * @param newColor java.lang.Integer
	 */
	public void setColor(java.lang.Integer newColor) {
		color = newColor;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/6/00 2:49:32 PM)
	 * @param newDeviceName java.lang.String
	 */
	public void setDeviceName(java.lang.String newDeviceName) {
		deviceName = newDeviceName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:17 PM)
	 * @param newGraphDataSeriesID java.lang.Long
	 */
	public void setGraphDataSeriesID(java.lang.Integer newGraphDataSeriesID) {
		graphDataSeriesID = newGraphDataSeriesID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:28 PM)
	 * @param newGraphDefinitionID java.lang.Long
	 */
	public void setGraphDefinitionID(java.lang.Integer newGraphDefinitionID) {
		graphDefinitionID = newGraphDefinitionID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/28/00 2:21:02 PM)
	 * @param newLabel java.lang.String
	 */
	public void setLabel(java.lang.String newLabel) {
		label = newLabel;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:37 PM)
	 * @param newPointID java.lang.Long
	 */
	public void setMultiplier(Double newMultiplier) {
		multiplier = newMultiplier;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/13/99 1:41:37 PM)
	 * @param newPointID java.lang.Long
	 */
	public void setPointID(java.lang.Integer newPointID) {
		pointID = newPointID;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/30/2001 2:09:16 PM)
	 * @param newType java.lang.String
	 */
	//public void setType(java.lang.String newType) {
	public void setType(java.lang.Integer newType) {
		type = newType;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/6/00 2:49:54 PM)
	 * @param newUnitOfMeasure java.lang.String
	 */
	/*public void setUoMId(java.lang.Integer newUOMID) {
		uomID = newUOMID;
	}*/
	/**
	 * Insert the method's description here.
	 * Creation date: (12/17/99 9:55:58 AM)
	 * @return java.lang.String
	 */
	public String toString() {
	
		return "GraphDataSeries - type:  " + getType() + "  gdsID:  " + getGraphDataSeriesID() + "  pointID:  " + getPointID() + "\n";
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		
		String[] setColumns = 
		{ 
			"GraphDefinitionID",		
			"PointID",	
			"Label",
			"Axis", 
			"Color",
			"Type",
			"Multiplier",
			"Renderer",
			"MoreData"
		};
		
		Object[] setValues = 
		{ 
			getGraphDefinitionID(),		
			getPointID(),		
			getLabel(),
			getAxis(), 
			getColor(),
			getType(),
			getMultiplier(),
			getRenderer(),
			getMoreData()	
		};
	
		String[] constraintColumns = { "GraphDataSeriesID" };
		Object[] constraintValues = { getGraphDataSeriesID() };
	
		update( tableName, setColumns, setValues, constraintColumns, constraintValues );
	}
	/**
	 * @return
	 */
	public String getMoreData()
	{
		return moreData;
	}
	/**
	 * @param object
	 */
	public void setMoreData(String object)
	{
		moreData = object;
	}

	public java.util.Date getSpecificDate()
	{
		if( com.cannontech.common.util.CtiUtilities.STRING_NONE.equalsIgnoreCase( getMoreData()))
			return null;
			
		java.util.Date date = null;
		if( GDSTypesFuncs.isDateType(getType().intValue()) )
		{
			long ts = Long.valueOf(getMoreData()).longValue();
			date = new java.util.Date(ts);
		}
		return date;
	}
	/**
	 * @return
	 */
	public Integer getRenderer()
	{
		return renderer;
	}

	/**
	 * @param integer
	 */
	public void setRenderer(Integer integer)
	{
		renderer = integer;
	}

}
