package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:24:43 PM)
 * @author: 
 */
import java.util.Observable;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSessionBindingEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.roles.yukon.SystemRole;

public class YC extends Observable implements com.cannontech.message.util.MessageListener, javax.servlet.http.HttpSessionBindingListener
{
	/** HashSet of userMessageIds for this instance */
	private java.util.Set requestMessageIDs = new java.util.HashSet();
	
//	public final String ALT_SERIALNUMBER_FILENAME = "VersacomSerial";	//serial number file name
	public final String VERSACOM_SERIAL_FILENAME = "VersacomSerial";	//serial number file name
	public final String EXPRESSCOM_SERIAL_FILENAME = "ExpresscomSerial";	//serial number file name
	public final String SA205_SERIAL_FILENAME = "SA205Serial";	//serial number file name
	public final String SA305_SERIAL_FILENAME = "SA305Serial";	//serial number file name
	public final String SERIALNUMBER_FILENAME = "LCRSerial";	//serial number file name
	public final String DEFAULT_FILENAME = "default";	//serial number file name
	public final String COLLECTION_GROUP_FILENAME = "CollectionGroup";	//serial number file name
	public final String CICUSTOMER_FILENAME = "CICustomer";	//serial number file name

	/** Porter Return messages displayable text */
	private String resultText = "";
	/** Current comand file, init only to avoid having null values.*/ 
	private String commandFileName = "commandFile";
	/** Command file extension. */
	private String commandFileExt = ".txt";
	
	/** Current command string to execute */
	private String command = "";
	
	/** deviceID(opt1) or serialNumber(opt2) will be used to send command to.
	/** Selected deviceID */
	private int deviceID = -1; 
	/** Selected serial Number */
	private String serialNumber;
	/** Selected tree item object*/
	private Object treeItem = null;
	/** selected routeid, used for serialNumber commands or for some loop commands*/
	private int routeID = -1;
	/** Selected tree model type. Refer to com.cannontech.database.model.* for valid models. */
	private int modelType = 0;
	
	/** Valid send command modes */ 
	public static int DEFAULT_MODE = 0;	//send commands with YC addt's (loop, queue, route..)
	public static int CGP_MODE = 1;	//send commands as they are, no parsing done on them
	private int commandMode = DEFAULT_MODE;
	
	/** Store last Porter request message, for use when need to send it again (loop) */
	public Request porterRequest = null;
	
	/** Singleton incrementor for messageIDs to send to porter connection */
	private static volatile long currentUserMessageID = 1;
	
	/** flag indicating more commands to send out (mainly loop commands)*/
	public volatile int sendMore = 0;

	/** All LiteYukonPaobject of type route */
	public Object[] allRoutes = null;

	/** Valid loop command types */
	public static int NOLOOP = 0;	//loop not parsed
	public static int LOOP = 1;	//loop alone parsed
	public static int LOOPNUM = 2;//loop for some num of times
	public static int LOOPLOCATE = 3;//loop locate parsed
	public static int LOOPLOCATE_ROUTE = 4;//loop locate route parsed
	private int loopType = NOLOOP;
	
	/** A singleton instance for a connection to Pil */
	public static ClientConnection connToPorter = null;
	
	/** KeysAndValues for readable command/actual command string*/
	private KeysAndValuesFile keysAndValuesFile = null;

	/** Default YC properties*/
	private YCDefaults ycDefaults = null;
	
	/** Fields used in the MessageReceived(..) method to track last printed data */
	/** dateTime formating string */
	private java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMM d HH:mm:ss a z");
	/** Keep track of the last userMessageID from the MessageEvents */
	private long prevUserID = -1;
	/**TODO - fix this!!*/
	/**Flag indicating first display type data (for headings)*/
	private boolean firstTime = true;
	
	
	public class OutputMessage{
		public static final int DISPLAY_MESSAGE = 0;	//YC defined text
		public static final int DEBUG_MESSAGE = 1;		//Porter defined text
		private int type = DEBUG_MESSAGE;
		private String text;
		private int status = Integer.MIN_VALUE;
		private boolean isUnderline = false;

		public OutputMessage(int type_, String message_)
		{
			this(type_, message_, Integer.MIN_VALUE, false);
		}
		public OutputMessage(int type_, String message_, boolean underline_)
		{
			this(type_, message_, Integer.MIN_VALUE, underline_);
		}
		
		public OutputMessage(int type_, String message_, int status_)
		{
			this(type_, message_, status_, false);
		}
		public OutputMessage(int type_, String message_, int status_, boolean underline_)
		{
			super();
			type = type_;
			text = message_;
			status = status_;
			isUnderline = underline_;
		}
		public boolean isUnderline() { return isUnderline; }
		public int getStatus(){ return status; }
		public String getText(){ return text; }
		public int getType(){ return type; }
	}
	/**
	 * YC constructor comment.
	 */
	public YC()
	{
		this(false);	//don't load defaults from file (mainly for web servlet)
	}
	/**
	 * YC constructor.
	 * If loadDefaultsFromFile_ is true, use the saved properties file for class defualts. 
	 * Gets a connection to porter and adds a message listener to this.
	 * @param boolean loadDefaultsFromFile_
	 */
	public YC(boolean loadDefaultsFromFile_) 
	{
		super();
		ycDefaults = new YCDefaults(loadDefaultsFromFile_);
		getConnToPorter();
		connToPorter.addMessageListener(this);
	}
	
	/**
	 * Execute the command, based on commandMode, selected object type, and YC properties.
	 */
	public void executeCommand()
	{
		if ( getCommandMode() == DEFAULT_MODE)	//Need to do all checks and setup for DEFAULT_MODE
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
			if ( getCommandMode() == CGP_MODE )
			{
				porterRequest = new Request( 0, getCommand(), currentUserMessageID );
				porterRequest.setPriority(getCommandPriority());
				writeNewRequestToPorter( porterRequest );
			}
				
			else if( getTreeItem() != null )	//must setup the request to send
			{
				// Stops the requests from continuing (a.k.a. kills the "loop" command).
				sendMore = 0;
					
				// Device item selected (including other models)
				if( getTreeItem() instanceof LiteYukonPAObject )
				{
					LiteYukonPAObject liteYukonPao = (LiteYukonPAObject) getTreeItem();
					setDeviceID(liteYukonPao.getYukonID());
					handleDevice();
				}
				// Meter number item in tree selected.
				else if( getTreeItem() instanceof LiteDeviceMeterNumber )
				{
					LiteDeviceMeterNumber ldmn = (LiteDeviceMeterNumber) getTreeItem();
					setDeviceID(ldmn.getLiteID());
					handleDevice();
				}		
				// Serial Number item in tree selected.
				else if (ModelFactory.isEditableSerial(getModelType()))
				{
					handleSerialNumber();
				}
				// TestCollectionGroup is selected.
				else if ( getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
				{
					Integer [] deviceMeterGroupIds = DeviceMeterGroup.getDeviceIDs_TestCollectionGroups(CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
					for ( int i = 0; i < deviceMeterGroupIds.length; i++)
					{
						setDeviceID(deviceMeterGroupIds[i].intValue());
						handleDevice ();
					}
				}
				// Collectiongroup is selected.
				else if ( getModelType() == ModelFactory.COLLECTIONGROUP )
				{
					Integer [] deviceMeterGroupIds = DeviceMeterGroup.getDeviceIDs_CollectionGroups(CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
					for ( int i = 0; i < deviceMeterGroupIds.length; i++)
					{
						setDeviceID(deviceMeterGroupIds [i].intValue());
						handleDevice();
					}
				}
				else
				{
					CTILogger.info(getModelType() + " - New type needs to be handled");
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
		if( allRoutes == null)
			allRoutes = com.cannontech.database.cache.functions.PAOFuncs.getAllLiteRoutes();
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
	
	/**
	 * Return the YCDefaults commandFileDirectory
	 * Directory of the command files.
	 * @return String commandFileDirectory.
	 */
	public String getCommandFileDirectory()
	{
		return getYCDefaults().getCommandFileDirectory();
	}
	/**
	 * Returns the commandFileDirectory for custom command files.
	 * Directory of the custom command files. 
	 * @return String customCommandFileDirectory
	 */
	public String getCustomCommandFileDirectory()
	{
		return getYCDefaults().getCommandFileDirectory()+"Custom\\";
	}
	
	/**
	 * Retturns the YCDefaults commandPriority (values 1-14)
	 * @return int commandPriority 
	 */
	public int getCommandPriority()
	{
		return getYCDefaults().getCommandPriority();
	}
	
	/**
	 * Returns the singleton instance of this Porter connection
	 * @return
	 */
	public synchronized ClientConnection getConnToPorter() {
		if(connToPorter == null)
		{
			connect();
		}
		return connToPorter;
	}	

	/**
	 * Creates a singleton connection to porter if it does not exist.
	 * Messages from porter are set to not queue, so we don't overload memory with web calls.
	 * @return com.cannontech.message.porter.ClientConnection
	 */
	private synchronized void connect()
	{
		String host = "127.0.0.1";
		int port = 1540;
		try
		{
			host = RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_MACHINE );
			port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_PORT ) ); 
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

		connToPorter = new ClientConnection();
		connToPorter.setQueueMessages(false);	//don't keep messages, toss once read.
		connToPorter.setHost(host);
		connToPorter.setPort(port);
		connToPorter.setAutoReconnect(true);
					
		try 
		{
			connToPorter.connectWithoutWait();
		}
		catch( Exception e ) 
		{
			CTILogger.error( e.getMessage(), e );
		}
		CTILogger.info(" ************ CONNECTION TO PORTER ESTABLISHED ********************");
		return;	
	}	
	/**
	 * Returns the loop command type
  	 * Valid loop types are:
	 * NOLOOP=0				- loop not parsed
	 * LOOP=1				- loop alone parsed
	 * LOOPNUM=2			- loop for some num of times
	 * LOOPLOCATE=3			- loop locate parsed
	 * LOOPLOCATE_ROUTE=4	- loop locate route parsed
	 * @return int loopType
	 */
	public int getLoopType()
	{
		return loopType;
	}
	/**
	 * Return the model type.
	 * Refer to com.cannontech.database.model.* for valid models.
	 * @return int modelType
	 */
	public int getModelType()
	{
		return modelType;
	}
	/**
	 * Returns the "queue" command string if queuing is turned on.
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
	 * Return the routeID
	 * TODO - RouteID is only useful for loop commands or 
	 *  those applied to serialNumber.
	 * @return int routeID
	 */
	public int getRouteID()
	{
		return routeID;
	}
	/**
	 * Return the serialNumber
	 * @return String serialNumber
	 */
	public String getSerialNumber()
	{
		return serialNumber;
	}
	/**
	 * Return the treeItem.
	 * The current selected tree object
	 * @return java.lang.Object treeItem
	 */
	public Object getTreeItem()
	{
		return treeItem;
	}
	
	/**
	 * Return the default properties.
	 * @return com.cannontech.yc.gui.YCDefaults ycDefaults
	 */
	public YCDefaults getYCDefaults()
	{
		if( ycDefaults == null)
			ycDefaults = new YCDefaults();
		return ycDefaults;
	}
	
	/**
	 * Gets the device ID(opt1) of the object, or selected serial number(opt2) if deviceID is <0
	 * Checks for the command string "loop" to exist.
	 * Creates the message.Request to send to porter.
	 * Saves the message.Request if "loop" was found in the string so the Request can be resubmitted.
	 * Write's the Request to the porter connection.
	 */
	public void handleDevice()
	{
		LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(getDeviceID());

		if( getDeviceID() < 0 )	//no device selected
		{
			logCommand(" *** Warning: Please select a Device (or Serial Number) ***");
			return;
		}
	
		setLoopType( parseLoopCommand() );
		String command = getCommand();
	
		if ( DeviceTypesFuncs.isMCT(liteYukonPao.getType()) || DeviceTypesFuncs.isRepeater(liteYukonPao.getType()))
		{
			if( command.indexOf("noqueue") < 0)
				setCommand( command + getQueueCommandString());
		}
	
		porterRequest = new Request( getDeviceID(), getCommand(), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
		
		if (getLoopType() == LOOPLOCATE)
	 	{
			if( getAllRoutes() != null && getAllRoutes()[sendMore] instanceof LiteYukonPAObject)
			{
				LiteYukonPAObject rt = (LiteYukonPAObject) getAllRoutes()[sendMore];
				while( rt.getType() == PAOGroups.ROUTE_MACRO && sendMore > 0)
				{
					sendMore--;
					rt = (LiteYukonPAObject) getAllRoutes()[sendMore];
				}
	
				if( rt.getType() == PAOGroups.ROUTE_MACRO)
					return;
	
				porterRequest.setRouteID(rt.getYukonID());
			}
	 	}
		else if( getLoopType() == LOOPLOCATE_ROUTE )
		{
			porterRequest.setRouteID(getRouteID());
		}
		writeNewRequestToPorter(porterRequest);
	}
	/**
	 * Looks for the string "serial" in the command.
	 * If the string is not found, tack it on to the string along with the currentSelectedSerialNumber.
	 * Create a message.Request to send to porter.
	 * Set the route selected for the message.Request.
	 * Write Request to the connection to porter.
	 */
	public void handleSerialNumber()
	{
		int index = getCommand().indexOf("serial");
		
		if( index < 0 )	// serial not in command string = -1
			setCommand( getCommand() + " serial " + serialNumber );
		else {	// set serial as in command string
			StringTokenizer st = new StringTokenizer( getCommand().substring(index+6) );
			if (st.hasMoreTokens())
				setSerialNumber( st.nextToken() );
		}
		
		if ( getSerialNumber() == null)	// NO serial Number Selected
		{
			logCommand(" *** Warning: Please select a Serial Number (or Device)***");
			return;
		}
	
		setLoopType( parseLoopCommand() );
		
		porterRequest = new Request( com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID, getCommand(), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
	
		// Get routeID / set it in the request
		if( getRouteID() >= 0)
		{
			porterRequest.setRouteID(getRouteID());
		}
		else
		{
			CTILogger.info("Route cannot be determined. " + getRouteID());
		}
		writeNewRequestToPorter( porterRequest );
	}
	
	/**
	 * Returns the send command mode.
	 * Valid values are:
	 * DEFAULT_MODE = 0;	//send commands with YC addt's (loop, queue, route..)
	 * CGP_MODE = 1;	//send commands as they are, no parsing done on them
	 * @return int commandMode
	 */
	public int getCommandMode() 
	{
		return commandMode;
	}
	
	/**
	 * Notifies the observers of new logging data.
	 * @param logString_ java.lang.String
	 */
	public void logCommand(String logString_)
	{
		setChanged();
		this.notifyObservers(logString_);
	}
	
	/**
	 * Search for the string "loop" in the command.
	 * If loop is found, search for any value in the command (following "loop").
	 * Set sendMore to the number found after loop (which tells how many times loop wishes to perform).
	 * Return boolean: True if "loop" found, False if not found.
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
	 * Sets allRoutes with allRoutesArray Object[]
	 * @param allRoutesVector_ java.util.Vector
	 */
	public void setAllRoutes(Object[] allRoutesArray_) 
	{
		allRoutes = new Object[allRoutesArray_.length];
		for ( int i = 0; i < allRoutesArray_.length; i++)
		{
			allRoutes[i] = allRoutesArray_[i];
		}
	}
	/**
	 * Set the commmand string
	 * @param command_ java.lang.String
	 */
	public void setCommand(String command_)
	{
		command = command_;
	}

	/**
	 * Set the commandFileName based on the item instance.
	 * @param item_ Object
	 */
	public void setCommandFileName(Object item_)
	{
		String className = DEFAULT_FILENAME;
	
		if( item_ instanceof DeviceBase)					//ModelFactory.DEVICE,MCTBROADCAST,LMGROUPS,CAPBANKCONTROLLER
		{
			className = ((DeviceBase)item_).getPAOType();
		}
		else if( item_ instanceof CICustomerBase)		//ModelFactory.CICUSTOMER
		{
			className = CICUSTOMER_FILENAME;
		}
		else if(item_ instanceof DeviceMeterGroupBase)	//ModelFactory.DEVICE_METERNUMBER,		
		{
			int devID = ((DeviceMeterGroupBase)item_).getDeviceMeterGroup().getDeviceID().intValue();
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(devID);
			className = PAOGroups.getPAOTypeString(litePao.getType());
		}
		else if (item_ instanceof String)				//ModelFactory.COLLECTION_GROUP, TESTCOLLECTIONGROUP, LCRSERIAL
		{
			className = (String) item_;
		}
		else 
		{
			//*TODO - This is a really bad catch all...revise!*/
			CTILogger.debug("FILENAME: undefined. Item instance of " + item_.getClass());
		}
		commandFileName = className + getCommandFileExt();
		CTILogger.info(" COMMAND FILE: " + getCommandFileDirectory()+ commandFileName);
		keysAndValuesFile = new KeysAndValuesFile(getCommandFileDirectory(), commandFileName);
	}
			
	/**
	 * Set the connToPorter
	 * @param connection_ com.cannontech.message.porter.ClientConnection
	 */
	public void setConnToPorter(ClientConnection connection_)
	{
		connToPorter = connection_;
		//Must setup these options for this reference also.
		connToPorter.setQueueMessages(false);	//don't keep messages, toss once read.
		connToPorter.addMessageListener(this);
	}
	
	/**
	 * Set the commandMode
	 * Valid values are:
	 * DEFAULT_MODE = 0;	//send commands with YC addt's (loop, queue, route..)
	 * CGP_MODE = 1;	//send commands as they are, no parsing done on them
	 * @param int mode_
	 */
	public void setCommandMode(int mode_) 
	{
		commandMode = mode_;
	}
	/**
	 * Set the loopType
 	 * Valid loop types are:
	 * NOLOOP=0				- loop not parsed
	 * LOOP=1				- loop alone parsed
	 * LOOPNUM=2			- loop for some num of times
	 * LOOPLOCATE=3			- loop locate parsed
	 * LOOPLOCATE_ROUTE=4	- loop locate route parsed
	 * @param loopType_ int
	 */
	public void setLoopType(int loopType_) 
	{
		loopType = loopType_;
	}
	/**
	 * Set the modelType
	 * Refer to com.cannontech.database.model.* for valid models. 
	 * @param modelType_ int
	 */
	public void setModelType(int modelType_)
	{
		modelType = modelType_;	
	}
	/**
	 * Set the routeID
	 * @param routeID_ int 
	 */
	public void setRouteID(int routeID_) 
	{
		routeID = routeID_;
	}
	/**
	 * Set the serialNumber
	 * The serialNumber for the LCR commands
	 * @param serialNumber_ java.lang.String
	 */
	public void setSerialNumber(String serialNumber_)
	{
		if( serialNumber_ == null)
			serialNumber = null;
		else 
			serialNumber = serialNumber_.trim();
	}
	/**
	 * Set the selected treeItem object
	 * @param treeItem_ java.lang.Object
	 */
	public void setTreeItem(Object treeItem_)
	{
		treeItem = treeItem_;
	}
	/**
	 * Set the YCDefualts.
	 * The default properties for YC setup.
	 * @param defaults_ com.cannontech.yc.gui.YCDefaults
	 */
	public void setYCDefaults(YCDefaults defaults_)
	{
		ycDefaults = defaults_;
	}
	/**
	 * Reset the sendMore counter to -1.
	 * Stops multiple command processing done by YC.
	 */
	public void stop()
	{
		generateMessageID();
		sendMore = -1;
	}
	/**
	 * Assumes command may be a "user-friendly" string instead of a porter accepted command string.
	 * Attempt to substitute a porter accepted command for a "user-friendly" command
	 * (Ex.  User type "Read My Meter" instead of "getvalue kwh").
	 */
	public String substituteCommand(String command_)
	{
		if( getKeysAndValues() != null)
		{
			String lowerCommand = command_.toLowerCase().trim();
		
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
	
		return command_; //default, return whatever they typed in on the command line, didn't match with anything
	}
	/**
	 * Write Request message to porter.
	 * @param request com.cannontech.message.porter.message.Request
	 */
	public void writeNewRequestToPorter(Request request_)
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM d HH:mm:ss a z");					
		long timer = (System.currentTimeMillis());

		String log = "";
		if( request_.getDeviceID() > 0)
			log = " Device \'" + PAOFuncs.getYukonPAOName(request_.getDeviceID()) + "\'";
		else
			log = " Serial # \'" + serialNumber + "\'";

		logCommand("[" + format.format(new java.util.Date(timer)) 
			+ "] - {"+ currentUserMessageID + "} Command Sent to" + log + " -  \'" + getCommand() + "\'");
		if( getConnToPorter() != null )
		{
			if( getConnToPorter().isValid())
			{
				getConnToPorter().write( request_ );
				requestMessageIDs.add(new Long(currentUserMessageID));
				generateMessageID();
			}
			else
			{
				String logOutput= "\n["+ displayFormat.format(new java.util.Date()) + "]- Command request not sent.\n" + 
					"Connection to porter is not established.\n";
				OutputMessage message = new OutputMessage(OutputMessage.DEBUG_MESSAGE, logOutput);
				setChanged();
				this.notifyObservers(message);
				setResultText( getResultText() + message.getText());
			
				CTILogger.info("REQUEST NOT SENT: CONNECTION TO PORTER IS NOT VALID");
			}
		}
		else
		{
			CTILogger.info("REQUEST NOT SENT: CONNECTION TO PORTER IS NULL");
		}
			
	}
	/**
	 * Returns the porterRequest.
	 * @return com.cannontech.message.porter.message.Request
	 */
	public Request getPorterRequest()
	{
		return porterRequest;
	}

	/**
	 * Returns the keysAndValuesFile.
	 * @return com.cannontech.common.util.KeysAndValuesFile
	 */
	public KeysAndValuesFile getKeysAndValuesFile()
	{
		return keysAndValuesFile;
	}

	/**
	 * Returns the keysAndValues.
	 * key=UserFriendlyCommandName value=PorterCommandString
	 * @return com.cannontech.common.util.KeysAndValues
	 */
	public KeysAndValues getKeysAndValues()
	{
		if( getKeysAndValuesFile() != null)
			return getKeysAndValuesFile().getKeysAndValues();
		return null;
	}
	/**
	 * A unique count id of Request messages sent to Porter.
	 * @return int currentUserMessageID
	 */
	public long getCurrentUserMessageID()
	{
		return currentUserMessageID;
	}
	/**
	 * Command files extension.
	 * @return String commandFileExt
	 */
	public String getCommandFileExt()
	{
		return commandFileExt;
	}

	/**
	 * Replaces the run() method and allows us to remove the Runnable interface
	 *  by using the MessageListener available to us.
	 * Verifies the messages are of Return type and then uses these messages to create
	 *  readable display/debug messages, OutputMessage.
	 */
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
				if( !requestMessageIDs.contains( new Long(returnMsg.getUserMessageID())))
					return;
				/*else
				{
					/**TODO Should the ids be removed after being processed ?
					//Remove the messageID from the set of this ids.
					requestMessageIDs.remove( new Long(returnMsg.getUserMessageID()));
				}*/
				java.awt.Color textColor = getYCDefaults().getDisplayTextColor();
				String debugOutput = "";
				String displayOutput = "";

				/**inner class object used to contain displayable Return message data.*/
				OutputMessage message;
				/** When new (one that is different from the previous) userMessageID occurs, print datetime, command, etc info*/ 
				if( prevUserID != returnMsg.getUserMessageID())
				{
					//textColor = java.awt.Color.black;
					debugOutput = "\n["+ displayFormat.format(returnMsg.getTimeStamp()) + "]-{" + returnMsg.getUserMessageID() +"} {Device: " +  PAOFuncs.getYukonPAOName(returnMsg.getDeviceID()) + "} Return from \'" + returnMsg.getCommandString() + "\'\n";					
					message = new OutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput);
					setChanged();
					this.notifyObservers(message);
					setResultText( getResultText() + message.getText());
					debugOutput = "";
					prevUserID = returnMsg.getUserMessageID();

					/**TODO - better implement getting the header on the display screen*/				
					/*if( firstTime && getLoopType() != YC.NOLOOP)
					{
						displayOutput = "\n\nROUTE\t\t\tVALID\t\tERROR\n";
						message = new OutputMessage(OutputMessage.DISPLAY_MESSAGE, displayOutput, true);
						setChanged();
						this.notifyObservers(message);
						setResultText( getResultText() + message.getText());
						displayOutput = "";
						firstTime = false;
					}*/
				}
				
				/** Add all PointData.getStr() objects to the output */
				for (int i = 0; i < returnMsg.getVector().size(); i++)
				{
					Object o = returnMsg.getVector().elementAt(i);
					if (o instanceof com.cannontech.message.dispatch.message.PointData)
					{
						com.cannontech.message.dispatch.message.PointData pd = (com.cannontech.message.dispatch.message.PointData) o;
						if ( pd.getStr().length() > 0 )
						{
							int tabCount = (60 - displayOutput.length())/ 24;
							for (int x = 0; x <= tabCount; x++)
							{
								displayOutput += "\t";
							}
							debugOutput += pd.getStr() + "\n";
						}
					}
				}

				if( returnMsg.getExpectMore() == 0)
				{
					String routeName = null;
					if (returnMsg.getRouteOffset() > 0)
						routeName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(returnMsg.getRouteOffset());																				
					
					if( routeName == null)
						routeName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(returnMsg.getDeviceID());

					displayOutput = "Route:   " + routeName;
					int tabCount = (60 - displayOutput.length())/ 24;
					for (int i = 0; i <= tabCount; i++)
					{
						displayOutput += "\t";
					}


					if( getLoopType() != YC.NOLOOP)
					{
						if( returnMsg.getStatus() != 0)
						{
							textColor = getYCDefaults().getInvalidTextColor();
							if( returnMsg.getExpectMore() == 0)
								displayOutput += "Error  " + returnMsg.getStatus() + "\t( " + returnMsg.getResultString()+ " )";
						}
						else	//status == 0 == successfull
						{
							textColor = getYCDefaults().getValidTextColor();
							if( returnMsg.getExpectMore() == 0)
								displayOutput += "Valid";
						}
						displayOutput += "\n";
						message = new OutputMessage(OutputMessage.DISPLAY_MESSAGE, displayOutput, returnMsg.getStatus());
						setChanged();
						this.notifyObservers(message);
						setResultText( getResultText() + message.getText());									
					}
				}
				if(returnMsg.getResultString().length() > 0)
				{
					debugOutput += returnMsg.getResultString() + "\n";
				}
				
				message = new OutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput, returnMsg.getStatus());
				setChanged();
				this.notifyObservers(message);
				setResultText( getResultText() + message.getText());
				synchronized ( YukonCommander.class )
				{
					if( returnMsg.getExpectMore() == 0)	//Only send next message when ret expects nothing more
					{
						//Break out of this outer loop.
						doneSendMore:
						if( sendMore == 0)
						{
							// command finished
						}
						else if ( sendMore > 0)
						{
							sendMore--;	//decrement the number of messages to send
							if (getLoopType() == YC.LOOPLOCATE)
							{
								if( getAllRoutes()[sendMore] instanceof LiteYukonPAObject)
								{
									LiteYukonPAObject rt = (LiteYukonPAObject) getAllRoutes()[sendMore];
									while( rt.getType() == PAOGroups.ROUTE_MACRO
										&& sendMore > 0)
									{
										sendMore--;
										rt = (LiteYukonPAObject) getAllRoutes()[sendMore];
									}
									// Have to check again because last one may be route_ macro
									if(rt.getType() == PAOGroups.ROUTE_MACRO)
										break doneSendMore;

									getPorterRequest().setRouteID(rt.getYukonID());
								}
							}
							getConnToPorter().write( getPorterRequest());	//do the saved loop request
						}
						else
						{
							debugOutput = "Command cancelled\n";
							textColor = getYCDefaults().getInvalidTextColor();
							message = new OutputMessage(OutputMessage.DEBUG_MESSAGE, debugOutput, returnMsg.getStatus());
							setChanged();
							this.notifyObservers(message);
							setResultText( getResultText() + message.getText());
						}
					}
				}
			}
		}
	}

		
	/**
	 * Returns result string from Return porter messages.
	 * @return String resultText
	 */
	public String getResultText()
	{
		return resultText;
	}

	/**
	 * Sets the resultText
	 * @param string String
	 */
	public void setResultText(String string)
	{
		resultText = string;
	}
	public void clearResultText()
	{
		resultText = "";
	}

	/**
	 * Write resultText to out.
	 * @param out OutputStream
	 * @throws java.io.IOException
	 */
	public void encodeResults(java.io.OutputStream out) throws java.io.IOException 
	{
		do
		{
			StringBuffer buf = new StringBuffer("<table><tr><td>"+getResultText()+"</td></tr></table>");
			
			out.write(buf.toString().getBytes());	
		}
		while (false);
		System.out.println(" EXITTING ENCODERESULTS");
	}

	/**
	 * DeviceId of the current selected device, if exists.
 	 * @return int deviceID
	 */
	public int getDeviceID()
	{
		return deviceID;
	}

	/**
	 * Sets the deviceID
	 * @param deviceID_ int
	 */
	public void setDeviceID(int deviceID_)
	{
		deviceID = deviceID_;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0)
	{
		// TODO Auto-generated method stub
		System.out.println("***** Value Bound " + arg0.getValue().toString() + "*****");	
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0)
	{
		// TODO Is removing the messageListener enough?
		System.out.println("***** Value UNBound " + arg0.getValue().toString() + "*****");
		getConnToPorter().removeMessageListener(this);
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
}
