package com.cannontech.tdc;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.database.db.point.TAGLog;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tdc.custom.CustomDisplay;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ViewCreator
{	
	//the original table of TDC we are changing
	private final Display2WayDataAdapter tableModel;

	private int pageCount = 1;
	private int qMin = 0;
	private int qMax = 0;
	private int qRowCnt = 0;


	public static final SimpleDateFormat MILLI_FORMATTER = 
			new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.SSS");

	
	/**
	 * 
	 */
	protected ViewCreator( Display2WayDataAdapter tableModel_ )
	{
		super();
		tableModel = tableModel_;
	}

	private void processPagingValues( String minPage, String maxPage, int page )
	{
		qMin = (minPage.length() > 0 
				? Integer.parseInt(minPage) 
				: 0);

		qMax = (maxPage.length() > 0 
				? Integer.parseInt(maxPage) 
				: 0);

		qRowCnt = (qMax == qMin ? 0 : qMax - qMin + 1);

		if( qRowCnt > TDCDefines.MAX_ROWS )
		{
			pageCount = qRowCnt / TDCDefines.MAX_ROWS;

			if( (qRowCnt - (pageCount * TDCDefines.MAX_ROWS)) > 0 )
				pageCount++;
		}

		if( page > pageCount )
			page = 1;
		else if( page < 1 )
			page = pageCount;

		qMax = qMax - ( (page - 1) * TDCDefines.MAX_ROWS );

		qMin = qMin >= qMax ? qMin : (qMax - TDCDefines.MAX_ROWS);
	}


	private void debugOut( Object[][] rowData, Object[] objs, String rowQuery, int page )
	{
		
		try
		{
			StringBuffer b = new StringBuffer(rowQuery);
			for( int i = 0; i < objs.length; i++ )
			{
				int loc = b.toString().indexOf("?");
				b.replace( loc, loc+1, objs[i].toString() );
			}
	
			CTILogger.debug("   TDC Page=" + page + ", PageCnt=" + pageCount +
							", min=" + qMin +", max=" + qMax );		
			CTILogger.debug("   TDC query=" + b.toString() );
			CTILogger.debug("   TDC rowCnt=" + rowData.length );
		}
		catch( Exception e )
		{}
		
	}
	
	/**
	 * SOE event log generator derived from the SystemLog table
	 * @param sql java.lang.String
	 */
	public synchronized int createRowsForSOELogView(Date date, int page) 
	{
		tableModel.setCurrentDate( date );

//		String rowCountQuery = "select min(s.logid), max(s.logid)" +
//						  " from " + SOELog.TABLE_NAME + " s" +
//						  " where s.soedatetime >= ?" +
//						  " and s.soedatetime < ?";

//		String rowQuery = 
//				"select s.soedatetime, y.PAOName, p.pointname, s.description, " +
//				" s.additionalinfo, s.millis, s.pointid " +
//				" from " + SOELog.TABLE_NAME + " s, YukonPAObject y, point p " +
//				" where s.pointid=p.pointid and y.PAObjectID=p.PAObjectID " +
//				" and s.soedatetime >= ? " +
//				" and s.soedatetime < ? " +
//				" and s.logid >= ? " +
//				" and s.logid <= ? " +
//				" order by s.soedatetime, s.millis";

		String rowCountQuery = "select min(s.logid), max(s.logid)" +
						  " from " + SystemLog.TABLE_NAME + " s, " + Point.TABLE_NAME + " p " +
						  " where s.datetime >= ?" +
						  " and s.datetime < ? " +
						  " and p.LogicalGroup = '" + PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE] +"' " +
						  " and s.pointid=p.pointid";

		//only get the SOE points
		String rowQuery = 
				"select s.datetime, y.PAOName, p.pointname, s.description, s.action, " +
				" s.millis, s.pointid " +
				" from " + SystemLog.TABLE_NAME + " s, YukonPAObject y, point p " +
				" where p.LogicalGroup = '" + PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE] +"' " +
				" and s.pointid=p.pointid and y.PAObjectID=p.PAObjectID " +
				" and s.datetime >= ? " +
				" and s.datetime < ? " +
				" and s.logid >= ? " +
				" and s.logid <= ? " +
				" order by s.datetime, s.millis";



		GregorianCalendar lowerCal = new GregorianCalendar();
		lowerCal.setTime( date );
		lowerCal.set( lowerCal.HOUR_OF_DAY, 0 );
		lowerCal.set( lowerCal.MINUTE, 0 );
		lowerCal.set( lowerCal.SECOND, 0 );
		lowerCal.set( lowerCal.MILLISECOND, 000 );
   
		GregorianCalendar upperCal = new GregorianCalendar();
		upperCal.setTime( date );
		upperCal.set( upperCal.HOUR_OF_DAY, 23 );
		upperCal.set( upperCal.MINUTE, 59 );
		upperCal.set( upperCal.SECOND, 59 );
		upperCal.set( upperCal.MILLISECOND, 999 );



		Object[] objs = new Object[2];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();

		//get the row count, min, max   
		Object[][] rowCount = DataBaseInteraction.queryResults( rowCountQuery, objs );
		
		
		//set the correct page number values
		processPagingValues( 
				rowCount[0][0].toString(),
				rowCount[0][1].toString(),
				page );
		
	
		objs = new Object[4];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();
		objs[2] = new Integer(qMin);
		objs[3] = ( qRowCnt <= TDCDefines.MAX_ROWS
						? new Integer(qMax)
						: new Integer(qMin + TDCDefines.MAX_ROWS) );

		//get the actual data
		Object[][] rowData = DataBaseInteraction.queryResults( rowQuery, objs );


		if( rowData == null )
			return -1;


		debugOut( rowData, objs, rowQuery, page );
	
	
		for( int i = 0; i < rowData.length; i++ )
		{
			Vector newRow = new Vector( tableModel.getColumnCount() );
			for( int j = 0; j < tableModel.getColumnCount(); j++ )
				newRow.addElement( "" );  // put these into the vector just as place holder values

			// set TimeStamp
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) ) // format of ORACLE: "2000-06-09 16:34:34.0"
			{
				//set the millis to what we got from the DB
				upperCal.setTime( (Timestamp)rowData[i][0] );
				upperCal.set( GregorianCalendar.MILLISECOND, Integer.parseInt(rowData[i][5].toString()) );

				//new ModifiedDate(upperCal.getTime().getTime()), 
				newRow.setElementAt(
						MILLI_FORMATTER.format(upperCal.getTime()),
						tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) );
			}
			
			// set DeviceName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICENAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][1] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICENAME) );
			
			// set PointName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTNAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][2] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTNAME) );
			
			// set Description
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_UOFM) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][3] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_UOFM));

			// set AdditionalInfo
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTQUALITY) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][4] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTQUALITY));
	
			tableModel.checkRowExceedance();

			// we must add rows like this to accomodate the blank rows that automatically
			//   get added in	
			if( tableModel.getRowCount() > 0 )
				tableModel.getRows().insertElementAt( newRow, 0 );
			else
				tableModel.getRows().addElement( newRow );
			
			// put a holder value for the model in row location i
			tableModel.createPsuedoPointValue( 0 );
//			tableModel.createPsuedoPointValue(
//						Integer.parseInt(rowData[i][6].toString()), //pointID
//						((Timestamp)rowData[i][0]).getTime(), //TimeStamp
//						CommonUtils.createString( rowData[i][1] ), //DeviceName
//						0, //SOE_Tag
//						0 );

			tableModel.addBlankRowIfNeeded();
		}
	
		tableModel.fireTableDataChanged(); // is actually fireTableDataChanged();
	
		return pageCount;
	}

	/**
	 * TAG log generator
	 * @param sql java.lang.String
	 */
	public synchronized int createRowsForTAGLogView(Date date, int page) 
	{
		tableModel.setCurrentDate( date );

		String rowCountQuery = "select min(s.logid), max(s.logid)" +
						  " from " + TAGLog.TABLE_NAME + " s" +
						  " where s.tagtime >= ?" +
						  " and s.tagtime < ?";

		String rowQuery = 
				"select s.tagtime, y.PAOName, p.pointname , s.description, s.action, s.username, " +
				" s.pointid, s.refstr, s.forstr, t.tagname " +
				" from " + TAGLog.TABLE_NAME + " s, YukonPAObject y, point p, tags t " +
				" where s.pointid=p.pointid and y.PAObjectID=p.PAObjectID " +
				" and t.tagid = s.tagid " +
				" and s.tagtime >= ? " +
				" and s.tagtime < ? " +
				" and s.logid >= ? " +
				" and s.logid <= ? " +
				" order by s.tagtime, s.tagid";
   
		GregorianCalendar lowerCal = new GregorianCalendar();
		lowerCal.setTime( date );
		lowerCal.set( lowerCal.HOUR_OF_DAY, 0 );
		lowerCal.set( lowerCal.MINUTE, 0 );
		lowerCal.set( lowerCal.SECOND, 0 );
		lowerCal.set( lowerCal.MILLISECOND, 000 );
   
		GregorianCalendar upperCal = new GregorianCalendar();
		upperCal.setTime( date );
		upperCal.set( upperCal.HOUR_OF_DAY, 23 );
		upperCal.set( upperCal.MINUTE, 59 );
		upperCal.set( upperCal.SECOND, 59 );
		upperCal.set( upperCal.MILLISECOND, 999 );



		Object[] objs = new Object[2];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();

		//get the row count, min, max   
		Object[][] rowCount = DataBaseInteraction.queryResults( rowCountQuery, objs );
		
		
		//set the correct page number values
		processPagingValues( 
				rowCount[0][0].toString(),
				rowCount[0][1].toString(),
				page );
		
	
		objs = new Object[4];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();
		objs[2] = new Integer(qMin);
		objs[3] = ( qRowCnt <= TDCDefines.MAX_ROWS
						? new Integer(qMax)
						: new Integer(qMin + TDCDefines.MAX_ROWS) );

		//get the actual data
		Object[][] rowData = DataBaseInteraction.queryResults( rowQuery, objs );


		if( rowData == null )
			return -1;


		debugOut( rowData, objs, rowQuery, page );
	
	
		for( int i = 0; i < rowData.length; i++ )
		{
			Vector newRow = new Vector( tableModel.getColumnCount() );
			for( int j = 0; j < tableModel.getColumnCount(); j++ )
				newRow.addElement( "" );  // put these into the vector just as place holder values

//			"select s.tagtime, y.PAOName, p.pointname , s.description, 
//				s.action, s.username, " +
//			" s.pointid, s.refstr, s.forstr, t.tagname " +

			// set TimeStamp
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) ) // format of ORACLE: "2000-06-09 16:34:34.0"
				newRow.setElementAt( new ModifiedDate( 
							((Timestamp)rowData[i][0]).getTime() ), 
							tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) );
			
			// set DeviceName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICENAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][1] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICENAME) );
			
			// set PointName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTNAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][2] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTNAME) );
			
			// set Description
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_UOFM) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][3] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_UOFM));

			// set Action
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTQUALITY) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][4] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTQUALITY));

			// set UserName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICEID) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][5] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICEID));

			// set RefStr
//			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICEID) )
//				newRow.setElementAt( CommonUtils.createString( rowData[i][7] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICEID));

			// set ForStr
//			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICEID) )
//				newRow.setElementAt( CommonUtils.createString( rowData[i][8] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICEID));

			// set TagName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_STATE) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][9] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_STATE));



	
			tableModel.checkRowExceedance();

			// we must add rows like this to accomodate the blank rows that automatically
			//   get added in	
			if( tableModel.getRowCount() > 0 )
				tableModel.getRows().insertElementAt( newRow, 0 );
			else
				tableModel.getRows().addElement( newRow );
			
			tableModel.createPsuedoPointValue( 0 );

			// put a holder value for the model in row location i
//			tableModel.createPsuedoPointValue(
//						Integer.parseInt(rowData[i][6].toString()), //pointID
//						((Timestamp)rowData[i][0]).getTime(), //TimeStamp
//						CommonUtils.createString( rowData[i][1] ), //DeviceName
//						0, //SOE_Tag
//						0 );

			tableModel.addBlankRowIfNeeded();
		}
	
		tableModel.fireTableDataChanged(); // is actually fireTableDataChanged();
	
		return pageCount;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/00 1:24:13 PM)
	 * @param sql java.lang.String
	 */
	public synchronized int createRowsForHistoricalAlarmView( Date date, int page, int displayNum_ ) 
	{
		tableModel.setCurrentDate( date );
		Integer catID = null;
		if( Display.isAlarmDisplay(displayNum_) && displayNum_ != Display.GLOBAL_ALARM_DISPLAY )
		{
			catID = new Integer( (displayNum_ + Signal.EVENT_SIGNAL) - Display.GLOBAL_ALARM_DISPLAY);
		}

		String rowCountQuery = "select min(s.logid), max(s.logid) " +
						  "from " + SystemLog.TABLE_NAME + " s " +
						  "where s.type = " + SystemLog.TYPE_ALARM + " " +
						  "and s.datetime >= ? " +
						  "and s.datetime < ? " +
						  (catID == null ? "" : "and s.priority = " + catID + " ");

		String rowQuery = "select s.logid, s.pointid, s.datetime, s.soe_tag, " +
						  "s.type, s.priority, s.action, s.description, s.username " +
						  "from " + SystemLog.TABLE_NAME + " s " +
						  "where s.type = " + SystemLog.TYPE_ALARM + " " +
						  "and s.datetime >= ? " +
						  "and s.datetime < ? " +
						  "and s.logid >= ? " +
						  "and s.logid <= ? " +
						  (catID == null ? "" : "and s.priority = " + catID + " " ) +
						  "order by s.datetime, s.logid";


		GregorianCalendar lowerCal = new GregorianCalendar();
		lowerCal.setTime( date );
		lowerCal.set( lowerCal.HOUR_OF_DAY, 0 );
		lowerCal.set( lowerCal.MINUTE, 0 );
		lowerCal.set( lowerCal.SECOND, 0 );
		lowerCal.set( lowerCal.MILLISECOND, 000 );
   
		GregorianCalendar upperCal = new GregorianCalendar();
		upperCal.setTime( date );
		upperCal.set( upperCal.HOUR_OF_DAY, 23 );
		upperCal.set( upperCal.MINUTE, 59 );
		upperCal.set( upperCal.SECOND, 59 );
		upperCal.set( upperCal.MILLISECOND, 999 );


		Object[] objs = new Object[2];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();

		//get the row count, min, max   
		Object[][] rowCount = DataBaseInteraction.queryResults( rowCountQuery, objs );


		//set the correct page number values
		processPagingValues(
				rowCount[0][0].toString(),
				rowCount[0][1].toString(),
				page );


		objs = new Object[4];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();
		objs[2] = new Integer(qMin);
		objs[3] = ( qRowCnt <= TDCDefines.MAX_ROWS
						? new Integer(qMax)
						: new Integer(qMin + TDCDefines.MAX_ROWS) );

		//get the actual data
		Object[][] rowData = DataBaseInteraction.queryResults( rowQuery, objs );


		if( rowData == null )
			return -1;

		debugOut( rowData, objs, rowQuery, page );

	
		for( int i = 0; i < rowData.length; i++ )
		{
			Signal sig = new Signal();
			// put a holder value for the model in row location i
			if( rowData[i].length >= 7 && rowData[i][6] != null )
			{
				sig.setPointID( Integer.parseInt(rowData[i][1].toString()) );			
				sig.setTimeStamp( new ModifiedDate( ((Timestamp)rowData[i][2]).getTime() ) );
				sig.setSOE_Tag( Integer.parseInt(rowData[i][3].toString()) );
				sig.setLogType( Integer.parseInt(rowData[i][4].toString()) );
				sig.setCategoryID( Integer.parseInt(rowData[i][5].toString()) );
				sig.setAction( CommonUtils.createString( rowData[i][6] ) );
				sig.setDescription( CommonUtils.createString( rowData[i][7] ) );
				sig.setUserName( CommonUtils.createString( rowData[i][8] ) );
			
						
				tableModel.insertAlarmDisplayAlarmedRow( sig );
			}

		}
	
		tableModel.fireTableDataChanged(); // is actually fireTableDataChanged();
	
		return pageCount;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/00 1:24:13 PM)
	 * @param sql java.lang.String
	 */
	public synchronized int createRowsForHistoricalView(Date date, int page) 
	{
		tableModel.setCurrentDate( date );

		String rowCountQuery = "select min(s.logid), max(s.logid)" +
						  " from " + SystemLog.TABLE_NAME + " s" +
						  " where s.datetime >= ?" +
						  " and s.datetime < ?";

		String rowQuery = "select s.datetime, y.PAOName, p.pointname, s.description, s.action, " +
						  " s.username, s.pointid, s.soe_tag " +
						  " from " + SystemLog.TABLE_NAME + " s, YukonPAObject y, point p " +
						  " where s.pointid=p.pointid and y.PAObjectID=p.PAObjectID " +
						  " and s.datetime >= ? " +
						  " and s.datetime < ? " +
						  " and s.logid >= ? " +
						  " and s.logid < ? " +
						  " order by s.datetime, s.soe_tag";
   
		GregorianCalendar lowerCal = new GregorianCalendar();
		lowerCal.setTime( date );
		lowerCal.set( lowerCal.HOUR_OF_DAY, 0 );
		lowerCal.set( lowerCal.MINUTE, 0 );
		lowerCal.set( lowerCal.SECOND, 0 );
		lowerCal.set( lowerCal.MILLISECOND, 000 );
   
		GregorianCalendar upperCal = new GregorianCalendar();
		upperCal.setTime( date );
		upperCal.set( upperCal.HOUR_OF_DAY, 23 );
		upperCal.set( upperCal.MINUTE, 59 );
		upperCal.set( upperCal.SECOND, 59 );
		upperCal.set( upperCal.MILLISECOND, 999 );



		Object[] objs = new Object[2];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();

		//get the row count, min, max   
		Object[][] rowCount = DataBaseInteraction.queryResults( rowCountQuery, objs );


		//set the correct page number values
		processPagingValues(
				rowCount[0][0].toString(),
				rowCount[0][1].toString(),
				page );

	
		objs = new Object[4];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();
		objs[2] = new Integer(qMin);
		objs[3] = ( qRowCnt <= TDCDefines.MAX_ROWS
						? new Integer(qMax)
						: new Integer(qMin + TDCDefines.MAX_ROWS) );

		//get the actual data
		Object[][] rowData = DataBaseInteraction.queryResults( rowQuery, objs );


		if( rowData == null )
			return -1;

		debugOut( rowData, objs, rowQuery, page );

	
		for( int i = 0; i < rowData.length; i++ )
		{
			Vector newRow = new Vector( tableModel.getColumnCount() );
			for( int j = 0; j < tableModel.getColumnCount(); j++ )
				newRow.addElement( "" );  // put these into the vector just as place holder values

			// set TimeStamp
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) ) // format of ORACLE: "2000-06-09 16:34:34.0"
				newRow.setElementAt( new ModifiedDate( 
							((Timestamp)rowData[i][0]).getTime() ), 
							tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) );
			
			// set DeviceName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICENAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][1] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICENAME) );
			
			// set PointName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTNAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][2] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTNAME) );
			
			// set Description
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_UOFM) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][3] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_UOFM));

			// set Action
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_TXT_MSG) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][4] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_TXT_MSG));
	
			// set User Name
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICEID) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][5] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICEID) );

			tableModel.checkRowExceedance();

			// we must add rows like this to accomodate the blank rows that automatically
			//   get added in	
			if( tableModel.getRowCount() > 0 )
				tableModel.getRows().insertElementAt( newRow, 0 );
			else
				tableModel.getRows().addElement( newRow );
			
			// put a holder value for the model in row location i
			if( rowData[i].length >= 7 && rowData[i][6] != null )
//				tableModel.createPsuedoPointValue( Long.parseLong(rowData[i][6].toString()),
//								((Timestamp)rowData[i][0]).getTime(), //TimeStamp
//								CommonUtils.createString( rowData[i][1] ), //DeviceName
//								Integer.parseInt(rowData[i][7].toString()), //SOE_Tag
//								0 );
				tableModel.createPsuedoPointValue( 0 );


			tableModel.addBlankRowIfNeeded();
		}
	
		tableModel.fireTableDataChanged(); // is actually fireTableDataChanged();
	
		return pageCount;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/24/00 1:24:13 PM)
	 * @param sql java.lang.String
	 */
	public synchronized int createRowsForRawPointHistoryView(Date date, int page) 
	{
		tableModel.setCurrentDate( date );
		
		String rowCountQuery = "select min(s.logid), max(s.logid)" +
						  " from rawpointhistory s" +
						  " where s.timestamp >= ?" +
						  " and s.timestamp < ?";
		
		String rowQuery = "select r.timestamp, y.PAOName, p.pointname, r.value, r.quality, " +
						  " r.pointid " +  // this extra pointid column needs to be here
						  " from rawpointhistory r, YukonPAObject y, point p " +
						  " where r.pointid=p.pointid and y.PAObjectID=p.PAObjectID " +
						  " and r.timestamp >= ? " +
						  " and r.timestamp < ? " +
						  " order by r.changeid desc";
						  
		GregorianCalendar lowerCal = new GregorianCalendar();
		lowerCal.setTime( date );
		lowerCal.set( lowerCal.HOUR_OF_DAY, 0 );
		lowerCal.set( lowerCal.MINUTE, 0 );
		lowerCal.set( lowerCal.SECOND, 0 );
		lowerCal.set( lowerCal.MILLISECOND, 000 );
   
		GregorianCalendar upperCal = new GregorianCalendar();
		upperCal.setTime( date );
		upperCal.set( upperCal.HOUR_OF_DAY, 23 );
		upperCal.set( upperCal.MINUTE, 59 );
		upperCal.set( upperCal.SECOND, 59 );
		upperCal.set( upperCal.MILLISECOND, 999 );



		Object[] objs = new Object[2];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();

		//get the row count, min, max   
		Object[][] rowCount = DataBaseInteraction.queryResults( rowCountQuery, objs );
		
		
		//set the correct page number values
		processPagingValues(						  
				rowCount[0][0].toString(),
				rowCount[0][1].toString(),
				page );

						  
		objs = new Object[4];
		objs[0] = lowerCal.getTime();
		objs[1] = upperCal.getTime();
		objs[2] = new Integer(qMin);
		objs[3] = ( qRowCnt <= TDCDefines.MAX_ROWS
						? new Integer(qMax)
						: new Integer(qMin + TDCDefines.MAX_ROWS) );

		//get the actual data
		Object[][] rowData = DataBaseInteraction.queryResults( rowQuery, objs );

		if( rowData == null )
			return -1;

	
		for( int i = 0; i < rowData.length; i++ )
		{
			Vector newRow = new Vector( tableModel.getColumnCount() );
			for( int j = 0; j < tableModel.getColumnCount(); j++ )
				newRow.addElement( "" );  // put these into the vector just as place holder values

			// set TimeStamp
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) ) // format of ORACLE: "2000-06-09 16:34:34.0"
			{
				newRow.setElementAt( new ModifiedDate( ((Timestamp)rowData[i][0]).getTime() ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTTIMESTAMP) );
			}
			
			// set DeviceName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_DEVICENAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][1] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_DEVICENAME) );
			
			// set PointName
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTNAME) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][2] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTNAME) );
			
			// set Value
			if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTVALUE) )
				newRow.setElementAt( CommonUtils.createString( rowData[i][3] ), tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTVALUE));

			try
			{
				// set Quality
				if( tableModel.getColumnTypeName().contains(CustomDisplay.COLUMN_TYPE_POINTQUALITY) )
					newRow.setElementAt( 
								PointQualities.getQuality(
								Integer.parseInt(CommonUtils.createString(rowData[i][4]))), 
								tableModel.getColumnTypeName().indexOf(CustomDisplay.COLUMN_TYPE_POINTQUALITY));
			}
			catch( CTIPointQuailtyException ex )
			{
				CTILogger.error( "An error occured with the point quality", ex );
			}	


			tableModel.checkRowExceedance();

			// we must add rows like this to accomodate the blank rows what automatically
			//   get added in	
			if( tableModel.getRowCount() > 0 )
				tableModel.getRows().insertElementAt( newRow, 0 );
			else
				tableModel.getRows().addElement( newRow );
			
			// put a holder value for the model in row location i
			if( rowData[i].length >= 7 && rowData[i][6] != null )
				tableModel.createPsuedoPointValue( 0 );
//				tableModel.createPsuedoPointValue( 
//						Long.parseLong( rowData[i][6].toString() ),
//						new Date().getTime(),
//						"DUMMY",
//						0,		
//						0 );

			tableModel.addBlankRowIfNeeded();
		}
	
		tableModel.fireTableDataChanged(); // is actually fireTableDataChanged();
	
		return pageCount;
	}

}
