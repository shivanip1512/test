package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (6/14/2001 2:46:06 PM)
 * @author: 
 */
public abstract class GraphModel implements com.klg.jclass.chart.ChartDataModel, com.cannontech.graph.GraphDataFormats, GraphModelType
{
	// The database alias the model will use to retrieve database connections
	public java.lang.String databaseAlias = null;

	// The name of the model... likely the name of the device it represents
	public java.lang.String name;
	public java.lang.String device;	
	
	// The point id's of the points in this model
	// NOTE:  These point id's must must be in SORTED order, sorry.
	public long[] points = null;

	// The data series types of each series
	public String[] seriesTypes = null;
	//public boolean[] isBaselinePoint = null;
		
	// The starting date for the data to be retrieved
	public java.util.Date startDate = null; 

	// The ending date for the data to be retrieved
	public java.util.Date endDate = null;
	
	// The data retrieve from the database,
	// are (re-)initialized every time hitDatabase() is called

	// The [][] arrays are defined so that if there are left and right (axis) models,
	//  then the left axis model is stored in [0][0..n]
	//  then the right axis model is stored in [1][0..n]
	public double[][] xSeries = null;
	public  double[][] ySeries = null;
	public  int[][]	yQuality = null;

	// The min and max values read for each point in the model, stored in an array that
	//  is in accordance to the order of the pointIds.
	public double[] minimums = null;
	public double[] maximums = null;

	// Load factor values computed for each point in the model, stored in an array that
	//  is in accordance to the order of the pointIds.
	public double[] areaOfSet = null;	//Load Factor, area under the curve
	public double[] maxArea = null;		//Load Factor, total area (using max point value)

	// Multiplier of the point (placed on each point's values, stored in an array that
	//  is in accordance to the order of the pointIds.
	//public double[] multiplier = null;	//(Analog, Accumulator) Point's Multiplier

	// Number of decimal places each point has (from pointUnit table)
	public int [] decimalPlaces = null;
		
	public java.lang.String[] seriesNames = null;
	public  java.lang.String[] seriesDevices = null;
	public  java.awt.Color[] seriesColors = null;
	
	public double labelMin;
	public  double labelMax;


/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 9:40:33 AM)
 * @param indexToCopy int
 */
public void copyAllFields(int indexToCopy)
{
	xSeries[indexToCopy+1] = xSeries[indexToCopy];
	ySeries[indexToCopy+1] = ySeries[indexToCopy];
	yQuality[indexToCopy+1] = yQuality[indexToCopy];
	maxArea[indexToCopy+1] = maxArea[indexToCopy];
	areaOfSet[indexToCopy+1] = areaOfSet[indexToCopy];						
	minimums[indexToCopy+1] = minimums[indexToCopy];
	maximums[indexToCopy+1] = maximums[indexToCopy];
	//multiplier[indexToCopy+1] = multiplier[indexToCopy];
	decimalPlaces[indexToCopy+1] = decimalPlaces[indexToCopy];
}
/**
 * Returns the maximum value for the series indexed.
 * Creation date: (9/27/00 2:15:47 PM)
 * @return double
 * @param series int
 */
public double getAreaOfSet(int series) {
	return (series < areaOfSet.length ? areaOfSet[series] : 0.0);
}
/**
 * Returns the number of decimal places for the series indexed.
 * Creation date: (6/15/01 )
 * @return double
 * @param series int
 */
public int getDecimalPlaces(int series)
{
	return (series < decimalPlaces.length ? decimalPlaces[series] : 0);
}
/**
 * Returns the end date for the data.
 * Creation date: (10/3/00 5:52:24 PM)
 * @return java.util.Date
 */
public java.util.Date getEndDate() {
	return endDate;
} 
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 2:46:50 PM)
 * @return double
 */
public double getLabelMax() {
	return labelMax;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 2:46:38 PM)
 * @return double
 */
public double getLabelMin() {
	return labelMin;
}
/**
 * Returns the maximum value for the series indexed.
 * Creation date: (9/27/00 2:15:47 PM)
 * @return double
 * @param series int
 */
public double getMaxArea(int series) {
	return (series < maxArea.length ? maxArea[series] : 0.0);
}
/**
 * Returns the maximum value for the series indexed.
 * Creation date: (9/27/00 2:15:47 PM)
 * @return double
 * @param series int
 */
public double getMaximum(int series) {
	return (series < maximums.length ? maximums[series] : 0.0);
}
/**
 * Returns the minimum value for the series indexed.
 * Creation date: (9/27/00 2:15:36 PM)
 * @return double
 * @param series int
 */
public double getMinimum(int series) {
	return (series < minimums.length ? minimums[series] : 0.0);
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 2:28:45 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Returns the number of series retrieved.
 */
public int getNumSeries() {
	return (xSeries != null ? xSeries.length : 0);
}
/**
 * Sets the point id's of the points that this model represents.
 * Creation date: (10/3/00 5:45:29 PM)
 * @param pointIDs long[]
 */
public long[] getPointIDs() 
{
	return points;
}
/**
 * Insert the method's description here.
 * Creation date: (9/26/2001 2:06:10 PM)
 * @return int
 * @param pointId int
 * Use this method to find the index of specified pointID in the points array.
 *  Created this method because sorting the points in pointid order just doesn't cut it any more.
 *  Tried point offsets but calculated points don't have them...can't think of a better way.  SN
 */
public int getPointIndex(long pointID) 
{
	for ( int i = 0; i < points.length; i++)
	{
		if ( pointID == points[i] )
			return i;
	}
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (11/8/00 1:50:55 PM)
 * @return java.awt.Color[]
 */
public java.awt.Color getSeriesColors(int series) {
	return (series < seriesColors.length ? seriesColors[series] : null);
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 10:15:55 AM)
 * @return java.lang.String[]
 */
public java.lang.String getSeriesDevices(int series) {
	return (series < seriesDevices.length ? seriesDevices[series] : "");
} 
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 10:15:55 AM)
 * @return java.lang.String[]
 */
public java.lang.String getSeriesNames(int series) {
	return (series < seriesNames.length ? seriesNames[series] : "");
} 
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 10:15:55 AM)
 * @return java.lang.String[]
 */
public java.lang.String getSeriesTypes(int series) {
	return (series < seriesTypes.length ? seriesTypes[series] : "");
} 
/**
 * Returns the starting date for the data.
 * Creation date: (10/3/00 5:51:55 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDate() {
	return startDate;
}
/**
 * Returns the x-values for for the series indexed.
 */
public double[] getXSeries(int series) {
	return xSeries[series];
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 1:27:35 PM)
 * @return int[][]
 */
public int[] getYQuality(int series) {
	return yQuality[series];
}
/**
 * Returns the y values for the series indexed.
 */
public double[] getYSeries(int series) {
	return ySeries[series];
}
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
abstract void hitDatabase();
/**
 * Insert the method's description here.
 * Creation date: (7/10/2001 9:45:28 AM)
 */
public void initGraphModelFields(int numPoints)
{
	xSeries = new double[numPoints][];
	ySeries = new double[numPoints][];
	yQuality = new int[numPoints][];
	
	decimalPlaces = new int[numPoints];
	minimums = new double[numPoints]; 
	maximums = new double[numPoints];

	areaOfSet = new double[numPoints]; 
	maxArea = new double[numPoints];
	
	//Initialize the multiplier for each point, set to 1.0 for dividing purposes
	//multiplier = new double[numPoints];
	//for ( int i = 0; i < numPoints; i++)
		//multiplier[i] = 1.0;

}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2001 4:38:34 PM)
 * @return int
 * @param point long
 * @param conn java.sql.Connection
 */
public void retrieveDecimalPlaces( )
{
	decimalPlaces = new int[points.length];
	
	for (int i = 0; i < points.length; i++)
	{
		int decimal = 0;
		
		com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT DECIMALPLACES FROM POINTUNIT WHERE POINTID = " + points[i], databaseAlias );
 												 
	 	try
 		{											
			stmt.execute();
			for (int j = 0; j < stmt.getRowCount();j++)
				decimalPlaces[i] = ((java.math.BigDecimal) stmt.getRow(j)[0] ).intValue();
 		}

		catch( Exception e )
		{
			e.printStackTrace();
		}	

	}
}
/**
 * Sets the database alias to be used in retrieve point data.
 * Creation date: (10/3/00 6:31:25 PM)
 * @param dbAlias java.lang.String
 */
public void setDatabaseAlias(String dbAlias) 
{
	databaseAlias = dbAlias;
} 
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 2:28:45 PM)
 * @param newName java.lang.String
 */
public void setDevice(java.lang.String newDevice) {
	device = newDevice;
}
/**
 * Sets the ending date for the data.
 * Creation date: (10/3/00 5:51:29 PM)
 * @param end java.util.Date
 */
public void setEndDate(java.util.Date end) 
{
	endDate = end;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 2:46:50 PM)
 * @param newLabelMax double
 */
public void setLabelMax(double newLabelMax) {
	labelMax = newLabelMax;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 2:46:38 PM)
 * @param newLabelMin double
 */
public void setLabelMin(double newLabelMin) {
	labelMin = newLabelMin;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 2:28:45 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Sets the point id's of the points that this model represents.
 * Creation date: (10/3/00 5:45:29 PM)
 * @param pointIDs long[]
 */
public void setPointIDs(long[] pointIDs) 
{
	points = pointIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (11/8/00 1:50:55 PM)
 * @param newSeriesColors java.awt.Color[]
 */
public void setSeriesColors(java.awt.Color[] newSeriesColors) {
	seriesColors = newSeriesColors;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 10:15:55 AM)
 * @param newSeriesNames java.lang.String[]
 */
public void setSeriesDevices(java.lang.String[] newSeriesDevices) {
	seriesDevices = newSeriesDevices;
} 
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 10:15:55 AM)
 * @param newSeriesNames java.lang.String[]
 */
public void setSeriesNames(java.lang.String[] newSeriesNames) {
	seriesNames = newSeriesNames;
} 
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 3:27:29 PM)
 * @param newSeriesTypes java.lang.String[]
 */
public void setSeriesTypes(java.lang.String[] newSeriesTypes) {
	seriesTypes = newSeriesTypes;
}
/**
 * Sets the starting date for the data.
 * Creation date: (10/3/00 5:50:50 PM)
 * @param start java.util.Date
 */
public void setStartDate(java.util.Date start) 
{
	startDate = start;
}
public void setXSeries(int series, double[] xVals)
{
	xSeries[series] = xVals;
}
public void setYQuality(int series, int[] yQual)
{
	yQuality[series] = yQual;
}
public void setYSeries(int series, double[] yVals)
{
	ySeries[series] = yVals;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 9:42:54 AM)
 * @return java.lang.String
 * @param date java.util.Date
 */
public String toOracleDateString(java.util.Date date)
{

	if( date == null )
		return null;
		
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();	
	cal.setTime(date);

	int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
	int year = cal.get(java.util.Calendar.YEAR);

	java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM");
	String month = format.format(date).toUpperCase();

	return day + "-" + month + "-" + year;	
}
}
