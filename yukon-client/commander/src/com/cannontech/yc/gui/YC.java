package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:24:43 PM)
 * @author: 
 */
import com.cannontech.database.model.ModelFactory;
public class YC
{
	public final String ALT_SERIALNUMBER_FILENAME = "VersacomSerial";	//serial number file name
	public final String SERIALNUMBER_FILENAME = "LCRSerial";	//serial number file name
	public final String DEFAULT_FILENAME = "default";	//serial number file name
	private String commandFile = "commandFile";	//current command file, init only to NOT have null value
	
	private String command;	//holds the current Command to execute
	private String serialNumber;	// currently selected serial number (from tree or box)
	private Object treeItem = null;
	private Object route = null;			//when serial number used, the current routeID
	private int    modelType = 0;
	
	private boolean DIRECTLY_SEND_COMMAND = false;	//allows a shortcut send method, changes with CGPCheckBox.

	private volatile int currentUserMessageID = 1;
	public static volatile int sendMore = 0;

	public  static com.cannontech.message.porter.message.Request loopReq = null;

	public Object[] allRoutes = null;

	private int loopType = 0;
	public static int NOLOOP = 0;	//loop not parsed
	public static int LOOP = 1;	//loop alone parsed
	public static int LOOPNUM = 2;	//loop with a count parsed
	public static int LOOPLOCATE = 3;//loop locate parsed
	public static int LOOPLOCATE_ROUTE = 4;//loop locate parsed
	
	private com.cannontech.message.porter.ClientConnection connToPorter = null;

	private YCDefaults ycDefaults = null;
/**
 * YC constructor comment.
 */
public YC() 
{
	super();
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
			com.cannontech.message.porter.message.Request req = 
				new com.cannontech.message.porter.message.Request( 0, getCommand(), currentUserMessageID );
			req.setPriority(getCommandPriority());
			writeNewRequestToPorter( req );
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
				//  ** ADD CODE HERE TO HANDLE NOQUEUE
				com.cannontech.database.data.lite.LiteDeviceMeterNumber ldmn = (com.cannontech.database.data.lite.LiteDeviceMeterNumber) getTreeItem();
				//handleDevice (ldmn.getDeviceID(), ldmn.getLiteType());
				handleDevice (ldmn.getDeviceID());
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
				handleGroup( deviceMeterGroupIds );
			}
			// Collectiongroup is selected.
			else if ( getModelType() == ModelFactory.COLLECTIONGROUP )
			{
				Integer [] deviceMeterGroupIds = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceIDs_CollectionGroups(com.cannontech.common.util.CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
				handleGroup( deviceMeterGroupIds );
			}
			
			else
			{
				//appendOutputTextArea("Unable to interpret command -> " + getCommand() + "\n");
			}
		}
		else
		{
			//appendOutputTextArea(" && getTreeItem() == null");
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 3:38:37 PM)
 * @return java.util.Vector
 */
public Object[] getAllRoutes()
{
	return allRoutes;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 12:11:11 PM)
 * @return java.lang.String
 */
public String getCommand()
{
	return command;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 2:06:03 PM)
 * @return java.lang.String
 */
public String getCommandFile()
{
	if ( commandFile == null )
		commandFile = "commandFile";	//set to something so it's not null
	
	return commandFile;
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
 * This method was created in VisualAge.
 * @return com.cannontechc.client.util.ConfigParmsFile
 * @param item java.lang.Object
 * Obtains the complete command file path.
 * With the exception of the serial number, the command file name is the class path of
 *	of the selected item in the tree.
 * The command file name for the serial numbers is "VersacomSerial" defined globally
 *	as serialNumberFile.
 * The name of the command file is the name of the class that is selected -
 *	the short name, not fully qualified
 */
public com.cannontech.message.util.ConfigParmsFile getConfigFile(Object item)
{
	String className = null;
	
	if (item.toString() == SERIALNUMBER_FILENAME  ||item.toString() == ALT_SERIALNUMBER_FILENAME)	//get serial number class name (constant var)
		className = (String) item;
	else if (item.toString() == DEFAULT_FILENAME )
		className = (String) item;
	else
		className = item.getClass().getName() ;	//use the class name as the file name (.txt)
		
	className = className.substring( className.lastIndexOf('.') + 1) + ".txt";
	return new com.cannontech.message.util.ConfigParmsFile(getCommandFileDirectory() + className);	
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 2:02:30 PM)
 * @return com.cannontech.message.porter.ClientConnection
 */
public com.cannontech.message.porter.ClientConnection getConnToPorter()
{
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
public void handleDevice(long deviceID)
{
	if( deviceID < 0 )	//no device selected
	{
		if ( getSerialNumber() == null)	// NO serial Number Selected
		{
			logCommand(" *** Warning: Please select a Device or Serial Number ***");
			//appendOutputTextArea( "\n *** Warning: Please select a Device or Serial Number ***\n");
			return;
		}
	}

	setLoopType( parseLoopCommand() );

	//**  SEND WITH QUEUE COMMANDS STRING ON ALL **
	//**  FIX:  NEED TO CHECK IF PAOBJECT IS AN MCT OR NOT
	String command = getCommand();
	setCommand(command + getQueueCommandString());
	
	com.cannontech.message.porter.message.Request req = 
		new com.cannontech.message.porter.message.Request( deviceID, getCommand(), currentUserMessageID );
	req.setPriority(getCommandPriority());

 	if( getLoopType() > LOOP)
 	{
		loopReq = req;
 	}
	else if( getLoopType() == LOOPLOCATE )
	{
		//for (int i = get
	}
	else if( getLoopType() == LOOPLOCATE_ROUTE)
	{
		if( getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			req.setRouteID( ((com.cannontech.database.data.lite.LiteYukonPAObject) getRoute()).getYukonID());
		}
	}	

	//appendOutputTextArea( "\n-> Executing command -> " + getCommand() +"\n");
	writeNewRequestToPorter( req );
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
public void handleDevice(long deviceID, int type)
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

	//else if( DeviceTypesFuncs.isMCT( com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((CarrierBase)val).getPAOType() ) ) )
	if ( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type)
		|| com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(type))
		setCommand( command + getQueueCommandString());

	com.cannontech.message.porter.message.Request req = 
		new com.cannontech.message.porter.message.Request( deviceID, getCommand(), currentUserMessageID );
	req.setPriority(getCommandPriority());

 	if( getLoopType() > LOOP )
		loopReq = req;

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

			req.setRouteID(rt.getYukonID());
			loopReq = req;
		}
 	}
	else if( getLoopType() == LOOPLOCATE_ROUTE )
	{
		if( getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			req.setRouteID( ((com.cannontech.database.data.lite.LiteYukonPAObject)getRoute()).getYukonID());
		}
	}
	writeNewRequestToPorter( req );
}
/**
 * Insert the method's description here.
 * Obtains each member (device) that belong's to the currently selected group and creates
 *  a message.Request for each device.
 * Write's the Request to the porter connection.
 * Creation date: (9/16/2001 5:32:40 PM)
 */
public void handleGroup( Integer [] deviceMeterGroupIds )
{
	long deviceID = -1;						//defaulted to the system device
	
	String command = getCommand();

	//else if( DeviceTypesFuncs.isMCT( com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((CarrierBase)val).getPAOType() ) ) )
	//com.cannontech.clientutils.CTILogger.info(" TYPE = " + com.cannontech.database.data.pao.PAOGroups.getDeviceTypeString(type));
	//if ( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type))
	//**  SEND WITH QUEUE COMMANDS STRING ON ALL **
	//**  FIX:  NEED TO CHECK IF PAOBJECT IS AN MCT OR NOT
	setCommand(getCommand().concat(getQueueCommandString()));

	for ( int i = 0; i < deviceMeterGroupIds.length; i++)
	{
		deviceID = deviceMeterGroupIds [i].intValue();
				
		com.cannontech.message.porter.message.Request req = 
			new com.cannontech.message.porter.message.Request( deviceID, command, currentUserMessageID );
		req.setPriority(getCommandPriority());

		//appendOutputTextArea( "\n-> Executing command -> " + getCommand() +"\n");
		writeNewRequestToPorter( req );
	}
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
	long deviceID = -1;						//defaulted to the system device
		 
	int index = getCommand().indexOf("serial");
	
	if( index < 0 )	// serial not in command string = -1
		setCommand( getCommand() + " serial " + getTreeItem() );
	//else	// Do nothing, assume the command is correct

	setLoopType( parseLoopCommand() );
	
	com.cannontech.message.porter.message.Request req = 
			new com.cannontech.message.porter.message.Request( deviceID, getCommand(), currentUserMessageID );
	req.setPriority(getCommandPriority());

	// Set/save the loop request command.
 	if( getLoopType() > LOOP ) 	//more than just loop there
		loopReq = req;
				
	// Get routeID from comboBox / set it in the request
	if( getRoute() != null && getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
	{
		com.cannontech.database.data.lite.LiteYukonPAObject r = (com.cannontech.database.data.lite.LiteYukonPAObject) getRoute();
		req.setRouteID(r.getYukonID());
	}

	if( deviceID < 0 )	//no device selected
	{
		if ( getSerialNumber() == null)	// NO serial Number Selected
		{
			logCommand(" *** Warning: Please select a Device or Serial Number ***");
			return;
		}
	}

	writeNewRequestToPorter( req );
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

	if ( loopIndex >= 0 && tempCommand.length() > 4 )	//there's more there than just loop
	{	
		for (int i = tempCommand.indexOf('p') + 1; i < tempCommand.length(); i++)
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
				synchronized (YC.class)
				{
					setCommand("loop");
					//sendMore = getAllRoutes().length - 1;
					//loop through each route
				}
				return LOOPLOCATE_ROUTE;
			}
			else if( valueSubstring.startsWith("loc"))	//parse out locate
			{
				synchronized (YC.class)
				{
					setCommand("loop");
					sendMore = getAllRoutes().length - 1;
					//loop through each route
				}
				return LOOPLOCATE;
			}
			else
			{
				Integer value = new Integer(valueSubstring );	//assume it is an integer.
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
public void setCommandFile(String newCommandFile)
{
	commandFile = newCommandFile;
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
	//if( !connToPorter.isValid() )
	//{
		//getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
		////appendOutputTextArea( " ** Warning: Not connected to port control service **\n");
		//return;
	//}
	currentUserMessageID++;

	sendMore = -1;		//Cancels multiple command processing done by YC
	//getOutputTextArea().setCaretPosition( getOutputTextArea().getDocument().getEndPosition().getOffset() - 1 );
}
/**
 * This method attempts to substitute a command for the typed in command
 * based on the type of device/point selected
 * -----------------------------------------------------------------------------------------
 * ?? I don't know why this method is applicable, the command files will only contain some of the
 *  possible commands that could be used, there fore any typed command is valid and doesn't need
 *  to be verified (checked that it exists).??  SN sometime in April 2001
 * ----------------------------------------------------------------------------------------- 
 */
public String substituteCommand(String command)
{
	com.cannontech.message.util.ConfigParmsFile cpf = null;

	//if (getTreeItem() == null)
		//return null;

	// If a lite object, using the device type selected. 
	if (getTreeItem() instanceof com.cannontech.database.data.lite.LiteBase)
	{
		com.cannontech.database.db.DBPersistent dbp = com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase) getTreeItem());
		
		if (dbp == null)
			return null;
			
		cpf = getConfigFile(dbp);
	}
	// Else if serial number, use the serial number file string constant.
	else if (getModelType() == ModelFactory.EDITABLELCRSERIAL)
	{
		cpf = getConfigFile( SERIALNUMBER_FILENAME );
		if (cpf.getKeysAndValues() ==null)
			cpf = getConfigFile(ALT_SERIALNUMBER_FILENAME);
	}
	else if (getModelType() == ModelFactory.COLLECTIONGROUP ||
			getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
	{
		cpf = getConfigFile( DEFAULT_FILENAME );
	}
	else
		return null;


	String[][] keysAndValues = cpf.getKeysAndValues();
	String lowerCommand = command.toLowerCase().trim();

	//try to match the command to a key in cpf
	for (int i = 0; i < keysAndValues[0].length; i++)
	{
		String lowerKey = keysAndValues[0][i].toLowerCase().trim();

		if (lowerKey.equals(lowerCommand))
			return keysAndValues[1][i];
	}

	for (int i = 0; i < keysAndValues[1].length; i++)
	{
		String lowerKey = keysAndValues[1][i].toLowerCase().trim();

		if (lowerKey.equals(lowerCommand))
			return keysAndValues[1][i];
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
			com.cannontech.clientutils.CTILogger.info(" REQUEST = "+ request.toString());
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
}
