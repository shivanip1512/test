/*
 * Created on Oct 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yc.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.Device;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.gui.YC;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class YCBean extends YC implements MessageListener, HttpSessionBindingListener
{
    private java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");

	private Vector deviceIDs = null;
    //Contains <String>serialType to <Vector<String>> serialNumbers
	private HashMap serialTypeToNumberMap = null;

	private int userID = 0;
	
	/** Contains Integer(pointID), Object(PointData) values */
	private Map pointIDToRecentPDMap = null;
	
	/** Contains Integer(pointID), Object(Map of Timestamp to Double(value) values)*/
	private Map energyPtToPrevTSMapToValueMap = new TreeMap();
	
	/** Contains String(name from returnMessage parse), Object(PointData, made up one though) values */
	private Map returnNameToRecentPDMap = null;

	/** A Parameter for LPDate that a "bean" understands" */
	private String lpDateStr = "";
	
	/** A Date for LP query */
	private Date lpDate = null;
    
    private int peakProfileDays = 5;
	
	/** A pointID for selecting Data (historical RPH mainly)*/
	private int pointID = PointTypes.SYS_PID_SYSTEM;
	
	/** Contains Integer(deviceID) to Integer(discAddress, -1 if none) values */
	private Map deviceIDToDiscAddressMap  = new HashMap();
	
	/** A vector of RPH data, for the currently selected point 
	 * This vector of data should only live as long as a point and timestamp 
	 * selected do not change, so clear it out on pointID or timestamp changes*/
	private Vector currentRPHVector = new Vector();

	/** Valid route types for serial commands to be sent out on */
	private int [] validRouteTypes = new int[]{
		RouteTypes.ROUTE_CCU,
		RouteTypes.ROUTE_MACRO
		};	
	/**
	 * 
	 */
	public YCBean()
	{
		super();
	}

	/* (non-Javadoc)
	 * Sets the current deviceID from the session.
	 * Loads a collection of points to register with dispatch.
	 * @see com.cannontech.yc.gui.YC#setDeviceID(int)
	 */
	public void setDeviceID(int deviceID_)
	{
		if( deviceID_ != getDeviceID())
		{
			super.setDeviceID(deviceID_);
			if( deviceID_ > Device.SYSTEM_DEVICE_ID && !getDeviceIDs().contains(new Integer(deviceID_)))
				getDeviceIDs().addElement(new Integer(deviceID_));
			
			//Remove data from other devices..we don't care about it anymore
			clearResultText();
			try {
				setCommandString("");
			} catch (PaoAuthorizationException e) {
				//IGNORE, we're clearing it out, we have the right to do this.
			}
			clearErrorMsg();
            clearCurrentData();
		}
	}

	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialType_, String serialNumber_)
	{
		if( serialType_ != null && serialNumber_ != null)
		{
			super.setSerialNumber(serialNumber_);
			if( serialNumber_.length() > 0)// && !getSerialNumbers().contains(serialNumber_))
			{
				Vector<String> serialNumbers = getSerialNumbers(serialType_);
				if( serialNumbers == null)
					serialNumbers = new Vector<String>();
				
				if( !serialNumbers.contains(serialNumber_))
					serialNumbers.add(serialNumber_);
					
				getSerialTypeToNumberMap().put(serialType_, serialNumbers);
			}
			clearErrorMsg();
		}
	}

	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialNumber_)
	{
		setSerialNumber(getDeviceType(), serialNumber_);
	}	
	
	/* (non-Javadoc)
	 * Load the data maps with the returned pointData 
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();
		if( in instanceof Return)
		{
			Return returnMsg = (Return)in;
			if ( returnMsg.getStatus() != 0)	//Error Message!
			{
				if( getErrorMsg().indexOf(returnMsg.getCommandString()) < 0)	//command string not displayed yet
					setErrorMsg(getErrorMsg() + "<br>* Command Failed - " + returnMsg.getCommandString());
				DeviceErrorTranslatorDao deviceErrorTrans = YukonSpringHook.getBean("deviceErrorTranslator", DeviceErrorTranslatorDao.class);
				DeviceErrorDescription deviceErrorDesc = deviceErrorTrans.translateErrorCode(returnMsg.getStatus());
				setErrorMsg( getErrorMsg() + "<BR><B>"+deviceErrorDesc.getCategory()+"</B> -- " 
						+ deviceErrorDesc.getDescription() + "<BR>" + returnMsg.getResultString());
			}
			
			String resultStr = returnMsg.getResultString();
			if(returnMsg.getVector().size() > 0 )
			{
				for (int i = 0; i < returnMsg.getVector().size(); i++)
				{
					Object o = returnMsg.getVector().elementAt(i);
					if (o instanceof PointData)
					{
						PointData point = (PointData) o;
						Integer x = new Integer(point.getId());
						getPointIDToRecentPDMap().put(x, point);
						CTILogger.info("Put (pointIDToRecentPDMap): " +x + ":"+point.getId()+"-"+point.getValue()+"-"+point.getPointDataTimeStamp());
						
						// BAD HACK:  Stuff the pointData string into the result string for OUTAGE POINT DATA!!!!
						// We could only display the most recent data value if the point exists in the database only, 
						//  By sending it through the return string parser, we'll get entries in the returnNameToRecentPDMap for outage info
						if( returnMsg.getCommandString().toLowerCase().indexOf("outage") >-1)
							resultStr += point.getStr() + "\n";	//Need to add new line in order to utilize normal outage parsing below.
						
						//Clear the Error Message Log, we did eventually read the meter
						//This is a request from Jeff W. to only display the error messages when no data is returned.
						clearErrorMsg();
					}
				}
			}
//			else	//Tried to do an else here because there is no reason to do both,
					//But if the ReturnMessage contains more the one point's data, but only one of the points 
					//  is in the returnMsg.getVector(), we have no real way of knowing that we need to parse the 
					// returnMsg.resultString.  Therefore, we do them both.
			{
				int multLineIndex = 0;
				int beginIndex = 0;
				String tempResult = resultStr;
				//Loop through the resultStr in case it is a multi-line return with more than one point info.
				do
				{
					multLineIndex = resultStr.indexOf('\n', beginIndex);					
					if(multLineIndex > 0)
					{
						tempResult = resultStr.substring(beginIndex, multLineIndex);
						beginIndex = multLineIndex+1;
						
					}
					else
					{
						tempResult = resultStr.substring(beginIndex);
					}

					// The order of these if statements is important since some of the checks will find the wrong data if not parsed for early enough. 
					if( returnMsg.getCommandString().toLowerCase().indexOf("lp") > -1 &&
							returnMsg.getCommandString().toLowerCase().indexOf("peak") > -1 )	//we have a lp peak report command
					{	
						//CAUTION!!!  This should be in the first line of the result message.
						if( tempResult.toLowerCase().indexOf("load profile report") >= 0)
						{
							tempResult = returnMsg.getResultString(); //reset the result string to the whold message result string.
							multLineIndex = -1;	//We want all the data, not just one line at a time, so after this we are done with our loop.
					        
							//The String "ID" is the deviceID + "PeakChannel " + <channelNumber (parsed from commandString)> 
							String id = String.valueOf(returnMsg.getDeviceID()) + "PeakChannel ";
							int channelStrIndex = returnMsg.getCommandString().toLowerCase().indexOf("channel");
							for (int i = channelStrIndex; i < returnMsg.getCommandString().length(); i++)
							{
								char currentChar = returnMsg.getCommandString().charAt(i);
								if (Character.isDigit(currentChar))	//hopefully we found our channel number!
								{
									id += currentChar;
									break;
								}
							}
							System.out.println("ID: " + id);
							PointData fakePtData = new PointData();
							fakePtData.setStr(tempResult);
							fakePtData.setTimeStamp(returnMsg.getTimeStamp());
							fakePtData.setTime(returnMsg.getTimeStamp());
							//The key is deviceID+STRING parsed from return message
							getReturnNameToRecentPDMap().put(id, fakePtData);
							CTILogger.info("Put (returnNameToRecentReadMap): " +id + ":"+fakePtData.getId()+"-"+fakePtData.getValue()+"-"+fakePtData.getPointDataTimeStamp());					    
						}
					}
					//Add check for command String and pointData vector not empty
					else if( returnMsg.getCommandString().indexOf("lp") > 0 &&
							returnMsg.getCommandString().indexOf("channel") >  0)	//we have a lp data collection (and archive) command.
					{
						tempResult = returnMsg.getResultString(); //reset the result string to the whold message result string.
						multLineIndex = -1;	//We want all the data, not just one line at a time, so after this we are done with our loop.
					    
						//The String "ID" is the deviceID + "Channel " + <channelNumber (parsed from commandString)> 
						String id = String.valueOf(returnMsg.getDeviceID()) + "DataChannel ";
						int channelStrIndex = returnMsg.getCommandString().toLowerCase().indexOf("channel");
						for (int i = channelStrIndex; i < returnMsg.getCommandString().length(); i++)
						{
							char currentChar = returnMsg.getCommandString().charAt(i);
							if (Character.isDigit(currentChar))	//hopefully we found our channel number!
							{
								id += currentChar;
								break;
							}
						}

						if ( returnMsg.getVector().size() > 0)		// LP Data collection (6 intervals) coming back on a Defined point
						{
							tempResult = "";	//clear out this string and build a custom one
							for (int i = 0; i < returnMsg.getVector().size(); i++)
							{
								Object o = returnMsg.getVector().elementAt(i);
								if (o instanceof PointData)
								{
									PointData point = (PointData) o;
									tempResult += point.getStr() + "\n";
								}
							}
					        
						}
						PointData fakePtData = new PointData();
						fakePtData.setStr(tempResult);
						fakePtData.setTimeStamp(returnMsg.getTimeStamp());
						fakePtData.setTime(returnMsg.getTimeStamp());
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentPDMap().put(id, fakePtData);
						CTILogger.info("Put (returnNameToRecentReadMap): " +id + ":"+fakePtData.getId()+"-"+fakePtData.getValue()+"-"+fakePtData.getPointDataTimeStamp());
						
					} else if ( tempResult.toLowerCase().indexOf("role ") > 0) {	//getconfig role # stuff
						//do nothing.  No point data information can be retrieved from this command
						// but since the result string has '=' (equal sign) we need to "if" this out of here.
						
					} else if ( tempResult.toLowerCase().indexOf("disconnect config sent") > 0)	{ //putcofig disconnect stuff
						//do nothing.  No point data information can be retrieved from this command
						// but since the result string has 'disconnect' we need to "if" this out of here.
						
					} else if( tempResult.indexOf('=') > 0) {
						int equal = tempResult.indexOf('=');
						int slash = tempResult.indexOf('/')+1;
				
						//The String "ID" is the deviceID + the value after the slash and before the equal sign
						String id = String.valueOf(returnMsg.getDeviceID()) + tempResult.substring(slash, equal).trim();
					
						//The Double "value" is the value after the equal sign to the end or to the next space character
						Double value = null;
						int at = tempResult.indexOf('@');
						int dashDash = tempResult.indexOf("--");
						
						if(at > 0) {
							if (! (tempResult.substring(equal+1, at).trim().equalsIgnoreCase("(invalid data)") ) ) 
								value = Double.valueOf(tempResult.substring(equal+1, at).trim());
						}
						else if( dashDash > 0)
							value = Double.valueOf(tempResult.substring(equal+1, dashDash).trim());
						else 
							value = Double.valueOf(tempResult.substring(equal+1).trim());

						//The Time is the value after '@' to the end of the line
						Date timestamp = new Date();
						if (at > 0)
						{
							String dateStr = "";
							if( dashDash > 0)
								dateStr = tempResult.substring(at+1, dashDash).trim();
							else 
								dateStr = tempResult.substring(at+1).trim();
						
							timestamp = ServletUtil.parseDateStringLiberally(dateStr);
						}
						PointData fakePtData = new PointData();
						if( value != null)
							fakePtData.setValue(value.doubleValue());
						fakePtData.setTimeStamp(timestamp);
						fakePtData.setTime(timestamp);
						fakePtData.setType(PointTypes.DEMAND_ACCUMULATOR_POINT);
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentPDMap().put(id, fakePtData);
						CTILogger.info("Put (returnNameToRecentReadMap): " +id + ":"+fakePtData.getId()+"-"+fakePtData.getValue()+"-"+fakePtData.getPointDataTimeStamp());
					}
					else if ( tempResult.indexOf("Disconnect") > 0)	//Disconnect status stuff
					{
						int colon = tempResult.indexOf(':');
						int slash = tempResult.indexOf('/')+1;
	
						//The String "ID" is the deviceID + the value after the slash and before the equal sign
						String id = String.valueOf(returnMsg.getDeviceID()) + tempResult.substring(slash, colon).trim();
	
						PointData fakePtData = new PointData();
						fakePtData.setStr(tempResult.substring(colon+1).trim());
						fakePtData.setValue(-1);	//unknown TODO
						fakePtData.setTimeStamp(returnMsg.getTimeStamp());
						fakePtData.setTime(returnMsg.getTimeStamp());
						fakePtData.setType(PointTypes.STATUS_POINT);
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentPDMap().put(id, fakePtData);
						CTILogger.info("Put (returnNameToRecentReadMap): " +id + ":"+fakePtData.getId()+"-"+fakePtData.getValue()+"-"+fakePtData.getPointDataTimeStamp());

					}					
					else if(returnMsg.getCommandString().toLowerCase().indexOf("outage") > -1 && tempResult.indexOf(':') > 0)	//outage stuff
					{					
						int colon = tempResult.indexOf(':');
						int slash = tempResult.indexOf('/')+1;

						//The String "ID" is the deviceID + the value after the slash and before the equal sign
						String id = String.valueOf(returnMsg.getDeviceID()) + tempResult.substring(slash, colon).trim();

						//The Timestamp is the value after the colon sign to the 'for' string
						Date date = null;
						//The duration is the value after 'for' string to the end
						Double value = new Double(-1);						
						int forIdx = tempResult.indexOf("for");
						if(forIdx > 0)
						{
							forIdx = forIdx + 3;
							date = ServletUtil.parseDateStringLiberally(tempResult.substring(colon+1, forIdx).trim());
							
							String durStr = tempResult.substring(forIdx+1).trim();
							if(!durStr.equalsIgnoreCase("(unknown duration)"))
							{
//								Only one of the daysIdx will be greater than -1, use that index
								int daysIdx = tempResult.indexOf("days,");
								if( daysIdx < 0) {
									daysIdx = tempResult.indexOf("day,");
									if( daysIdx > -1)
										daysIdx += 4;
								}
								else
									daysIdx += 5;
									
								if( daysIdx >= 0 ) {
									durStr = tempResult.substring(daysIdx+1).trim();				
								}
								
								double HH = Long.valueOf(durStr.substring(0, 2)).doubleValue() * 3600d;
								double mm = Long.valueOf(durStr.substring(3, 5)).doubleValue() * 60d;
								double ss = Long.valueOf(durStr.substring(6, 8)).doubleValue();
								value = new Double(HH + mm + ss);
								System.out.println(tempResult.substring(colon+1, forIdx).trim()+ ": TIME:"+date+":");
							}
						}
						
						PointData fakePtData = new PointData();
						fakePtData.setStr(tempResult.substring(colon+1).trim());
						fakePtData.setValue(value.doubleValue());
						fakePtData.setTimeStamp(date);
						fakePtData.setTime(date);
						fakePtData.setType(PointTypes.DEMAND_ACCUMULATOR_POINT);
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentPDMap().put(id, fakePtData);
						CTILogger.info("Put (returnNameToRecentReadMap): " +id + ":"+fakePtData.getId()+"-"+fakePtData.getValue()+"-"+fakePtData.getPointDataTimeStamp());
					}
				}
				while (multLineIndex > 0);
			}
		}
		super.messageReceived(e);		
	}

	/**
	 * A map of points found in the DB.
	 * A map of (Integer, PointData) values.
	 * Integer - the pointID of the read data
	 * PointData - the pointData from Return Message 
	 * @return
	 */
	public Map getPointIDToRecentPDMap()
	{
		if( pointIDToRecentPDMap == null)
		{
			pointIDToRecentPDMap = new HashMap(5);
		}
		return pointIDToRecentPDMap;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0)
	{
		CTILogger.info("YCBean value bound to session.");
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0)
	{
		CTILogger.info("YCBean value UnBound from session.");
        clearRequestMessage();
        getPilConn().removeMessageListener(this);
	}
	/**
	 * A map of points NOT found in the DB, stored by name instead of PointID. 
	 * A map of (String, PointData) values.
	 * String - a name found in the Return Message
	 * PointData - a "fake" pointData created to store the data parsed from the Return Message.
	 * @return
	 */
	public Map getReturnNameToRecentPDMap()
	{
		if( returnNameToRecentPDMap == null)
		{
			returnNameToRecentPDMap = new HashMap(5);
		}
		return returnNameToRecentPDMap;
	}

	/**
	 * Return PointData value from DynamicDataSource
	 * @param pointID
	 * @return
	 */
	public PointData getPointData(int pointID)
	{
		try {
	        DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
	        PointData pointData = dds.getPointData(pointID);
	        return pointData;
		} catch(DynamicDataAccessException ddae) {
			//Need to do something here.  We should tell someone.
			//When this is called from the jsp, we have already displayed the errorMsg.  So we need a different way.
		}
       	return null;
	}
	
	/**
	 * Return PointData value from poinIDToRecentReadMap, if found.
	 * If pointIDToRecentReadMap get returns null
	 * 	then attempt to find an entry in the returnNameToRecentReadMap using a regular expression.
	 * 
	 * The pointID (KEY) is determined by the deviceid, pointoffset, and pointtype parameters
	 *   If pointID is -1 (some default value) then a null PointData message will be found and
	 *   then combinations of the parameters will be used to take a best guess.
	 * @param deviceID
	 * @param pointOffset
	 * @param pointType
	 * @return
	 */
	public PointData getRecentPointData(int deviceID, int pointOffset, int pointType)
	{
		/*
		 * For LP Channel data (DataCollection and PeakReport) we do NOT want what is stored in the recentReadMap,
		 * since it is only storing the last read instead of the 6/12 intervals and the peak report, therefore we use 
		 * a different pointType instead of the Demand Accumulator type to find the points in the map by Name.  This
		 * helps with the fact that there are two types of data stored for one pointoffset/pointType key.  The below 
		 * call for pointID_ will actually return an invalid value and then allow us into the if/else to find data by name.
		 * SN 20050505
		 */
		int pointID_ = DaoFactory.getPointDao().getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType); 
		PointData pd = (PointData)getPointIDToRecentPDMap().get(new Integer( pointID_));
		if(pd == null) {
			
//			pd = getPointData(pointID_);	//load from pointChangeCache
//			if (pd == null) {
			
			//Try getting it from somewhere else?
			if( pointOffset == 20 && pointType == PointTypes.PULSE_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"powerfail count");
				if( pd == null)
					pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Blink Counter");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)power.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 1 && pointType == PointTypes.PULSE_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"kWh");
				if( pd == null)
					pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Current Meter Reading");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)kyz.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 1 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"kW");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)demand.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 11 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Peak Demand");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)peak.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 4 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Voltage");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)volt.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 1 && pointType == PointTypes.STATUS_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Disconnect Status");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentPDMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)disc.*"))
						{
							pd = (PointData)getReturnNameToRecentPDMap().get(key);
							break;
						}
					}
				}
			}			
			else if( pointOffset == 15 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{				
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Min Voltage");
			}
			else if( pointOffset == 14 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Max Voltage");
			}
			else if( pointType == PointTypes.OUTAGE_1)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 1");
			}
			else if( pointType == PointTypes.OUTAGE_2)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 2");
			}
			else if( pointType == PointTypes.OUTAGE_3)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 3");
			}
			else if( pointType == PointTypes.OUTAGE_4)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 4");
			}
			else if( pointType == PointTypes.OUTAGE_5)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 5");
			}
			else if( pointType == PointTypes.OUTAGE_6)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"Outage 6");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND && pointType == PointTypes.LP_PEAK_REPORT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"PeakChannel 1");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && pointType == PointTypes.LP_PEAK_REPORT)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"PeakChannel 4");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND && pointType == PointTypes.LP_ARCHIVED_DATA)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"DataChannel 1");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && pointType == PointTypes.LP_ARCHIVED_DATA)
			{
				pd = (PointData)getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"DataChannel 4");
			}
//			}
		}
		return pd;
	}
	/**
	 * @return
	 */
	public Vector getDeviceIDs()
	{
		if( deviceIDs == null)
			deviceIDs = new Vector();
			
		return deviceIDs;
	}
	
	/**
	 * Returns a vector of serialNumbers from the serialTypeToNumbersMap with key value of serialType_
	 * @return
	 */
	public Vector<String> getSerialNumbers(String serialType_)
	{
		return (Vector<String>)getSerialTypeToNumberMap().get(serialType_);
	}
	/**
	 * @return
	 */
	public HashMap getSerialTypeToNumberMap()
	{
		if( serialTypeToNumberMap == null)
			serialTypeToNumberMap = new HashMap();
	
		return serialTypeToNumberMap;
	}	
	
	public void setDeviceType(int devID)
	{
		LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(devID);
		deviceType = PAOGroups.getPAOTypeString(lPao.getType());
		CTILogger.debug(" DEVICE TYPE for command lookup: " + deviceType);
		setLiteDeviceTypeCommandsVector(DaoFactory.getCommandDao().getAllDevTypeCommands(deviceType));
	}
	
	/** Previous month's RPH timestamp to value map for some pointID, determined
	 * by deviceID, pointOffset, pointType*/
	public TreeMap getPrevMonthTSToValueMap(int pointID_)
	{
		if( energyPtToPrevTSMapToValueMap.get(new Integer(pointID_)) == null )
		{
			TreeMap tsToValMap = retrieveMonthlyRPHVector(pointID_);
			if( tsToValMap != null)
				energyPtToPrevTSMapToValueMap.put(new Integer(pointID_), tsToValMap);
		}
        //Always add the most recent one from PointChangeCache.  If it already exists, it will simply be overwritten
		PointData pointData = getPointData(pointID_);
		
        if (pointData != null)
        {
            TreeMap tsToValMap = (TreeMap)energyPtToPrevTSMapToValueMap.get(new Integer(pointID_));
            tsToValMap.put(pointData.getPointDataTimeStamp(), new Double(pointData.getValue()));
        }
            
		return (TreeMap)energyPtToPrevTSMapToValueMap.get(new Integer(pointID_));
	}
	
	private TreeMap retrieveMonthlyRPHVector(int pointID_)
	{
		TreeMap timestampToValueMap = new TreeMap();
		String sql = "SELECT DISTINCT POINTID, TIMESTAMP, VALUE FROM RAWPOINTHISTORY WHERE POINTID = " + pointID_ +
					" AND TIMESTAMP > ? ORDER BY TIMESTAMP ASC";
		CTILogger.info(sql.toString());	
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return null;
			}
			else
			{
				pstmt = conn.prepareStatement(sql);
				GregorianCalendar monthAgo = new GregorianCalendar();
				monthAgo.add(Calendar.MONTH, -1);
				pstmt.setTimestamp(1, new java.sql.Timestamp( monthAgo.getTimeInMillis()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					java.sql.Timestamp ts = rset.getTimestamp(2);
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(ts.getTime());
					Double val = new Double(rset.getDouble(3));
					timestampToValueMap.put(tempCal.getTime(), val );
					CTILogger.info(tempCal.getTime() + " " + val);
				}
			}
		}
			
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			SqlUtils.close(rset, pstmt, conn );
		}
		return timestampToValueMap;
	}
	
	public static int getPointID(int deviceID, int pointOffset, int pointType)
	{
		return DaoFactory.getPointDao().getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType);
	}
	
	public Date[] getPrevDateArray(int pointID_) 
	{
		java.util.TreeMap tempMap = getPrevMonthTSToValueMap(pointID_);
		if( tempMap != null)
		{
			java.util.Set keySet = tempMap.keySet();
			java.util.Date[] keyArray = new java.util.Date[keySet.size()];
			keySet.toArray(keyArray);
			return keyArray;
		}
		return null;
	}
	
	public boolean isEnergyPoint(int pointID_)
	{
		LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointID_);
		if( lp.getPointOffset() == PointTypes.PT_OFFSET_TOTAL_KWH &&
				lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT)
			return true;
		return false;
	}
	/**
	 * @return
	 */
	public Date getLPDate()
	{
		if( lpDate == null)
		{
			lpDate = ServletUtil.getToday();
		}
		return lpDate;
	}

	/**
	 * @param date
	 */
	public void setLPDate(Date date)
	{
		if( lpDate.compareTo(date) != 0)
			clearCurrentData();
		lpDate = date;
	}

	/**
	 * @return
	 */
	public String getLPDateStr()
	{
		return lpDateStr;
	}

	/**
	 * @param string
	 */
	public void setLPDateStr(String string)
	{
		lpDateStr = string;
		setLPDate(ServletUtil.parseDateStringLiberally(lpDateStr));		
	}

	/**
	 * Returns a vector of LiteRawPointHistory values for pointID, using the bean's StateDate through StartDate +1  
	 */
	public void loadRPHData()
	{
		clearCurrentData();	//clear out old data
	    
		if (getPointID() == PointTypes.SYS_PID_SYSTEM)
			return;

		/** 
		 * The query is setup to include the 00:00 time of the begining date, since PIL does not work in the hour ending format when retreiving intervals.  SN 20050504
		 */
		StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, VALUE, TIMESTAMP FROM RAWPOINTHISTORY " +
						" WHERE TIMESTAMP >= ? AND TIMESTAMP <= ? " +
						" AND POINTID = " + getPointID() + 
						 
						" ORDER BY POINTID, TIMESTAMP");
		CTILogger.info(sql.toString());	
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getLPDate().getTime() ));
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(getLPDate());
				cal.add(Calendar.DATE, 1);
				pstmt.setTimestamp(2, new java.sql.Timestamp( cal.getTimeInMillis()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					int ptID = rset.getInt(1);
					double value = rset.getDouble(2);
					Timestamp ts = rset.getTimestamp(3);
					GregorianCalendar tsCal = new GregorianCalendar();
					tsCal.setTimeInMillis(ts.getTime());
					LiteRawPointHistory lrph = new LiteRawPointHistory(ptID);
					lrph.setValue(value);
					lrph.setTimeStamp(tsCal.getTimeInMillis());
					getCurrentRPHVector().add(lrph);
				}
				getCurrentRPHVector().trimToSize();
				CTILogger.info("ResultSize: " + getCurrentRPHVector().size()+ " : START DATE > " + getLPDate() + "  -  STOP DATE <= " + cal.getTime());
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
				if( rset != null)
					rset.close();
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * @return
	 */
	public int getPointID()
	{
		return pointID;
	}

	/**
	 * @param i
	 */
	public void setPointID(int i)
	{
		if( pointID != i)
			clearCurrentData();
		pointID = i;
	}

	/**
	 * Returns the disconnect address if exists, returns -1 if not exists.
	 * @param deviceID
	 * @return
	 */
	public int getMCT410DisconnectAddress(int deviceID)
	{
		Integer discAddress = (Integer)getDeviceIDToDiscAddressMap().get(new Integer(deviceID));
		if (discAddress == null)
		{
			String sql = "SELECT DEVICEID, DISCONNECTADDRESS FROM DEVICEMCT400SERIES " +
							" WHERE DEVICEID = " + deviceID; 
			CTILogger.info(sql);	
			
			java.sql.Connection conn = null;
			java.sql.PreparedStatement pstmt = null;
			java.sql.ResultSet rset = null;
	
			try
			{
				conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
				int returnAddress = -1;
	
				if( conn == null )
				{
					CTILogger.error(getClass() + ":  Error getting database connection.");
					return -1;
				}
				else
				{
					pstmt = conn.prepareStatement(sql.toString());
					rset = pstmt.executeQuery();
					while( rset.next())
					{
						returnAddress = rset.getInt(2);
						break;
					}
				}
				//If none found, then -1 is entered, else the found result is entered.
				discAddress = new Integer(returnAddress);
				getDeviceIDToDiscAddressMap().put(new Integer(deviceID), discAddress);
			}
				
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if( rset != null)
						rset.close();
					if( pstmt != null )
						pstmt.close();
					if( conn != null )
						conn.close();
				}
				catch( java.sql.SQLException e )
				{
					e.printStackTrace();
				}
			}
		}
		
		return discAddress.intValue();
	}
	/**
	 * @return
	 */
	public Map getDeviceIDToDiscAddressMap()
	{
		return deviceIDToDiscAddressMap;
	}

	/**
	 * @return Returns the currentRPHVector.
	 */
	public Vector getCurrentRPHVector()
	{
		return currentRPHVector;
	}
	/**
	 * @param currentRPHVector The currentRPHVector to set.
	 */
	public void setCurrentRPHVector(Vector currentRPHVector)
	{
		this.currentRPHVector = currentRPHVector;
	}
    
	private void clearCurrentData()
	{
		getCurrentRPHVector().clear();
	}
    

	public final LiteYukonPAObject[] getValidRoutes()
	{
		return DaoFactory.getPaoDao().getRoutesByType(validRouteTypes);
	}

    public int getUserID()
    {
        return userID;
}
    public void setUserID(int userID)
    {
        this.userID = userID;
        setLiteUser(DaoFactory.getYukonUserDao().getLiteYukonUser(userID));
    }
    
    public List<LiteYukonPAObject> getLiteDevices(){
        List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
        
        PaoDao paoDao = DaoFactory.getPaoDao();
        for(Object deviceId : this.getDeviceIDs()){
            deviceList.add(paoDao.getLiteYukonPAO((Integer)deviceId));
        }
        
        return deviceList;
    }
    
    public String getFormattedTimestamp(PointData pointData, String defaultValue) { //int deviceID, int pointOffset, int pointType){
	    if( pointData != null)
	    	return dateTimeFormat.format(pointData.getPointDataTimeStamp()); 
	    else
	    	return defaultValue; 
    }
    
    public String getFormattedValue(PointData pointData, String format, String defaultValue) {
    	try {
    		if( pointData != null){
   		  		java.text.DecimalFormat df = new java.text.DecimalFormat(format);
   		  		LitePointUnit lpu = DaoFactory.getPointDao().getPointUnit(pointData.getId());
   		  		df.setMaximumFractionDigits(lpu.getDecimalPlaces());
   		  		df.setMinimumFractionDigits(lpu.getDecimalPlaces());
   		  		return df.format(pointData.getValue());
    		}
	  	} catch (NotFoundException nfe) {
	  		//We accept that we might not get the LitePointUnit.
   		}
	  	return defaultValue;
    }

    public String getFormattedStr(PointData pointData, String defaultValue) {
		if( pointData != null)
	  		return pointData.getStr();
	  	return defaultValue;
    }

    public int getPeakProfileDays() {
        return peakProfileDays;
    }

    public void setPeakProfileDays(int peakProfileDays) {
        this.peakProfileDays = peakProfileDays;
    }
    
    
}