package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (12/9/99 11:36:00 AM)
 * @author: 
 */
public class GraphDefinition extends com.cannontech.database.db.DBPersistent implements java.io.Serializable {
	private java.lang.Integer graphDefinitionID = null;
	private java.lang.String name = null;
	
	private java.lang.Character autoScaleTimeAxis = new Character('Y');
	private java.lang.Character autoScaleLeftAxis = new Character('Y');
	private java.lang.Character autoScaleRightAxis = new Character('Y');
		
	private java.util.Date startDate = com.cannontech.util.ServletUtil.getToday();
	private java.util.Date stopDate = com.cannontech.util.ServletUtil.getTomorrow();

	private java.lang.Double leftMin = new Double(0.0);
	private java.lang.Double leftMax = new Double(0.0);
	private java.lang.Double rightMin = new Double(0.0);
	private java.lang.Double rightMax = new Double(0.0);

	//default to line graph
	private java.lang.String type = "L";
	
	//map of number of days to a period string
	//used by getPeriodString()
	//please keep the following two arrays in sync
	//Not sure i like this technique, not so flexible

	public static final String periodStrings[] =
	{
		"1 Day",
		"3 Days",
		"5 Days",
		"1 Week",
		"1 Month"
	};

	// the first int is the number of days, the second is an index
	// into the periodStrings array
	private static final int days[][] =
	{
		{ 1,0 },
		{ 3,1 },
		{ 5,2 },
		{ 7,3 },
		{ 28,4 },
		{ 29,4 },
		{ 30,4 },
		{ 31,4 }
	};
	
	private static final String tableName = "GraphDefinition";			
/**
 * GraphDefinition constructor comment.
 */
public GraphDefinition() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:42:14 AM)
 */
public void add() throws java.sql.SQLException
{
	synchronized( com.cannontech.database.db.graph.GraphDefinition.class )
	{
		if( getGraphDefinitionID() == null )		
			setGraphDefinitionID( getNextID( getDbConnection().toString())  );
	}
		
	Object addValues[] = 
	{ 	
		getGraphDefinitionID(), 
		getName(), 
		getAutoScaleTimeAxis(), 
		getAutoScaleLeftAxis(), 
		getAutoScaleRightAxis(),
		getStartDate(), 
		getStopDate(), 
		getLeftMin(), 
		getLeftMax(), 
		getRightMin(), 
		getRightMax(), 
		getType() 
	};
	
	add( tableName, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:42:26 AM)
 */
public void delete() throws java.sql.SQLException
{
	delete( tableName, "GraphDefinitionID", getGraphDefinitionID());
} 
/**
 * Insert the method's description here.
 * Creation date: (3/27/00 2:08:30 PM)
 * @return boolean
 * @param val java.lang.Object
 */
public boolean equals(Object val) {

	if( val instanceof GraphDefinition )
		return ( this.getGraphDefinitionID().equals( ((GraphDefinition) val).getGraphDefinitionID() ) );
	else
		return super.equals(val);
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:39:46 PM)
 * @return java.lang.Character
 */
public java.lang.Character getAutoScaleLeftAxis() {
	return autoScaleLeftAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:40:01 PM)
 * @return java.lang.Character
 */
public java.lang.Character getAutoScaleRightAxis() {
	return autoScaleRightAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:39:31 PM)
 * @return java.lang.Character
 */
public java.lang.Character getAutoScaleTimeAxis() {
	return autoScaleTimeAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:37:49 AM)
 * @return java.lang.Long
 */
public java.lang.Integer getGraphDefinitionID() {
	return graphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:37 PM)
 * @return java.lang.Double
 */
public java.lang.Double getLeftMax() {
	return leftMax;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getLeftMin() {
	return leftMin;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:29 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
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
 		new com.cannontech.database.SqlStatement("SELECT MAX(GraphDefinitionID) FROM GraphDefinition",
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
 * Returns a string that represents the period the graphdefinition is for.
 * Creation date: (1/27/00 2:53:54 PM)
 * @return java.lang.String
 */
public String getPeriodString() {

	java.util.Date start = getStartDate();
	java.util.Date stop = getStopDate();

	if( start == null || stop == null )
		return null;

	//Not sure about this technique to figure out the difference in days
	//most certainly won't work if the period is more than 1 year	
	long diffMilli = stop.getTime() - start.getTime();

	java.util.GregorianCalendar origin = new java.util.GregorianCalendar();
	origin.setTime( new java.util.Date(0L));


	java.util.GregorianCalendar offset = new java.util.GregorianCalendar();
	offset.setTime( new java.util.Date(diffMilli) );

	int days = offset.get(java.util.Calendar.DAY_OF_YEAR) - 
			   origin.get(java.util.Calendar.DAY_OF_YEAR);
			   		

	for( int i = 0; i < GraphDefinition.days.length; i++ )
	{
		if( GraphDefinition.days[i][0] == days )
		{
			return GraphDefinition.periodStrings[GraphDefinition.days[i][1]];
		}
	}
	
	//didn't find it so....
	return "";
	//end unsure technique
	

}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:57 PM)
 * @return java.lang.Double
 */
public java.lang.Double getRightMax() {
	return rightMax;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:48 PM)
 * @return java.lang.Double
 */
public java.lang.Double getRightMin() {
	return rightMin;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:46 AM)
 * @return java.util.Date
 */
public java.util.Date getStartDate() {
	return startDate;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:55 AM)
 * @return java.util.Date
 */
public java.util.Date getStopDate() {
	return stopDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:01:24 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:42:05 AM)
 */
public void retrieve() throws java.sql.SQLException
{
	String selectColumns[] = { "Name", "AutoScaleTimeAxis", "AutoScaleLeftAxis", "AutoScaleRightAxis", "StartDate", "StopDate", "LeftMin", "LeftMax", "RightMin", "RightMax", "Type" };
	String constraintColumns[] = { "GraphDefinitionID" };
	Object constraintValues[] = { getGraphDefinitionID() };

	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		String name = (String) results[0];
		
		String temp = (String) results[1];
		Character autoScaleTimeAxis = new Character( temp.charAt(0) );

		temp = (String) results[2];
		Character autoScaleLeftAxis = new Character( temp.charAt(0) );

		temp = (String) results[3];
		Character autoScaleRightAxis = new Character( temp.charAt(0) );
			
		java.sql.Timestamp start = (java.sql.Timestamp) results[4];
		java.sql.Timestamp stop = (java.sql.Timestamp) results[5];		
		Double leftMin = (Double) results[6];
		Double leftMax = (Double) results[7];
		Double rightMin = (Double) results[8];
		Double rightMax = (Double) results[9];
		String type = (String) results[10];
					
		setName( name );
		setAutoScaleTimeAxis( autoScaleTimeAxis );
		setAutoScaleLeftAxis( autoScaleLeftAxis );
		setAutoScaleRightAxis( autoScaleRightAxis );
		
		setStartDate(start);
		setStopDate(stop);
		setLeftMin(leftMin);
		setLeftMax(leftMax);
		setRightMin(rightMin);
		setRightMax(rightMax);
		setType(type);			
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:39:46 PM)
 * @param newAutoScaleLeftAxis java.lang.Character
 */
public void setAutoScaleLeftAxis(java.lang.Character newAutoScaleLeftAxis) {
	autoScaleLeftAxis = newAutoScaleLeftAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:40:01 PM)
 * @param newAutoScaleRightAxis java.lang.Character
 */
public void setAutoScaleRightAxis(java.lang.Character newAutoScaleRightAxis) {
	autoScaleRightAxis = newAutoScaleRightAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 3:39:31 PM)
 * @param newAutoScaleTimeAxis java.lang.Character
 */
public void setAutoScaleTimeAxis(java.lang.Character newAutoScaleTimeAxis) {
	autoScaleTimeAxis = newAutoScaleTimeAxis;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:37:49 AM)
 * @param newGraphDefinitionID java.lang.Long
 */
public void setGraphDefinitionID(java.lang.Integer newGraphDefinitionID) {
	graphDefinitionID = newGraphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:37 PM)
 * @param newLeftMax java.lang.Double
 */
public void setLeftMax(java.lang.Double newLeftMax) {
	leftMax = newLeftMax;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:28 PM)
 * @param newLeftMin java.lang.Double
 */
public void setLeftMin(java.lang.Double newLeftMin) {
	leftMin = newLeftMin;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:29 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:57 PM)
 * @param newRightMax java.lang.Double
 */
public void setRightMax(java.lang.Double newRightMax) {
	rightMax = newRightMax;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:00:48 PM)
 * @param newRightMin java.lang.Double
 */
public void setRightMin(java.lang.Double newRightMin) {
	rightMin = newRightMin;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:46 AM)
 * @param newStartDate java.util.Date
 */
public void setStartDate(java.util.Date newStartDate) {
	startDate = newStartDate;
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:38:55 AM)
 * @param newStopDate java.util.Date
 */
public void setStopDate(java.util.Date newStopDate) {
	stopDate = newStopDate;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 1:01:24 PM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/99 9:48:02 AM)
 * @return java.lang.String
 */
public String toString() {

	return getName();
	/*StringBuffer buf = new StringBuffer();

	buf.append("Graph Definition - " + getGraphDefinitionID() + " - " + getUserID() + " - " + getName() + " - " + getStartDate() + " - " + getStopDate() + "\n");

	java.util.Vector series = getGraphDataSeries();

	if( series != null )
	{
		for( int i = 0; i < series.size(); i++ )
		{
			series.elementAt(i).toString();
		}
	}

	return buf.toString();*/
}
/**
 * Insert the method's description here.
 * Creation date: (12/9/99 11:42:34 AM)
 */
public void update() throws java.sql.SQLException
{
	if( getGraphDefinitionID() == null )
		setGraphDefinitionID( getNextID() );
	
	
	String setColumns[] = 
	{ 
		"Name", 
		"AutoScaleTimeAxis", 
		"AutoScaleLeftAxis", 
		"AutoScaleRightAxis", 
		"StartDate", 
		"StopDate", 
		"LeftMin", 
		"LeftMax", 
		"RightMin", 
		"RightMax", 
		"Type" 
	};
	
	Object setValues[] = 
	{
		getName(),
		getAutoScaleTimeAxis(),
		getAutoScaleLeftAxis(),
		getAutoScaleRightAxis(),
		getStartDate(),
		getStopDate(),
		getLeftMin(),
		getLeftMax(),
		getRightMin(),
		getRightMax(),
		getType()
	};
	
	String constraintColumns[] = { "GraphDefinitionID" };
	Object constraintValues[] = { getGraphDefinitionID() };
	
	update( tableName, setColumns, setValues, constraintColumns, constraintValues );
}
}
