package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (12/13/99 1:39:30 PM)
 * @author: 
 */
public class GraphDataSeries extends com.cannontech.database.db.DBPersistent {

	// GraphDataSeries valid types
	public static final String GRAPH_TYPE_STRING = "graph";
	public static final String PEAK_TYPE_STRING  = "peak";
	public static final String USAGE_TYPE_STRING = "usage";
	public static final String YESTERDAY_TYPE_STRING = "yesterday";
	public static final String PEAK_INTERVAL_TYPE_STRING = "peakinterval";
	
	public static final int GRAPH_TYPE= 0x0001;
	public static final int PEAK_TYPE = 0x0002;
	public static final int USAGE_TYPE = 0x0004;
	public static final int YESTERDAY_TYPE = 0x0008;
	public static final int PEAK_INTERVAL_TYPE = 0x0010;
	public static String[] validTypeStrings = 
	{
		GRAPH_TYPE_STRING,
		PEAK_TYPE_STRING, 
		USAGE_TYPE_STRING, 
		YESTERDAY_TYPE_STRING, 
		PEAK_INTERVAL_TYPE_STRING
	};	
	public static int[] validTypeInts =
	{
		GRAPH_TYPE,
		PEAK_TYPE, 
		USAGE_TYPE, 
		YESTERDAY_TYPE, 
		PEAK_INTERVAL_TYPE
	}; 	
	public static final int NORMAL_QUERY_TYPE = 0x0007; // graph, peak, usage
//	public static final int VALID_INTERVAL_TYPE = 0x0019;	// graph, yesterday, peakinterval
	
	
	private java.lang.Integer graphDataSeriesID = null;
	private java.lang.String type = GRAPH_TYPE_STRING;
	private java.lang.Integer graphDefinitionID = null;
	private java.lang.Integer pointID = null;	
	private java.lang.String label = " ";
	private java.lang.Character axis = new Character('L');
	private java.lang.Integer color = null;
	private Double multiplier = new Double(1.0);
	
	public static final String tableName = "GraphDataSeries";
	
	//These come from the device and point unit tables
	//respectively..... it saves a database hit in
	//certain circumstances
	private java.lang.String deviceName;

public static int getTypeInt(String type)
{
	for( int i = 0; i < validTypeStrings.length; i++)
	{
		if( validTypeStrings[i].equalsIgnoreCase(type) )
			return validTypeInts[i];
	}
	// TYPE NOT FOUND, default to Graph
	return GRAPH_TYPE;
}
public static String getType(int type)
{
	for( int i = 0; i < validTypeInts.length; i++)
	{
		if( validTypeInts[i] == type )
			return validTypeStrings[i];
	}
	// TYPE NOT FOUND, default to Graph
	return GRAPH_TYPE_STRING;
}

public static boolean isValidIntervalType(int type)
{
	if(isGraphType(type) || isYesterdayType(type) || isPeakIntervalType(type))
		return true;

	return false;
}
public static boolean isGraphType(int type)
{
	if((type & GRAPH_TYPE) == GRAPH_TYPE)
		return true;

	return false;
}
public static boolean isYesterdayType(int type)
{
	if ((type & YESTERDAY_TYPE) == YESTERDAY_TYPE)
		return true;

	return false;
}
public static boolean isPeakIntervalType(int type)
{
	if ((type & PEAK_INTERVAL_TYPE) == PEAK_INTERVAL_TYPE)
		return true;

	return false;
}
public static boolean isUsageType(int type)
{
	if ((type & USAGE_TYPE) == USAGE_TYPE)
		return true;

	return false;
}
public static boolean isPeakType(int type)
{
	if ((type & PEAK_TYPE) == PEAK_TYPE)
		return true;

	return false;
}
public static boolean isBasicQueryType(int type)
{
	if( isGraphType(type) || isPeakType(type) || isUsageType(type))
		return true;
	
	return false;
}
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
			setGraphDataSeriesID( getNextID( getDbConnection().toString() ) );
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
			getMultiplier()
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
	return getAllGraphDataSeries( graphDefinitionID, "yukon" );
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

		String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, gds.COLOR, pao.PAONAME, gds.MULTIPLIER FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p WHERE gds.GRAPHDEFINITIONID = " + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID ORDER BY p.POINTOFFSET";
//		String sqlString = "SELECT gds.GRAPHDATASERIESID, gds.TYPE, gds.POINTID, gds.LABEL, gds.AXIS, gds.COLOR, pao.PAONAME FROM GRAPHDATASERIES gds, YUKONPAOBJECT pao, POINT p WHERE gds.GRAPHDEFINITIONID = " + graphDefinitionID.toString() + " AND p.POINTID = GDS.POINTID AND pao.PAOBJECTID = p.PAOBJECTID ORDER BY p.POINTOFFSET";		

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
		String type = (String) sql.getRow(i)[1];
		java.math.BigDecimal pID = (java.math.BigDecimal) sql.getRow(i)[2];		
		String label = (String) sql.getRow(i)[3];
		String axis = (String) sql.getRow(i)[4];
		java.math.BigDecimal color = (java.math.BigDecimal) sql.getRow(i)[5];
		String deviceName = (String) sql.getRow(i)[6];
		Number mult = (Number)sql.getRow(i)[7];
//		java.math.BigDecimal mult = (java.math.BigDecimal)sql.getRow(i)[7];
//		java.math.BigDecimal uomid = (java.math.BigDecimal) sql.getRow(i)[7];
	
		dataSeries.setGraphDataSeriesID ( new Integer( gdsID.intValue() ) );		
		dataSeries.setGraphDefinitionID(graphDefinitionID);
		dataSeries.setType(type);
		dataSeries.setPointID( new Integer( pID.intValue() ) );
		dataSeries.setLabel(label);		
		dataSeries.setAxis( new Character( axis.charAt(0)) );
		dataSeries.setColor( new Integer( color.intValue() ) );
		dataSeries.setDeviceName(deviceName);
		dataSeries.setMultiplier(new Double(mult.doubleValue()));
//		dataSeries.setUoMId(new Integer (uomid.intValue()) );
			
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
public static synchronized Integer getNextID() {
	
	return getNextID("yukon");
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/99 10:31:33 AM)
 * @return java.lang.Long
 */
public static synchronized Integer getNextID(String databaseAlias) {
	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT MAX(GraphDataSeriesID) FROM GraphDataSeries",
 													databaseAlias );

	Integer returnVal = null;
														
	try
	{
		stmt.execute();

		if( stmt.getRowCount() > 0 )
		{
			returnVal = new Integer( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() + 1);
		}	

	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	if( returnVal == null )
		returnVal = new Integer(1);
		
	return returnVal;
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
public java.lang.String getType() {
	return type;
}

public int getTypeInt()
{
	return getTypeInt(getType());
}
/**
 * Insert the method's description here.
 * Creation date: (10/6/00 2:49:54 PM)
 * @return java.lang.String
 */
/*public java.lang.Integer getUoMId() {
	return uomID;
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
		"Multiplier"
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
		setType( (String) results[5] );
		setMultiplier((Double)results[6]);
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
public void setType(java.lang.String newType) {
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
		"Multiplier"
	};
	
	Object[] setValues = 
	{ 
		getGraphDefinitionID(),		
		getPointID(),		
		getLabel(),
		getAxis(), 
		getColor(),
		getType(),
		getMultiplier()	
	};

	String[] constraintColumns = { "GraphDataSeriesID" };
	Object[] constraintValues = { getGraphDataSeriesID() };

	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
