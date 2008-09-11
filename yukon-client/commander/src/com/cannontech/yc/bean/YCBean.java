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
import java.util.List;
import java.util.Map;
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
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.point.PointTypes;
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
    List<LiteYukonPAObject> deviceHistory = new ArrayList<LiteYukonPAObject>();
    
    private java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("MM/dd/yy HH:mm");

    //Contains <String>serialType to <Vector<String>> serialNumbers
	private HashMap<String, Vector<String>> serialTypeToNumberMap = null;

	private int userID = 0;
	
	/** Contains String(name from returnMessage parse), Object(PointData, made up one though) values */
	private Map<String, PointData> returnNameToRecentPDMap = null;

	/** A Parameter for LPDate that a "bean" understands" */
	private String lpDateStr = "";
	
	/** A Date for LP query */
	private Date lpDate = null;
    
    private int peakProfileDays = 5;
	
	/** A pointID for selecting Data (historical RPH mainly)*/
	private int pointID = PointTypes.SYS_PID_SYSTEM;
	
	/** A vector of RPH data, for the currently selected point 
	 * This vector of data should only live as long as a point and timestamp 
	 * selected do not change, so clear it out on pointID or timestamp changes*/
	private Vector<LiteRawPointHistory> currentRPHVector = new Vector<LiteRawPointHistory>();

	/** Valid route types for serial commands to be sent out on */
	private int [] validRouteTypes = new int[]{
		RouteTypes.ROUTE_CCU,
		RouteTypes.ROUTE_MACRO
		};	

	public YCBean()
	{
		super();
	}

	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialType_, String serialNumber_) {
	    
		if( serialType_ != null && serialNumber_ != null) {
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
	public void setSerialNumber(String serialNumber_) {
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
			if(returnMsg.getVector().size() > 0 ) {
			    
				for (int i = 0; i < returnMsg.getVector().size(); i++) {
				    
					Object o = returnMsg.getVector().elementAt(i);
					
					if (o instanceof PointData) {
						PointData point = (PointData) o;
						
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
			//else	//Tried to do an else here because there is no reason to do both,
					//But if the ReturnMessage contains more the one point's data, but only one of the points 
					//  is in the returnMsg.getVector(), we have no real way of knowing that we need to parse the 
					// returnMsg.resultString.  Therefore, we do them both.
			{
				int multLineIndex = 0;
				int beginIndex = 0;
				//remove "(point not in DB)", need to escape the special characters ) and (
                resultStr = resultStr.replaceAll("\\(point not in DB\\)", "");
				
				String tempResult = resultStr;
				//Loop through the resultStr in case it is a multi-line return with more than one point info.
				do {
					multLineIndex = resultStr.indexOf('\n', beginIndex);					
					if(multLineIndex > 0) {
						tempResult = resultStr.substring(beginIndex, multLineIndex);
						beginIndex = multLineIndex+1;
						
					} else {
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
						
					}
				}
				while (multLineIndex > 0);
			}
		}
		super.messageReceived(e);		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {
		CTILogger.info("YCBean value bound to session.");
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
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
	public Map<String, PointData> getReturnNameToRecentPDMap() {
		if( returnNameToRecentPDMap == null) {
			returnNameToRecentPDMap = new HashMap<String, PointData>(5);
		}
		return returnNameToRecentPDMap;
	}

	/**
	 * Return PointData value from DynamicDataSource
	 * @param pointID
	 * @return
	 */
	private PointValueHolder getPointData(int pointID) {
		try {
	        DynamicDataSource dds = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
	        PointValueHolder pointValueHolder =  dds.getPointValue(pointID);
	        return pointValueHolder;
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
	public PointValueHolder getRecentPointData(int deviceID, int pointOffset, int pointType) {

	    PointValueHolder pd = null;
	    /*
		 * For LP Channel data (DataCollection and PeakReport) we do NOT want what is stored in the recentReadMap,
		 * since it is only storing the last read instead of the 6/12 intervals and the peak report, therefore we use 
		 * a different pointType instead of the Demand Accumulator type to find the points in the map by Name.  This
		 * helps with the fact that there are two types of data stored for one pointoffset/pointType key.  The below 
		 * call for pointID_ will actually return an invalid value and then allow us into the if/else to find data by name.
		 * SN 20050505
		 */
		int pointID_ = DaoFactory.getPointDao().getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType);
        if (pointID_ != PointTypes.SYS_PID_SYSTEM)
            pd = getPointData(pointID);
        
		if(pd == null) {
		    //Removed all other cases except for PeakChannel and DataChannel checks.  All others are presumed to have 
		    // points defined otherwise won't have them in our records.  Only the CommandInv.jsp still uses these in 
		    // its imported pages.  Most has been deprecated with introduction of widgets.  SN 20080421
			if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND && pointType == PointTypes.LP_PEAK_REPORT) {
				pd = getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"PeakChannel 1");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && pointType == PointTypes.LP_PEAK_REPORT) {
				pd = getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"PeakChannel 4");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND && pointType == PointTypes.LP_ARCHIVED_DATA) {
				pd = getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"DataChannel 1");
			}
			else if( pointOffset == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && pointType == PointTypes.LP_ARCHIVED_DATA) {
				pd = getReturnNameToRecentPDMap().get(String.valueOf(deviceID)+"DataChannel 4");
			}
		}
		return pd;
	}
	
	/**
	 * Returns a vector of serialNumbers from the serialTypeToNumbersMap with key value of serialType_
	 * @return
	 */
	public Vector<String> getSerialNumbers(String serialType_) {
		return getSerialTypeToNumberMap().get(serialType_);
	}

	private HashMap<String, Vector<String>> getSerialTypeToNumberMap() {
		if( serialTypeToNumberMap == null)
			serialTypeToNumberMap = new HashMap<String, Vector<String>>();
	
		return serialTypeToNumberMap;
	}	
	
	public void setDeviceType(int devID) {
		LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(devID);
		deviceType = PAOGroups.getPAOTypeString(lPao.getType());
		CTILogger.debug(" DEVICE TYPE for command lookup: " + deviceType);
		setLiteDeviceTypeCommandsVector(DaoFactory.getCommandDao().getAllDevTypeCommands(deviceType));
	}

	public static int getPointID(int deviceID, int pointOffset, int pointType) {
		return DaoFactory.getPointDao().getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType);
	}

	public Date getLPDate() {
		if( lpDate == null) {
			lpDate = ServletUtil.getToday();
		}
		return lpDate;
	}

	public void setLPDate(Date date) {
		if( lpDate.compareTo(date) != 0)
			clearCurrentData();
		lpDate = date;
	}

	public String getLPDateStr() {
		return lpDateStr;
	}

	public void setLPDateStr(String string) {
		lpDateStr = string;
		setLPDate(ServletUtil.parseDateStringLiberally(lpDateStr));		
	}

	/**
	 * Returns a vector of LiteRawPointHistory values for pointID, using the bean's StateDate through StartDate +1  
	 */
	public void loadRPHData() {
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

		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			} else {
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getLPDate().getTime() ));
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(getLPDate());
				cal.add(Calendar.DATE, 1);
				pstmt.setTimestamp(2, new java.sql.Timestamp( cal.getTimeInMillis()));
				rset = pstmt.executeQuery();
				
				while( rset.next()) {
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
			
		} catch( java.sql.SQLException e ) {
			e.printStackTrace();
		} finally {
		    SqlUtils.close(rset, pstmt, conn );
		}
	}

	public int getPointID() {
		return pointID;
	}

	public void setPointID(int i) {
		if( pointID != i)
			clearCurrentData();
		pointID = i;
	}

	public Vector<LiteRawPointHistory> getCurrentRPHVector() {
		return currentRPHVector;
	}

	public void setCurrentRPHVector(Vector<LiteRawPointHistory> currentRPHVector) {
		this.currentRPHVector = currentRPHVector;
	}
    
	private void clearCurrentData() {
		getCurrentRPHVector().clear();
	}

	public final LiteYukonPAObject[] getValidRoutes() {
		return DaoFactory.getPaoDao().getRoutesByType(validRouteTypes);
	}

    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
        setLiteUser(DaoFactory.getYukonUserDao().getLiteYukonUser(userID));
    }
    
    public String getFormattedTimestamp(PointValueHolder pointValueHolder, String defaultValue) { //int deviceID, int pointOffset, int pointType){
	    if( pointValueHolder != null)
	    	return dateTimeFormat.format(pointValueHolder.getPointDataTimeStamp()); 
	    else
	    	return defaultValue; 
    }
    
    public String getFormattedValue(PointValueHolder pointValueHolder, String format, String defaultValue) {
    	try {
    		if( pointValueHolder != null){
   		  		java.text.DecimalFormat df = new java.text.DecimalFormat(format);
   		  		LitePointUnit lpu = DaoFactory.getPointDao().getPointUnit(pointValueHolder.getId());
   		  		df.setMaximumFractionDigits(lpu.getDecimalPlaces());
   		  		df.setMinimumFractionDigits(lpu.getDecimalPlaces());
   		  		return df.format(pointValueHolder.getValue());
    		}
	  	} catch (NotFoundException nfe) {
	  		//We accept that we might not get the LitePointUnit.
   		}
	  	return defaultValue;
    }
    
    // Adds an element to the previously viewed devices
    private void addToDeviceHistory(LiteYukonPAObject liteYukonPao){
        if(liteYukonPao != null){
            if(!this.deviceHistory.contains(liteYukonPao)){ 
                this.deviceHistory.add(liteYukonPao);
            }
        }
        
        // Remove data from other devices
        try{
            setCommandString("");
        } catch (PaoAuthorizationException e){}
        
        clearResultText();
        clearErrorMsg();
        clearCurrentData();
    }

    // Gets the list of previously added devices
    public List<LiteYukonPAObject> getDeviceHistory(){
        return this.deviceHistory;
    }
    
    /**
     * @param liteYukonPao
     */
    public void setLiteYukonPao(LiteYukonPAObject liteYukonPao){
        super.setLiteYukonPao(liteYukonPao);
        addToDeviceHistory(liteYukonPao);
    }
    
    public int getPeakProfileDays() {
        return peakProfileDays;
    }

    public void setPeakProfileDays(int peakProfileDays) {
        this.peakProfileDays = peakProfileDays;
    }
}