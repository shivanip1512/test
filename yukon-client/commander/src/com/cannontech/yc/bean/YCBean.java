/*
 * Created on Oct 18, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yc.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;
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
	/** Singleton counter for pointRegistration messages sent to dispatch connection */
	private volatile int pointRegCounter = 0;
	
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	
	/** Contains Integer(pointID), Object(PointData) values */
	private Map pointIDToRPHMap = null;

	/** Contains Integer(pointID), Object(PointData) values */
	private Map pointIDToRecentReadMap = null;
	
	/** Contains String(name from returnMessage parse), Object(PointData, made up one though) values */
	private Map returnNameToRecentReadMap = null;

	private Vector pointIDs = new Vector();

	/**
	 * 
	 */
	public YCBean()
	{
		super();
		System.setProperty("cti.app.name", "Commander_Web");		
	}

	/**
	 * Sets the deviceID
	 * @param deviceID_ int
	 */
	public void setDeviceID(int deviceID_)
	{
		if( deviceID_ != getDeviceID())
		{
			super.setDeviceID(deviceID_);
			LitePoint [] litePoints = PAOFuncs.getLitePointsForPAObject(deviceID_);

			for(int i = 0; i < litePoints.length; i++)
			{
				if( !pointIDs.contains(new Integer(((LitePoint)litePoints[i]).getPointID())))
					pointIDs.addElement(new Integer(((LitePoint)litePoints[i]).getPointID()));
			}
			getClientConnection().write(getPointRegistration(pointIDs));
		}
	}

	/**
	 * Returns the connToDispatch.
	 * @return com.cannontech.message.util.ClientConnection
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public com.cannontech.message.util.ClientConnection getClientConnection()
	{
		if( connToDispatch == null )
		{
			String host = "127.0.0.1";
			int port = 1510;
			try
			{
				host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
	            
				port = Integer.parseInt(
							RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) );
			}
			catch( Exception e)
			{
				CTILogger.error( e.getMessage(), e );
			}
	
			connToDispatch = new ClientConnection();
			Registration reg = new Registration();
			reg.setAppName(CtiUtilities.getAppRegistration());
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK
	
			connToDispatch.setHost(host);
			connToDispatch.setPort(port);
			connToDispatch.setAutoReconnect(true);
			connToDispatch.setRegistrationMsg(reg);
			
			try
			{
				connToDispatch.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
		return connToDispatch;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();		
		if( in instanceof PointData )
		{           
			PointData point = (PointData) in;
			Integer x = new Integer(point.getId());
			getPointIDToRPHMap().put(x, point);
			pointRegCounter--;
			System.out.println(" POINT DATA " + point.getId() + "  " + point.getValue() + " " + point.getPointDataTimeStamp() + " " + getPointIDToRPHMap().size()); 
		}
		else if( in instanceof Return)
		{
			Return returnMsg = (Return)in;
			if( !getRequestMessageIDs().contains( new Long(returnMsg.getUserMessageID())))
				return;
			
			if(returnMsg.getVector().size() > 0 )
			{
				for (int i = 0; i < returnMsg.getVector().size(); i++)
				{
					Object o = returnMsg.getVector().elementAt(i);
					if (o instanceof PointData)
					{
						PointData point = (PointData) o;
						Integer x = new Integer(point.getId());
						getPointIDToRecentReadMap().put(x, point);
						System.out.println(" RETURN POINT DATA " + point.getId() + "  " + point.getValue() + " " + point.getPointDataTimeStamp() + " " + point.toString()); 
					}
					else
						System.out.println("NOTHING");
				}
			}
//			else
			{
				String resultStr = returnMsg.getResultString();
				int multLineIndex = 0;
				int beginIndex = 0;
				String tempResult = resultStr;
				//Loop through the resultStr in case it is a multiline return, containing more than one point info.
				do
				{
					multLineIndex = tempResult.indexOf('\n');					
					if(multLineIndex > 0)
					{
						tempResult = resultStr.substring(beginIndex, multLineIndex);
						beginIndex = multLineIndex+1;
						
					}
					else
					{
						tempResult = resultStr.substring(beginIndex);
					}
					if( tempResult.indexOf('=') > 0)
					{
						int equal = tempResult.indexOf('=');
						int slash = tempResult.indexOf('/')+1;
				
						//The String "ID" is the deviceID + the value after the slash and before the equal sign
						String id = String.valueOf(returnMsg.getDeviceID()) + tempResult.substring(slash, equal).trim();
						System.out.println("VALUE:"+id+":");
					
						//The Double "value" is the value after the equal sign to the end or to the next space character
						Double value = null;
						int at = tempResult.indexOf('@');
						int dashDash = tempResult.indexOf("--");
						if(at > 0)
							value = Double.valueOf(tempResult.substring(equal+1, at).trim());
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
							System.out.println(dateStr + ": TIME:"+timestamp+":");
						}
						PointData fakePtData = new PointData();
						fakePtData.setValue(value.doubleValue());
						fakePtData.setTimeStamp(timestamp);
						fakePtData.setTime(timestamp);
						fakePtData.setType(PointTypes.DEMAND_ACCUMULATOR_POINT);
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentReadMap().put(id, fakePtData);
						System.out.println("value IS:"+ value.doubleValue()+":");
					}
					else if( tempResult.indexOf(':') > 0)	//outage stuff
					{					
						int colon = tempResult.indexOf(':');
						int slash = tempResult.indexOf('/')+1;

						//The String "ID" is the deviceID + the value after the slash and before the equal sign
						String id = String.valueOf(returnMsg.getDeviceID()) + tempResult.substring(slash, colon).trim();
						System.out.println("VALUE:"+id+":");

						//The Timestamp is the value after the colon sign to the 'for' string
						Date date = null;
						//The duration is the value after 'for' string to the end
						Double value = null;						
						int forIdx = tempResult.indexOf("for")+3;
						if(forIdx > 0)
						{
							date = ServletUtil.parseDateStringLiberally(tempResult.substring(colon+1, forIdx).trim());
							
							String durStr = tempResult.substring(forIdx+1).trim();
							if(!durStr.equalsIgnoreCase("(unknown duration)"))
							{
								double HH = Long.valueOf(durStr.substring(0, 2)).doubleValue() * 3600d;
								double mm = Long.valueOf(durStr.substring(3, 5)).doubleValue() * 60d;
								double ss = Long.valueOf(durStr.substring(6, 8)).doubleValue();
								value = new Double(HH + mm + ss);
								System.out.println(tempResult.substring(colon+1, forIdx).trim()+ ": TIME:"+date+":");
							}
							else
							{
								value = new Double(-1);
							}
						}
						
						PointData fakePtData = new PointData();
						fakePtData.setStr(tempResult.substring(colon+1).trim());
						fakePtData.setValue(value.doubleValue());
						fakePtData.setTimeStamp(date);
						fakePtData.setTime(date);
						fakePtData.setType(PointTypes.DEMAND_ACCUMULATOR_POINT);
						//The key is deviceID+STRING parsed from return message
						getReturnNameToRecentReadMap().put(id, fakePtData);
						System.out.println("value IS:"+ value.doubleValue()+":");
						
					}
					
				}
				while (multLineIndex > 0);
			}
		}
		super.messageReceived(e);		
	}

	/**
	 * Vector pointIDs contains Integer values
	 * @param pointIDs
	 * @return
	 */
	public PointRegistration getPointRegistration( Vector pointIDs)
	{
		//Register for points
		PointRegistration pReg = new PointRegistration();	 		
		com.roguewave.tools.v2_0.Slist list = new com.roguewave.tools.v2_0.Slist();
		getClientConnection().getRegistrationMsg();

		if(pointIDs != null)		
		{
			for(int i = 0; i < pointIDs.size(); i++)
			{
				list.insert(new Long( ((Integer)pointIDs.get(i)).longValue() ));
				pointRegCounter++;
				CTILogger.info("REGISTERING FOR POINTID: " + ((Integer)pointIDs.get(i)).intValue());
			}	
			pReg.setPointList( list );
		}
		else
		{	// just register for everything
			pReg.setRegFlags( PointRegistration.REG_NOTHING);
		}
		
		return pReg;
	}

	/**
	 * @return
	 */
	public Map getPointIDToRPHMap()
	{
		if( pointIDToRPHMap == null)
		{
			pointIDToRPHMap = new HashMap(5);
		}
		return pointIDToRPHMap;
	}
	/**
	 * @return
	 */
	public Map getPointIDToRecentReadMap()
	{
		if( pointIDToRecentReadMap == null)
		{
			pointIDToRecentReadMap = new HashMap(5);
		}
		return pointIDToRecentReadMap;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0)
	{
		// TODO Auto-generated method stub
		CTILogger.info("***** Value Bound " + arg0.getValue().toString() + "*****");
		getClientConnection().addMessageListener(this);		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0)
	{
		// TODO Is removing the messageListener enough?
		CTILogger.info("***** Value UNBound " + arg0.getValue().toString() + "*****");
		getClientConnection().removeMessageListener(this);
	}
	/**
	 * @return
	 */
	public int getPointRegCounter()
	{
		return pointRegCounter;
	}
	/**
	 * @return
	 */
	public Map getReturnNameToRecentReadMap()
	{
		if( returnNameToRecentReadMap == null)
		{
			returnNameToRecentReadMap = new HashMap(5);
		}
		return returnNameToRecentReadMap;
	}

	public PointData getRPHPointData(int deviceID, int pointOffset, int pointType)
	{
		int pointID = PointFuncs.getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType);		
		PointData pd = (PointData)getPointIDToRPHMap().get(new Integer(pointID));
		return pd;		
	}
	
	public PointData getRecentPointData(int deviceID, int pointOffset, int pointType)
	{
		int pointID = PointFuncs.getPointIDByDeviceID_Offset_PointType(deviceID, pointOffset, pointType);
		
		PointData pd = (PointData)getPointIDToRecentReadMap().get(new Integer(pointID));
		if(pd == null)
		{
			//Try getting it from somewhere else?
			if( pointOffset == 20 && pointType == PointTypes.PULSE_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"powerfail count");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentReadMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)power.*"))
						{
							pd = (PointData)getReturnNameToRecentReadMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 1 && pointType == PointTypes.PULSE_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"kWh");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentReadMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)kyz.*"))
						{
							pd = (PointData)getReturnNameToRecentReadMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 1 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"kW");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentReadMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)demand.*"))
						{
							pd = (PointData)getReturnNameToRecentReadMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 11 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Peak Demand");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentReadMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)peak.*"))
						{
							pd = (PointData)getReturnNameToRecentReadMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 4 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Voltage");
				if( pd == null)	//if we still haven't found it... PointUndefinedInDatabase
				{				
					String key = "";
					Iterator iter = getReturnNameToRecentReadMap().keySet().iterator();
					while(iter.hasNext())
					{
						key = (String)iter.next();
						if( key.matches(String.valueOf(getDeviceID())+"(?i)volt.*"))
						{
							pd = (PointData)getReturnNameToRecentReadMap().get(key);
							break;
						}
					}
				}
			}
			else if( pointOffset == 15 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{				
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Min Voltage");
			}
			else if( pointOffset == 14 && pointType == PointTypes.DEMAND_ACCUMULATOR_POINT)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Max Voltage");
			}
			else if( pointType == PointTypes.OUTAGE_1)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 1");
			}
			else if( pointType == PointTypes.OUTAGE_2)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 2");
			}
			else if( pointType == PointTypes.OUTAGE_3)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 3");
			}
			else if( pointType == PointTypes.OUTAGE_4)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 4");
			}
			else if( pointType == PointTypes.OUTAGE_5)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 5");
			}
			else if( pointType == PointTypes.OUTAGE_6)
			{
				pd = (PointData)getReturnNameToRecentReadMap().get(String.valueOf(deviceID)+"Outage 6");
			}			
		}
		return pd;
	}
}
