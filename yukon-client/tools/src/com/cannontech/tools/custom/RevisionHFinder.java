/*
 * Created on Oct 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.custom;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RevisionHFinder implements com.cannontech.message.util.MessageListener
{
	//log file (and path) to read from 
	private String logFileName = "";
	
	//file to write results report to
	private String reportFile = "C:/yukon/RevisionHReport.txt";
	
	//Vector of Strings (results from sql query)
	public Vector sqlData = new Vector();
	
	//Vector of Pairs (revision H meters <DeviceID, resultString>
	public Vector revHReads= new Vector();
	
	//vector of Integers (missed meter deviceIds)
	public Vector missedIDs = new Vector();
			
	/** Store last Porter request message, for use when need to send it again (loop) */
	public Request porterRequest = null;
	
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static volatile long currentUserMessageID = 1;

	/** HashSet of userMessageIds for this instance */
	private java.util.Set requestMessageIDs = new java.util.HashSet(10);
	
	// Number of retries if reading from collection group only.
	private int numRetries = 3;
	private String collGroup = "";
	
	/**
	 * logfile = the filename and path of the log file to be parsed
	 * coll[group] = the collection group to read the model from
	 * retry = the number of retries for missed meters, only valid in conjunction with collection group
	 * *NOTE* if logfile is set, the it is used and NOT the collection group
	 * 		Only one of coll or logfile should be set.
	 * reportfile = the path and filename to write result data to
	 * @param args
	 */
	public static void main(String[] args)
	{
		RevisionHFinder finder = new RevisionHFinder();
		if( args.length > 0 )
		{
			for (int i = 0; i < args.length; i++)
			{
				// Check the delimiter of '=', if not found check ':'
				int startIndex = args[i].indexOf(':');
				String key = args[i].substring(0, startIndex).toLowerCase();
				startIndex += 1;
				String value = args[i].substring(startIndex);
				
				if( key.startsWith("log"))
				{
					finder.logFileName = value;
				}
				else if( key.startsWith("retry"))
				{
					finder.numRetries = Integer.valueOf(value).intValue();
				}
				else if( key.startsWith("coll"))
				{
					finder.collGroup = value;
				}
				else if( key.startsWith("report"))
				{
					finder.reportFile = value;
				}
			}
		}
		if( finder.logFileName != "")
		{	//Find revH meters from the log file
			finder.retrieveFromLogFile();
		}
		else if( finder.collGroup != "")
		{
			//Add messageListener
			finder.getPilConn().addMessageListener(finder);
			
			//Find revH meters from the collection group
			finder.readCollectionGroup(finder.collGroup);
			while(!finder.getRequestMessageIDs().isEmpty())
			{
				try
				{
					//Sleep for a moment and let the return messages complete
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			//Attempt to retry reading the missed meter reads
			while(finder.numRetries > 0 && !finder.missedIDs.isEmpty())
			{
				finder.numRetries--;
				finder.readDevIDs((Vector)finder.missedIDs.clone());
				while(!finder.getRequestMessageIDs().isEmpty())
				{
					try
					{
						//Sleep for a moment and let the return messages complete
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}			
			}
		}

		finder.sqlData = finder.getSQLData();
		finder.writeToFile();		
		System.exit(0);
	}
	


	/**
	 * Returns a vector of strings to be written to a file of completed RevH meter reads. 
	 */
	private Vector getSQLData()
	{
		long timer = System.currentTimeMillis();
		
		if (revHReads.isEmpty())
			return null;
			
		Vector returnVector = new Vector();
	
		String sql = "SELECT PAONAME, PAOBJECTID, DCS.ADDRESS, TYPE, METERNUMBER, COLLECTIONGROUP " +
					" FROM DEVICEMETERGROUP DMG, YUKONPAOBJECT PAO, DEVICECARRIERSETTINGS DCS " +
					" WHERE PAOBJECTID = DMG.DEVICEID " + 
					" AND PAOBJECTID = DCS.DEVICEID " + 
					" AND PAOBJECTID IN (" + ((Integer)((Pair)revHReads.get(0)).getFirst()).intValue();
					
					for (int i = 1; i < revHReads.size(); i++)
					{
						sql += ", " + ((Integer)((Pair)revHReads.get(i)).getFirst()).intValue();
					}
					sql += ") ORDER BY COLLECTIONGROUP , PAONAME";
					
		returnVector.add(sql);
		returnVector.add("PAONAME            PAOID    ADDRESS      TYPE       METERNUMBER     COLLECTIONGROUP");
		returnVector.add("-----------------------------------------------------------------------------------"); 
							
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		System.out.println(sql);
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
				return null;
			}
			else
			{
				pstmt = conn.prepareStatement(sql);
				rset = pstmt.executeQuery();
		
				com.cannontech.clientutils.CTILogger.info(" *Start looping through return resultset");
		
				while (rset.next())
				{
					String paoName = rset.getString(1);
					String paoID = String.valueOf(rset.getInt(2));
					String address = rset.getString(3);
					String type = rset.getString(4);
					String meterNum = rset.getString(5);
					String collGrp = rset.getString(6);
					String writeString = paoName + "\t" + paoID + "\t" + address + "\t" + type + "\t" + meterNum + "\t" + collGrp;
					returnVector.add(writeString);
				}
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
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				e2.printStackTrace();//sometin is up
			}	
		}
		com.cannontech.clientutils.CTILogger.info("Revision H Meter Data Collection : Took " + (System.currentTimeMillis() - timer));
		return returnVector;
	}

	/**
	 * 
	 */
	public void retrieveFromLogFile()
	{
		System.out.println("Attempting to parse Revision H meters from log file: " + logFileName);
		File file = new File(logFileName);	
		java.io.RandomAccessFile raFile = null;
		try
		{
			// open file		
			if( file.exists() )
			{
				raFile = new RandomAccessFile( file, "r" );
						
				long readLinePointer = 0;
				long fileLength = raFile.length();
				String deviceIDLine = "";
				boolean writing = false;
				
				Pair revH = null;
				Integer tempID = null;
				String tempValue = null;
				while ( readLinePointer < fileLength )  // loop until the end of the file
				{
					String line = raFile.readLine();  // read a line in
					
					if( writing )	//Found RevH meter, writing it to file
					{
						tempValue += "\r\n" + line;
						if( line.length() == 0)
						{
							revH = new Pair(tempID, tempValue);
							revHReads.add(revH);
							writing = false;
						}
						
					}
					else if(line.indexOf("deviceid: ") >= 0)
					{
						deviceIDLine = line;
					}
					else if(line.indexOf("Revision H") >= 0)
					{
						writing = true;
						tempValue = deviceIDLine;
						tempValue += "\r\n"+line;

						int start = deviceIDLine.indexOf("deviceid: ")+10;
						int end = end = deviceIDLine.indexOf(" ", start);
						String devIDStr = deviceIDLine.substring(start, end);

						tempID = new Integer(devIDStr);
					}
					readLinePointer = raFile.getFilePointer();
				}
			}
			else
				return;
	
			// Close file
			raFile.close();						
		}
		catch( java.io.FileNotFoundException fnfe)
		{
			com.cannontech.clientutils.CTILogger.info("*** File Not Found Exception:");
			com.cannontech.clientutils.CTILogger.info("  " + fnfe.getMessage());
			com.cannontech.clientutils.CTILogger.error( fnfe.getMessage(), fnfe );
		}
		catch( java.io.IOException ioe )
		{
			com.cannontech.clientutils.CTILogger.error( ioe.getMessage(), ioe );
		}
		finally
		{
			try
			{
				if( raFile != null)
					raFile.close();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();		
			}
		}	
	}
	
	public void readCollectionGroup(String collGroup)
	{
		try
		{
			Integer[] devIds = DeviceMeterGroup.getDeviceIDs_CollectionGroups(CtiUtilities.getDatabaseAlias(), collGroup);
			Vector vectorDevIds = new Vector(devIds.length);
			for (int i = 0; i< devIds.length; i++)
				vectorDevIds.add(devIds[i]);
			
			readDevIDs(vectorDevIds);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 */
	private void readDevIDs(Vector deviceIDs)
	{
		//remove all previously missed deviceIds...this is new life
		missedIDs.clear();
		for(int i = 0; i < deviceIDs.size(); i++)
		{
			porterRequest = new Request( ((Integer)deviceIDs.get(i)).intValue(), "getconfig model", currentUserMessageID );
			porterRequest.setPriority(14);
			if( getPilConn().isValid() )
			{
				getPilConn().write( porterRequest);
				getRequestMessageIDs().add(new Long(currentUserMessageID));
				generateMessageID();
			}
		}
	}



	private IServerConnection getPilConn()
	{
		return ConnPool.getInstance().getDefPorterConn();        
	}
		
	/**
	 * generate a unique mesageid, don't let it be negative
	 * @return long currentMessageID
	 */
	private synchronized long generateMessageID() {
		if(++currentUserMessageID == Integer.MAX_VALUE) {
			currentUserMessageID = 1;
		}
		return currentUserMessageID;
	}

	public java.util.Set getRequestMessageIDs()
	{
		return requestMessageIDs;
	}



	/* (non-Javadoc)
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		if(e.getMessage() instanceof Return)
		{
			Return returnMsg = (Return) e.getMessage();
			synchronized(this)
			{
				if( !getRequestMessageIDs().contains( new Long(returnMsg.getUserMessageID())))
					return;

				System.out.println(returnMsg.getDeviceID() + " " + returnMsg.getResultString() + "  " + returnMsg.getExpectMore());
				synchronized ( RevisionHFinder.class )
				{
					if( returnMsg.getExpectMore() == 0)	//Only send next message when ret expects nothing more
					{
						int devID = returnMsg.getDeviceID();
						String result = returnMsg.getResultString();
						if( result.indexOf("Revision ") >= 0)
						{
							//We received a good message
							if( result.indexOf("Revision G") >= 0)
							{
								Pair revH = new Pair(new Integer(devID), result); 
								revHReads.add(revH);
							}
						}
						else
						{
							//Not a valid return message, save the deviceID and try again!
							missedIDs.add(new Integer(devID));						
						}
						getRequestMessageIDs().remove(new Long(returnMsg.getUserMessageID()));						
					}
					else if( returnMsg.getResultString().indexOf("NoMethod") >= 0 ||
							returnMsg.getResultString().indexOf("do not") >= 0)
					{
						getRequestMessageIDs().remove(new Long(returnMsg.getUserMessageID()));
					}
					
				}
			}
		}
	}
	public void writeToFile()
	{
		try
		{
			int index = reportFile.lastIndexOf('/');
			if( index < 0)
				index = reportFile.lastIndexOf('\\');
			String path = reportFile.substring(0, index);
			
			java.io.File file = new java.io.File(path);
			file.mkdirs();

			java.io.FileWriter writer = new java.io.FileWriter( reportFile);
			String endline = "\r\n";

			if( logFileName != "")
				writer.write("Data parsed from file: " + logFileName + endline + endline);
			else if( collGroup != "")
				writer.write("Data read from collection group: " + collGroup + endline + endline);

			if( sqlData != null)
			{
				for (int i = 0; i < sqlData.size(); i++)
					writer.write(sqlData.get(i) + endline);
			}
			
			writer.write(endline + "Revision H Meter Data From Return Message."+ endline);
			writer.write("------------------------------------------"+ endline);
			if( revHReads != null)
			{
				for (int i = 0; i < revHReads.size(); i++)
				{
					Pair revH = (Pair)revHReads.get(i);
					writer.write("DeviceID: " + revH.getFirst() + endline);
					writer.write("ResultString: " + revH.getSecond() + endline);
				}
			}
			
			writer.write(endline + "Missed Meter DeviceIDs, never read successfully."+ endline);
			writer.write("------------------------------------------------"+ endline);
			
			if( missedIDs != null)
			{
				for (int i = 0; i < missedIDs.size(); i++)
					writer.write(missedIDs.get(i) + endline);
			}
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeDatFile");
			e.printStackTrace();
		}
	}
}
