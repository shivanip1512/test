package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:24:43 PM)
 * @author: 
 */
import java.sql.SQLException;
import com.cannontech.database.model.ModelFactory;

public class YC
{
	public final String ALT_SERIALNUMBER_FILENAME = "VersacomSerial";	//serial number file name
	public final String SERIALNUMBER_FILENAME = "LCRSerial";	//serial number file name
	public final String DEFAULT_FILENAME = "default";	//serial number file name
	public final String COLLECTION_GROUP_FILENAME = "CollectionGroup";	//serial number file name
	private String commandFileName = "commandFile";	//current command file, init only to NOT have null value
	
	private String command;	//holds the current Command to execute
	private String serialNumber;	// currently selected serial number (from tree or box)
	private Object treeItem = null;
	private Object route = null;			//when serial number used, the current routeID
	private int    modelType = 0;
	
	private boolean DIRECTLY_SEND_COMMAND = false;	//allows a shortcut send method, changes with CGPCheckBox.

	private volatile int currentUserMessageID = 1;
	public static volatile int sendMore = 0;

	public  static com.cannontech.message.porter.message.Request porterRequest = null;

	public Object[] allRoutes = null;

	private int loopType = 0;
	public static int NOLOOP = 0;	//loop not parsed
	public static int LOOP = 1;	//loop alone parsed
	public static int LOOPNUM = 2;//loop for some num of times
	public static int LOOPLOCATE = 3;//loop locate parsed
	public static int LOOPLOCATE_ROUTE = 4;//loop locate route parsed
	
	private com.cannontech.message.porter.ClientConnection connToPorter = null;
	private com.cannontech.common.util.KeysAndValuesFile keysAndValuesFile = null;

	private YCDefaults ycDefaults = null;
	/**
	 * YC constructor comment.
	 */
	public YC() 
	{
		super();
		getConnToPorter();
		ycDefaults = new YCDefaults();
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/25/2002 5:03:35 PM)
	 */
	public void executeCommand()
	{
		if ( !isDirectSend())	//going to skip the check since user knows what's going on.
		{
			String subCommand = null;
	
			if (getTreeItem() == null)
				subCommand = null;
			else 
				subCommand = (substituteCommand( getCommand() ));
				
			if( subCommand == null )
			{
				// the command entered is not valid or was not found during command lookup
				logCommand(" ** Warning: The command was not recognized **");
				return;
			}
			else
				setCommand( subCommand );
		}
		//---------------------------------------------------------------------------------------
		try
		{
			if ( isDirectSend() )
			{
				porterRequest = new com.cannontech.message.porter.message.Request( 0, getCommand(), currentUserMessageID );
				porterRequest.setPriority(getCommandPriority());
				writeNewRequestToPorter( porterRequest );
			}
				
			else if( getTreeItem() != null )
			{
				// Stops the requests from continuing (a.k.a. kills the "loop" command).
				sendMore = 0;
					
				// Device item selected (including other models)
				if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
				{
					com.cannontech.database.data.lite.LiteYukonPAObject lpao = (com.cannontech.database.data.lite.LiteYukonPAObject) getTreeItem();
					handleDevice ( lpao.getYukonID(), lpao.getType() );
				}
				// Meter number item in tree selected.
				else if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteDeviceMeterNumber )
				{
					com.cannontech.database.data.lite.LiteDeviceMeterNumber ldmn = (com.cannontech.database.data.lite.LiteDeviceMeterNumber) getTreeItem();
					com.cannontech.database.data.lite.LiteYukonPAObject litePao = com.cannontech.database.cache.functions.DeviceFuncs.getLiteDevice(ldmn.getLiteID());
					handleDevice (ldmn.getDeviceID(), litePao.getType());
				}		
				// Serial Number item in tree selected.
				else if ( getModelType() == ModelFactory.EDITABLELCRSERIAL)
				{
					handleSerialNumber();
				}
				// TestCollectionGroup is selected.
				else if ( getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
				{
					Integer [] deviceMeterGroupIds = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceIDs_TestCollectionGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
					for ( int i = 0; i < deviceMeterGroupIds.length; i++)
					{
						com.cannontech.database.data.lite.LiteYukonPAObject lpao = com.cannontech.database.cache.functions.DeviceFuncs.getLiteDevice(deviceMeterGroupIds [i].intValue());
						handleDevice ( lpao.getYukonID(), lpao.getType() );
					}
				}
				// Collectiongroup is selected.
				else if ( getModelType() == ModelFactory.COLLECTIONGROUP )
				{
					Integer [] deviceMeterGroupIds = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceIDs_CollectionGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
					for ( int i = 0; i < deviceMeterGroupIds.length; i++)
					{
						com.cannontech.database.data.lite.LiteYukonPAObject lpao = com.cannontech.database.cache.functions.DeviceFuncs.getLiteDevice(deviceMeterGroupIds [i].intValue());
						handleDevice ( lpao.getYukonID(), lpao.getType() );
					}
				}
				else
				{
					com.cannontech.clientutils.CTILogger.info(getModelType() + " - New type needs to be handled");
				}
			}
			else
			{
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	/**
	 * Returns the allRoutes.
	 * @return Object[]
	 */
	public Object[] getAllRoutes()
	{
		return allRoutes;
	}
	/**
	 * Returns the command.
	 * @return java.lang.String
	 */
	public String getCommand()
	{
		return command;
	}
	/**
	 * Returns the commandFileName.
	 * @return java.lang.String
	 */
	public String getCommandFileName()
	{
		if ( commandFileName == null )
			commandFileName = "commandFile";	//set to something so it's not null
		
		return commandFileName;
	}
	
	public String getCommandFileDirectory()
	{
		return getYCDefaults().getCommandFileDirectory();
	}
	public int getCommandPriority()
	{
		return getYCDefaults().getCommandPriority();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 2:02:30 PM)
	 * @return com.cannontech.message.porter.ClientConnection
	 */
	public com.cannontech.message.porter.ClientConnection getConnToPorter()
	{
		if( connToPorter == null )
		{
			String host = "127.0.0.1";
			int port = 1510;
			try
			{
				host = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
	                  com.cannontech.common.util.CtiProperties.KEY_PORTER_MACHINE, 
	                  "127.0.0.1");
	            
				port = (new Integer( com.cannontech.common.util.CtiProperties.getInstance().getProperty(
	                  com.cannontech.common.util.CtiProperties.KEY_PORTER_PORT, 
	                  "1510"))).intValue();
			}
			catch( Exception e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
	
			connToPorter = new com.cannontech.message.porter.ClientConnection();
			connToPorter.setHost(host);
			connToPorter.setPort(port);
			connToPorter.setAutoReconnect(true);
			
			try 
			{
				connToPorter.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
	
		return connToPorter;	
	}	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/27/2002 4:42:57 PM)
	 * @return int
	 */
	public int getLoopType()
	{
		return loopType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 2:13:06 PM)
	 * @return int
	 */
	public int getModelType()
	{
		return modelType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/2002 2:57:37 PM)
	 * @return java.lang.String
	 */
	public String getQueueCommandString()
	{
		if( getYCDefaults().getQueueExecuteCommand())
			return "";
		else 
			return " noqueue";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 1:53:53 PM)
	 * @return java.lang.Object
	 */
	public Object getRoute()
	{
		return route;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 4:16:48 PM)
	 * @return String
	 */
	public String getSerialNumber()
	{
		return serialNumber;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 2:19:40 PM)
	 * @return java.lang.Object
	 */
	public Object getTreeItem()
	{
		return treeItem;
	}
	public YCDefaults getYCDefaults()
	{
		if( ycDefaults == null)
			ycDefaults = new YCDefaults();
		return ycDefaults;
	}
	/**
	 * Insert the method's description here.
	 * Gets the device ID of the object, and selected serial number (which should "never" happen)
	 * Checks for the command string "loop" to exist.
	 * Creates the message.Request to send to porter.
	 * Saves the message.Request if "loop" was found in the string so the Request can be resubmitted.
	 * Write's the Request to the porter connection.
	 * Creation date: (9/16/2001 5:32:40 PM)
	 */
	public void handleDevice(int deviceID, int type)
	{
			
		if( deviceID < 0 )	//no device selected
		{
			if ( getSerialNumber() == null)	// NO serial Number Selected
			{
				logCommand(" *** Warning: Please select a Device or Serial Number ***");
				return;
			}
		}
	
		setLoopType( parseLoopCommand() );
		String command = getCommand();
	
		if ( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type)
			|| com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(type))
			setCommand( command + getQueueCommandString());
	
		porterRequest = new com.cannontech.message.porter.message.Request( deviceID, getCommand(), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
	
		if (getLoopType() == LOOPLOCATE)
	 	{
			if( getAllRoutes() != null && getAllRoutes()[sendMore] instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
			{
				com.cannontech.database.data.lite.LiteYukonPAObject rt = (com.cannontech.database.data.lite.LiteYukonPAObject) getAllRoutes()[sendMore];
				while( rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO && sendMore > 0)
				{
					sendMore--;
					rt = (com.cannontech.database.data.lite.LiteYukonPAObject) getAllRoutes()[sendMore];
				}
	
				if( rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO)
					return;
	
				porterRequest.setRouteID(rt.getYukonID());
			}
	 	}
		else if( getLoopType() == LOOPLOCATE_ROUTE )
		{
			if( getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
			{
				porterRequest.setRouteID( ((com.cannontech.database.data.lite.LiteYukonPAObject)getRoute()).getYukonID());
			}
		}
		writeNewRequestToPorter(porterRequest);
	}
	/**
	 * Insert the method's description here.
	 * Looks for the string "serial" in the command.
	 * If the string is not found, tack it on to the string along with the currentSelectedSerialNumber.
	 * Create a message.Request to send to porter.
	 * Set the route selected for the message.Request.
	 * Write Request to the connection to porter.
	 * Creation date: (9/16/2001 5:32:40 PM)
	 */
	public void handleSerialNumber()
	{
		int deviceID = -1;						//defaulted to the system device
			 
		int index = getCommand().indexOf("serial");
		
		if( index < 0 )	// serial not in command string = -1
			setCommand( getCommand() + " serial " + getTreeItem() );
		//else	// Do nothing, assume the command is correct
	
		setLoopType( parseLoopCommand() );
		
		porterRequest = new com.cannontech.message.porter.message.Request( deviceID, getCommand(), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
	
		// Get routeID from comboBox / set it in the request
		if( getRoute() != null && getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject r = (com.cannontech.database.data.lite.LiteYukonPAObject) getRoute();
			porterRequest.setRouteID(r.getYukonID());
		}
	
		if( deviceID < 0 )	//no device selected
		{
			if ( getSerialNumber() == null)	// NO serial Number Selected
			{
				logCommand(" *** Warning: Please select a Device or Serial Number ***");
				return;
			}
		}
	
		writeNewRequestToPorter( porterRequest );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 12:21:26 PM)
	 * @return boolean
	 */
	public boolean isDirectSend() 
	{
		return DIRECTLY_SEND_COMMAND;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 11:58:03 AM)
	 * @param logString java.lang.String
	 */
	public void logCommand(String logString)
	{
		YukonCommander.getCommandLogPanel().addLogElement(logString);
	}
	/**
	 * Insert the method's description here.
	 * Search for the string "loop" in the command.
	 * If loop is found, search for any value in the command (following "loop").
	 * Set sendMore to the number found after loop (which tells how many times loop wishes to perform).
	 * Return boolean: True if "loop" found, False if not found.
	 * Creation date: (9/12/2001 3:07:17 PM)
	 */
	public int parseLoopCommand()
	{
		String tempCommand = getCommand().toLowerCase();
		String valueSubstring = null;
			
		int loopIndex = tempCommand.indexOf("loop");
	
		if ( loopIndex >= 0)	//a loop exists
		{	
			for (int i = tempCommand.indexOf("loop") + 4; i < tempCommand.length(); i++)
			{
				if ( tempCommand.charAt(i) != ' ' && tempCommand.charAt(i) != '\t')	//skip whitespaces
				{
					valueSubstring = tempCommand.substring( i );
					break;
				}
			}
			
			if (valueSubstring != null)
			{
				if( valueSubstring.startsWith("locater"))	//parse out locateroute
				{
					synchronized(YC.class)
					{
						setCommand("loop");
					}
					return LOOPLOCATE_ROUTE;
				}
				else if( valueSubstring.startsWith("loc"))	//parse out locate
				{
					synchronized (YC.class)
					{
						setCommand("loop");
						//loop through each route
						sendMore = getAllRoutes().length - 1;
					}
					return LOOPLOCATE;
				}
				else
				{
					Integer value = null;
					try
					{
						value = new Integer(valueSubstring );	//assume it is an integer.
					}
					catch(NumberFormatException nfe)
					{
						value = new Integer(1);
					}
					synchronized (YC.class) 
					{
						setCommand("loop");
						//Subtract one because 0 is the last occurance, not 1.  (Ex. 0-4 not 1-5)
						sendMore = value.intValue() - 1;
					}
					return LOOPNUM;
				}
			}
			return LOOP;
		}
		return NOLOOP;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/27/2002 3:38:15 PM)
	 * @param allRoutesVector java.util.Vector
	 */
	public void setAllRoutes(Object[] allRoutesArray) 
	{
		allRoutes = new Object[allRoutesArray.length];
		for ( int i = 0; i < allRoutesArray.length; i++)
		{
			allRoutes[i] = allRoutesArray[i];
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/11/2001 12:07:43 PM)
	 * @param newCommand java.lang.String
	 */
	public void setCommand(String newCommand)
	{
		command = newCommand;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/11/2001 2:05:16 PM)
	 * @param newCommandFile java.lang.String
	 */
//	public void setCommandFileName(String newCommandFileName)
//	{
//		commandFileName = newCommandFileName;
//	}

	public void setCommandFileName(Object item)
	{
		String className = DEFAULT_FILENAME;
	
		if (item.toString() == SERIALNUMBER_FILENAME  ||item.toString() == ALT_SERIALNUMBER_FILENAME)	//get serial number class name (constant var)
		{
			className = (String) item;
		}
		else if (item.toString() == DEFAULT_FILENAME )
		{
			className = (String) item;
		}
		else if( item instanceof com.cannontech.database.data.device.DeviceBase)	//		ModelFactory.DEVICE,MCTBROADCAST,LMGROUPS,CAPBANKCONTROLLER
		{
			className = ((com.cannontech.database.data.device.DeviceBase)item).getPAOType();
		}
		else if(item instanceof com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)//		ModelFactory.DEVICE_METERNUMBER,		
		{
			int devID = ((com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase)item).getDeviceMeterGroup().getDeviceID().intValue();
			com.cannontech.database.data.lite.LiteYukonPAObject litePao = com.cannontech.database.cache.functions.DeviceFuncs.getLiteDevice(devID);
			className = com.cannontech.database.data.pao.PAOGroups.getDeviceTypeString(litePao.getType());
		}		
		else 
		{
			com.cannontech.clientutils.CTILogger.debug("FILENAME: undefined. Item instance of " + item.getClass());
			com.cannontech.clientutils.CTILogger.debug(" THIS IS REALLY A BAD CATCH ALL HUH");
		}
		commandFileName = className + ".txt";
		com.cannontech.clientutils.CTILogger.info(" COMMAND FILE: " + getCommandFileDirectory()+ commandFileName);
		keysAndValuesFile = new com.cannontech.common.util.KeysAndValuesFile(getCommandFileDirectory(), commandFileName);
	}
			
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 2:02:49 PM)
	 * @param connection com.cannontech.message.porter.ClientConnection
	 */
	public void setConnToPorter(com.cannontech.message.porter.ClientConnection connection)
	{
		connToPorter = connection;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 12:20:08 PM)
	 * @param directlySend boolean
	 */
	public void setDirectSend(boolean directlySend) 
	{
		DIRECTLY_SEND_COMMAND = directlySend;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/27/2002 4:42:38 PM)
	 * @param newLoopType int
	 */
	public void setLoopType(int newLoopType) 
	{
		loopType = newLoopType;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 2:12:46 PM)
	 * @param typeSelected int
	 */
	public void setModelType(int typeSelected)
	{
		modelType = typeSelected;	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 1:48:11 PM)
	 * @param newRoute java.lang.Object
	 */
	public void setRoute(Object newRoute) 
	{
		route = newRoute;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 4:17:54 PM)
	 * @param newSerialNumber java.lang.String
	 */
	public void setSerialNumber(String newSerialNumber)
	{
		if( newSerialNumber == null)
			serialNumber = null;
		else 
			serialNumber = newSerialNumber.trim();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 2:21:41 PM)
	 * @param selectedTreeItem java.lang.Object
	 */
	public void setTreeItem(Object newTreeItem)
	{
		treeItem = newTreeItem;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/21/2002 4:16:33 PM)
	 * @param newDefaults com.cannontech.yc.gui.YCDefaults
	 */
	public void setYCDefaults(YCDefaults newDefaults)
	{
		ycDefaults = newDefaults;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/10/2001 3:15:58 PM)
	 */
	public void stop()
	{
		currentUserMessageID++;
		sendMore = -1;		//Cancels multiple command processing done by YC
	}
	/**
	 * This method attempts to substitute a command for the typed in command
	 * based on the type of device/point selected.
	 * Applicable when the user may actually type in the command KEY value instead of the actual VALUE.
	 */
	public String substituteCommand(String command)
	{
		if( getKeysAndValues() != null)
		{
			String lowerCommand = command.toLowerCase().trim();
		
			//try to match the command to a key in cpf
			for (int i = 0; i < getKeysAndValues().getKeys().length; i++)
			{
				String lowerKey = getKeysAndValues().getKeys()[i].toLowerCase().trim();
		
				if (lowerKey.equals(lowerCommand))
					return getKeysAndValues().getValues()[i];
			}
		
			for (int i = 0; i < getKeysAndValues().getValues().length; i++)
			{
				String lowerKey = getKeysAndValues().getValues()[i].toLowerCase().trim();
		
				if (lowerKey.equals(lowerCommand))
					return getKeysAndValues().getValues()[i];
			}
		}
	
		return command; //default, return whatever they typed in on the command line
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 3:52:17 PM)
	 * @param request com.cannontech.message.porter.message.Request
	 */
	public void writeNewRequestToPorter(com.cannontech.message.porter.message.Request request)
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM d HH:mm:ss a z");					
		long timer = (System.currentTimeMillis());
			
		logCommand("[" + format.format(new java.util.Date(timer)) 
			+ "] - {"+ currentUserMessageID + "} Command Sent to Device \'"+ getTreeItem() + "\'  -  \'" + getCommand() + "\'");
		if( getConnToPorter() != null )
		{
			if( getConnToPorter().isValid())
			{
				getConnToPorter().write( request );
				currentUserMessageID++;
			}
			else
			{
				com.cannontech.clientutils.CTILogger.info("REQUEST NOT SENT: CONNECTION TO PORTER IS NOT VALID");
			}
		}
		else
		{
			com.cannontech.clientutils.CTILogger.info("REQUEST NOT SENT: CONNECTION TO PORTER IS NULL");
		}
			
	}
	/**
	 * Returns the porterRequest.
	 * @return com.cannontech.message.porter.message.Request
	 */
	public static com.cannontech.message.porter.message.Request getPorterRequest()
	{
		return porterRequest;
	}

	/**
	 * Returns the keysAndValuesFile.
	 * @return com.cannontech.common.util.KeysAndValuesFile
	 */
	public com.cannontech.common.util.KeysAndValuesFile getKeysAndValuesFile()
	{
		return keysAndValuesFile;
	}

	/**
	 * Returns the keysAndValues.
	 * @return com.cannontech.common.util.KeysAndValues
	 */
	public com.cannontech.common.util.KeysAndValues getKeysAndValues()
	{
		return getKeysAndValuesFile().getKeysAndValues();
	}
	
	public int getCurrentUserMessageID()
	{
		return currentUserMessageID;
	}
}
