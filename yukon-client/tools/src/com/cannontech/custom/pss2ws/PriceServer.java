/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.custom.pss2ws;

import java.io.FileWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.net.ssl.SSLSession;

import sun.util.calendar.Gregorian;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.util.ServletUtil;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * Function 1:
 * Function #, Unique Point ID, value, data quality, timestamp,daylight savings flag
 * where
 *	Point ID is a alpha numeric id unique to this file
 *	value is numeric (decimal for analogs, integer for statues) *	data quality (G for good, M for manual, B for bad)
 *	timestamp (mm/dd/yyyy hh:mm:ss)
 *	daylight savings flag (D for daylight savings, S for standard time)
 * example: 1,PricePoint,2,G,08/08/2005 13:00:00,D
 * would import the value 2, timestamp of 1 oclock today into a point with a translation name of PricePoint.
 * The only rule is the point name must be unique.
 */
public class PriceServer implements Runnable{
	private String writeToFileName = "C:/yukon/server/import/pricePoint.txt";
	private String pointName = "PricePoint";
	private Thread priceThread = null;
	private String endpointURL = "https://www.electricprice.net/PSS2WS/PSS2WS";
	private GregorianCalendar nextRunTime = null;
	private int runIntervalInSeconds = 60;
	private SimpleDateFormat tsFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private PricePoint pricePoint = null;
	private PSS2WSSEI portStub;
	private PriceSchedule priceSchedule = null;

	// username and password
	private String userName = "target";
	private String password = "SW1623a";
	// ssl trust store file
	private String certFileName = "C:/yukon/server/config/cacerts.jks";
	// ssl trust store password
	private String certPassword = "epriceLBL";
	
	private class PricePoint{
		
		private int functionNumber = 1;
		private String pointName = "PricePoint";
		private double value;
		private char quality = 'G';
		private long timestamp;
		private char dstFlag = 'D';

		/**
		 * 
		 */
		public PricePoint(String name_, double value_, long timestamp_) {
			this(name_, value_, 'G', timestamp_);
		}
		
		/**
		 * 
		 */
		public PricePoint(String pointName_, double value_, char quality_, long timestamp_) {
			super();
			pointName = pointName_;
			value = value_;
			quality = quality_;
			timestamp = timestamp_;
			
			if( TimeZone.getDefault().inDaylightTime(new Date(timestamp)))
				dstFlag = 'D';
			else
				dstFlag = 'S';
						
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String str = functionNumber + ",";
			str += pointName + ",";
			str += value + ",";
			str += quality + ",";
			str += tsFormat.format(new Date(timestamp)) + ",";
			str += dstFlag;
			return str;
		}
	}
	
	public static void main(String [] args)
	{
		CTILogger.info("PriceServer - Yukon Version: " + VersionTools.getYUKON_VERSION() + " - Yukon Database Version: " +VersionTools.getDatabaseVersion());
		System.setProperty("cti.app.name", "PriceServer");
		PriceServer priceServer = new PriceServer();

		priceServer.loadParameters(args);
		priceServer.init();
		
		//start the process
		priceServer.start();
	}
	
	/**
	 * Write recordVector to getDirector()+getFileName().
	 * set recordVector = null when write to file is completed.
	 * Method writeToFile.
	 */
	public void writeToFile()
	{
		try
		{
			java.io.FileWriter outputFileWriter = new FileWriter(writeToFileName);
			outputFileWriter.write( getPricePoint().toString() );
			outputFileWriter.flush();
			outputFileWriter.close();
			CTILogger.info("Exported PricePoint(" + getPricePoint().toString() + ") to File: " + writeToFileName);
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		java.util.Date now = null;
//		figureNextRunTime();
		
		try
		{
			for (;;)
			{
				now = new java.util.Date();
				if( getNextRunTime().getTime().compareTo(now) <= 0)
				{
					try {
						priceSchedule = portStub.getPriceSchedule(priceSchedule);
						setPricePoint(new PricePoint(pointName, priceSchedule.getCurrentPriceDPKWH(), getNextRunTime().getTimeInMillis()));
						CTILogger.info("New PricePoint received: " +(getNextRunTime().getTime()) + " - " + getPricePoint().value);
						//Use the time that the request was sent out.
						writeToFile();
					} 
					catch (RemoteException e1) {
						e1.printStackTrace();
					}
						
					finally
					{
						figureNextRunTime();
					}						
				}
				Thread.sleep(1000);
			}		
		}
		catch (InterruptedException ie)
		{
			CTILogger.info("Interrupted Exception!!!");
		}

	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (12/4/2000 2:27:20 PM)
	 */
	private void figureNextRunTime()
	{
		GregorianCalendar tempCal = new GregorianCalendar();
		long nowInMilliSeconds = tempCal.getTimeInMillis();
		long aggIntInMilliSeconds = getRunIntervalInSeconds() * 1000;
		long tempSeconds = (nowInMilliSeconds-(nowInMilliSeconds%aggIntInMilliSeconds))+aggIntInMilliSeconds;

		if( tempSeconds < (getNextRunTime().getTime().getTime() + aggIntInMilliSeconds) )
		{
			tempSeconds += aggIntInMilliSeconds;
		}
		
		getNextRunTime().setTime(new java.util.Date(tempSeconds));
		CTILogger.debug("Next Runtime: " + getNextRunTime().getTime());
	}
	/**
	 * @return
	 */
	public GregorianCalendar getNextRunTime() {
		if( nextRunTime == null)
			nextRunTime = new GregorianCalendar();
		return nextRunTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/7/2000 11:43:39 AM)
	 * @return java.lang.Integer
	 */
	private int getRunIntervalInSeconds()
	{
		return runIntervalInSeconds;
	}

	private void setRunIntervalInSeconds(int seconds)
	{
		if( seconds < 60)
			runIntervalInSeconds = 60;
		else if (seconds > 300)
			runIntervalInSeconds = 300;
		else
			runIntervalInSeconds = seconds;
		CTILogger.info("RunTime Interval set to: " + runIntervalInSeconds + " seconds.");
	}
	

	/** 
	 * Start us
	 */
	public void start()
	{
//		retrieveParameters();
//		loadParameters();
		priceThread = new Thread( this, "PriceServer" );
		priceThread.start();
	}

	/** 
	 * Stop us
	 */
	public void stop()
	{
		try
		{
			Thread t = priceThread;
			priceThread= null;
			t.interrupt();
		}
		catch (Exception e)
		{}
	}
	
	public boolean isRunning()
	{
		return priceThread!= null;
	}
	/**
	 * @return
	 */
	public PricePoint getPricePoint() {
		return pricePoint;
	}

	/**
	 * @param point
	 */
	public void setPricePoint(PricePoint point) {
		pricePoint = point;
	}

	public void init()
	{
		// set up ssl
		System.setProperty("javax.net.ssl.trustStore", certFileName);
		System.setProperty("javax.net.ssl.trustStorePassword", certPassword);
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: " + urlHostName +
				  " vs. " + session.getPeerHost());
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);


		try {
			PSS2WS service = new PSS2WSLocator();
			((PSS2WSLocator)service).setPSS2WSSEIPortEndpointAddress(endpointURL);
			portStub = service.getPSS2WSSEIPort();
			((PSS2WSSEIBindingStub)portStub)._setProperty(Stub.USERNAME_PROPERTY, userName);
			((PSS2WSSEIBindingStub)portStub)._setProperty(Stub.PASSWORD_PROPERTY, password);
			((PSS2WSSEIBindingStub)portStub)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpointURL);


		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadParameters(String[] args)
	{
		if( args != null )
		{		
			for ( int i = 0; i < args.length; i++)
			{
				String arg = args[i];
				// Check the delimiter of '=', if not found check ':'
				int startIndex = arg.indexOf('=');
				if( startIndex < 0)
					startIndex = arg.indexOf(':');
				startIndex += 1;
					
				if( arg.toLowerCase().startsWith("point"))
					pointName = arg.substring(startIndex);
	
				else if( arg.toLowerCase().startsWith("export"))
					writeToFileName = arg.substring(startIndex);
					
				else if (arg.toLowerCase().startsWith("run"))	//RunTimeInSeconds
					runIntervalInSeconds = Integer.valueOf(arg.substring(startIndex).trim()).intValue();
					
				else if( arg.toLowerCase().startsWith("user"))	//username
					userName = arg.substring(startIndex);
				
				else if( arg.toLowerCase().startsWith("pass"))	//password
					password = arg.substring(startIndex);
					
				else if( arg.toLowerCase().startsWith("certfile"))	//certificate filename and path
					certFileName = arg.substring(startIndex);
				
				else if( arg.toLowerCase().startsWith("certpass")) //certificate password
					certPassword = arg.substring(startIndex);
			}
		}
	}
/*	private void retrieveParameters()
	{
		StringBuffer sql = new StringBuffer	("SELECT PARAMNAMES, PARAMVALUES FROM YUKONSERVICES " +
			" WHERE SERVICEID = 6 ");

		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.info("Error getting database connection.");
				return null;
			}
			else
			{
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
				groupIDs = new Vector();
				while( rset.next())
					groupIDs.add(new Integer( rset.getInt(1) ) );
			}
		}
	
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
	}*/
}