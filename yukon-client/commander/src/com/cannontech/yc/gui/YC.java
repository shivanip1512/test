package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2002 3:24:43 PM)
 * @author: 
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Timer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesFile;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.CommandFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.Command;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class YC extends Observable implements MessageListener
{
	/** HashSet of userMessageIds for this instance */
	private java.util.Set requestMessageIDs = new java.util.HashSet(10);

	/** Time in millis (yes I realize this is an int) to wait for command response messages */
	private int timeOut = 0;
	/** An action listener for the timer events */
	private ActionListener timerPerfomer = null;
	/** A timer which fires an action event after a specified delay in millis */
	private Timer stopWatch = null;	
	
	/** Porter Return messages displayable text */
	private String resultText = "";
	
	/** Current command string to execute */
	private String commandString = "";	//the actual string entered from the command line
	private Vector executeCmdsVector = null;	// the parsed vector of commands from the command line.
	
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
	
	/** Contains com.cannontech.database.data.lite.LiteDeviceTypeCommand for the deviceType selected */
	private Vector liteDeviceTypeCommandsVector = new Vector();
	/** The device Type for the currently selected object in the tree model. Values found in DeviceTypes class**/
	public String deviceType = null;
	
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
		loadCustomCommandsFromDatabase();
		ycDefaults = new YCDefaults(loadDefaultsFromFile_);
		getPilConn().addMessageListener(this);
	}

	private IServerConnection getPilConn()
    {
        return ConnPool.getInstance().getDefPorterConn();        
    }

	/**
	 * Execute the command, based on commandMode, selected object type, and YC properties.
	 */
	public void executeCommand()
	{
		//---------------------------------------------------------------------------------------
		try
		{
			if ( getCommandMode() == CGP_MODE )
			{
				porterRequest = new Request( 0, (String)getExecuteCmdsVector().get(0), currentUserMessageID );
				porterRequest.setPriority(getCommandPriority());
				getExecuteCmdsVector().remove(0);	//remove the sent command from the list!
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
					Vector savedVector = (Vector)getExecuteCmdsVector().clone();
					for ( int i = 0; i < deviceMeterGroupIds.length; i++)
					{
						setDeviceID(deviceMeterGroupIds[i].intValue());
						handleDevice ();
						//clone the vector because handleDevice() removed the command but in truth, it 
						// shouldn't be removed until all of the devices have been looped through.
						if( i < deviceMeterGroupIds.length)
							executeCmdsVector = (Vector)savedVector.clone();
					}
				}
				// Collectiongroup is selected.
				else if ( getModelType() == ModelFactory.COLLECTIONGROUP )
				{
					synchronized(YC.this)
					{
						Integer [] deviceMeterGroupIds = DeviceMeterGroup.getDeviceIDs_CollectionGroups(CtiUtilities.getDatabaseAlias(), getTreeItem().toString());
						Vector savedVector = (Vector)getExecuteCmdsVector().clone();
						
						for ( int i = 0; i < deviceMeterGroupIds.length; i++)
						{
							setDeviceID(deviceMeterGroupIds [i].intValue());
							handleDevice();
							//clone the vector because handleDevice() removed the command but in truth, it
							// shouldn't be removed until all of the devices have been looped through.							
							if( i+1 < deviceMeterGroupIds.length)
								executeCmdsVector = (Vector)savedVector.clone();
						}
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
	public String getCommandString()
	{
		return commandString;
	}

	/** Vector of String commands, parsed from commandString */	
	public Vector getExecuteCmdsVector()
	{
		if( executeCmdsVector == null)
			executeCmdsVector = new Vector(2);
		return executeCmdsVector;
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

		Vector commandVec = getExecuteCmdsVector();
		for (int i = 0; i < commandVec.size(); i++)
		{	
			String command = (String)getExecuteCmdsVector().get(i);			
			if ( DeviceTypesFuncs.isMCT(liteYukonPao.getType()) || DeviceTypesFuncs.isRepeater(liteYukonPao.getType()))
			{
				if( command.indexOf("noqueue") < 0)
				getExecuteCmdsVector().setElementAt( command + getQueueCommandString(), i);	//replace the old command with this one
			}
		}
	
		//send the first command from the vector out!
		porterRequest = new Request( getDeviceID(), (String)getExecuteCmdsVector().get(0), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
		getExecuteCmdsVector().remove(0);	//remove the sent command from the list!
		
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
		for (int i = 0; i < getExecuteCmdsVector().size(); i++)
		{
			String command = (String)getExecuteCmdsVector().get(i);
			int index = command.indexOf("serial");
			
			if( index < 0 )	// serial not in command string = -1
			getExecuteCmdsVector().setElementAt( command + " serial " + serialNumber , i);
			else {	// set serial as in command string
				StringTokenizer st = new StringTokenizer( command.substring(index+6) );
				if (st.hasMoreTokens())
				{
					/** TODO only one serial number can be entered with multiple commands at this time */
					String serialNumToken = st.nextToken();
					if( i > 0)	//more than one command
					{
						if( !getSerialNumber().equalsIgnoreCase(serialNumToken) )
							logCommand(" ** Warning: Different serial numbers are being used in this multiple command, the first serial number will be used for all commands!");
					}
					else	//only store/use the serial number of the first command!
						setSerialNumber( serialNumToken);
				}
			}
		}
		
		if ( getSerialNumber() == null)	// NO serial Number Selected
		{
			logCommand(" *** Warning: Please select a Serial Number (or Device)***");
			return;
		}
	
		setLoopType( parseLoopCommand() );
		
		porterRequest = new Request( com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID, (String)getExecuteCmdsVector().get(0), currentUserMessageID );
		porterRequest.setPriority(getCommandPriority());
		getExecuteCmdsVector().remove(0);	//remove the sent command from the list!

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
		for (int i = 0; i < getExecuteCmdsVector().size(); i++)
		{
			String tempCommand = ((String)getExecuteCmdsVector().get(i)).toLowerCase();
			String valueSubstring = null;
				
			int loopIndex = tempCommand.indexOf("loop");
		
			if ( loopIndex >= 0)	//a loop exists
			{	
				for (int j = tempCommand.indexOf("loop") + 4; j < tempCommand.length(); j++)
				{
					if ( tempCommand.charAt(j) != ' ' && tempCommand.charAt(j) != '\t')	//skip whitespaces
					{
						valueSubstring = tempCommand.substring( j );
						break;
					}
				}
				
				if (valueSubstring != null)
				{
					if( valueSubstring.startsWith("locater"))	//parse out locateroute
					{
						synchronized(YC.class)
						{
							getExecuteCmdsVector().setElementAt("loop", i);
						}
						return LOOPLOCATE_ROUTE;
					}
					else if( valueSubstring.startsWith("loc"))	//parse out locate
					{
						synchronized (YC.class)
						{
							getExecuteCmdsVector().setElementAt("loop", i);
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
							getExecuteCmdsVector().setElementAt("loop", i);
							//Subtract one because 0 is the last occurance, not 1.  (Ex. 0-4 not 1-5)
							sendMore = value.intValue() - 1;
						}
						return LOOPNUM;
					}
				}
				return LOOP;
			}
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
	public void setCommandString(String command_)
	{
		commandString = command_;
		setCommands(commandString);
	}

	/**
	 * Takes a command string, then parses the string for multiple commands
	 *  separated by the '&' character
	 */
	public void setCommands(String command_)
	{
		//remove everything from the vector, otherwise we could get in a big loop...
		getExecuteCmdsVector().removeAllElements();
		
		final char SEPARATOR = '&';
		String tempCommand = command_;

		int begIndex = 0;
		int sepIndex = tempCommand.indexOf(SEPARATOR);

		while(sepIndex > -1)
		{
			String begString = tempCommand.substring(0, sepIndex).trim();
			begIndex = sepIndex+1;
			getExecuteCmdsVector().add(getCommandFromLabel(begString));
			tempCommand = tempCommand.substring(begIndex).trim();
			sepIndex = tempCommand.indexOf(SEPARATOR);
		}
		//add the final (or only) command.
		getExecuteCmdsVector().add(getCommandFromLabel(tempCommand));
	}
	/**
	 * Set the commandFileName based on the item instance.
	 * @param item_ Object
	 */
	public void setDeviceType(Object item_)
	{
		if( item_ instanceof DeviceBase)					//ModelFactory.DEVICE,MCTBROADCAST,LMGROUPS,CAPBANKCONTROLLER
		{
			deviceType = ((DeviceBase)item_).getPAOType();
		}
		else if(item_ instanceof DeviceMeterGroupBase)	//ModelFactory.DEVICE_METERNUMBER,		
		{
			int devID = ((DeviceMeterGroupBase)item_).getDeviceMeterGroup().getDeviceID().intValue();
			LiteYukonPAObject litePao = PAOFuncs.getLiteYukonPAO(devID);
			deviceType = PAOGroups.getPAOTypeString(litePao.getType());
		}
		else if (item_ instanceof String)				//ModelFactory.COLLECTION_GROUP, TESTCOLLECTIONGROUP, LCRSERIAL
		{
			deviceType = (String) item_;
		}
		else 
		{
			deviceType = "";			
			//*TODO - This is a really bad catch all...revise!*/
			CTILogger.error("Device Type undefined. Item instance of " + item_.getClass());
		}
		CTILogger.debug(" DEVICE TYPE for command lookup: " + deviceType);
		setLiteDeviceTypeCommandsVector(CommandFuncs.getAllDevTypeCommands(deviceType));
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
	public String getCommandFromLabel(String command_)
	{
		if ( getCommandMode() == DEFAULT_MODE)	//Need to do all checks and setup for DEFAULT_MODE
		{
			if( getLiteDeviceTypeCommandsVector() != null)
			{
				String friendlyCommand = command_.trim();

				//try to match the entered command string alias to a label in the database, return the actual command.
				// OR
				//try to match the entered command string alias to the actual command in the database, return the actual command.
				for (int i = 0; i < getLiteDeviceTypeCommandsVector().size(); i++)
				{
					LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)getLiteDeviceTypeCommandsVector().get(i);
					LiteCommand lc = CommandFuncs.getCommand(ldtc.getCommandID());
					if (lc.getLabel().trim().equalsIgnoreCase(friendlyCommand) ||
						lc.getCommand().trim().equalsIgnoreCase(friendlyCommand))
						return lc.getCommand();
				}
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
			+ "] - {"+ currentUserMessageID + "} Command Sent to" + log + " -  \'" + request_.getCommandString() + "\'");
		if( getPilConn().isValid() )
		{
			startStopWatch(getTimeOut());
            getPilConn().write( request_ );
			getRequestMessageIDs().add(new Long(currentUserMessageID));
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
	/**
	 * Returns the porterRequest.
	 * @return com.cannontech.message.porter.message.Request
	 */
	public Request getPorterRequest()
	{
		return porterRequest;
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
		Message in = e.getMessage();		
		if(in instanceof Return)
		{
			Return returnMsg = (Return) in;
			synchronized(this)
			{
				if( !getRequestMessageIDs().contains( new Long(returnMsg.getUserMessageID())))
					return;
				else
				{
					/**TODO Should the ids be removed after being processed ? */
					//Remove the messageID from the set of this ids.
					if(sendMore == 0 && returnMsg.getExpectMore() == 0)	//nothing more is coming, remove from list.
					{
						getRequestMessageIDs().remove( new Long(returnMsg.getUserMessageID()));
					}
				}
				System.out.println(" - " + getRequestMessageIDs().size());
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
							// command finished, see if there are more commands to send
							if( !getExecuteCmdsVector().isEmpty())
							{
								executeCommand();
							}							
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
							startStopWatch(getTimeOut());
							getPilConn().write( getPorterRequest());	//do the saved loop request
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
	/**
	 * Returns a vector of com.cannontech.database.data.lite.LiteDeviceTypeCommand values.
	 * @return
	 */
	public Vector getLiteDeviceTypeCommandsVector ()
	{
		return liteDeviceTypeCommandsVector;
	}

	/**
	 * Vector of com.cannontech.database.data.lite.LiteDeviceTypeCommand values.
	 * @param vector
	 */
	public void setLiteDeviceTypeCommandsVector (Vector vector)
	{
		liteDeviceTypeCommandsVector = vector;
		java.util.Collections.sort(this.liteDeviceTypeCommandsVector, LiteComparators.liteDeviceTypeCommandComparator);
	}

	/**
	 * Returns the currently selected object from the tree model's device type string.
	 * @return String deviceType
	 */
	public String getDeviceType()
	{
		return deviceType;
	}

	/**
	 * @return
	 */
	public java.util.Set getRequestMessageIDs()
	{
		return requestMessageIDs;
	}

	/**
	 * @return
	 */
	public void startStopWatch(int timeOutInMillis)
	{
		if( stopWatch == null)
			stopWatch = new Timer(timeOutInMillis, timerPerfomer);
		stopWatch.setInitialDelay(timeOutInMillis);
		stopWatch.setRepeats(false);
		stopWatch.start();
	}
	/**
	 * Returns true if the timer is running, otherwise return false (timer has stopped).
	 * @return boolean
	 */
	public boolean isWatchRunning()
	{
		if( stopWatch != null && stopWatch.isRunning())
			return true;
		return false;
	}
	/**
	 * @return
	 */
	public int getTimeOut()
	{
		return timeOut;
	}

	/**
	 * @param i
	 */
	public void setTimeOut(int i)
	{
		timeOut = i;
	}

	/**
	 * @return
	 */
	public ActionListener getTimerPerfomer()
	{
		if (timerPerfomer == null)
		{
			timerPerfomer = new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					setTimeOut(0);
					((Timer)e.getSource()).stop();
				}
			};
		}
		return timerPerfomer;
	}
	
    /**
	 * Load custom command files to be parsed for custom commands.
	 */
	private void loadCustomCommandsFromDatabase()
	{
		File f = new File(CtiUtilities.getCommandsDirPath()+"custom/");
		
		if (f.exists())
		{
			String []fileNames = f.list();
			{
				for (int i = 0; i < fileNames.length; i++)
				{
					System.out.println(fileNames[i]);
					int extIndex= fileNames[i].lastIndexOf('.');
					if( extIndex > 0 )
					{
						String fileName = fileNames[i].substring(0, extIndex);
						String category = null;
						
						if( PAOGroups.getDeviceType(fileName) != PAOGroups.INVALID)
						{
						    category = fileName;
						}
						else if( fileName.equalsIgnoreCase("alpha-base"))
					        category = CommandCategory.STRING_CMD_ALPHA_BASE;
						else if( fileName.equalsIgnoreCase("cbc-base"))
					        category = CommandCategory.STRING_CMD_CBC_BASE;
						else if( fileName.equalsIgnoreCase("ccu-base"))
					        category = CommandCategory.STRING_CMD_CCU_BASE;
						else if( fileName.equalsIgnoreCase("iedbase"))
					        category = CommandCategory.STRING_CMD_IED_BASE;
						else if( fileName.equalsIgnoreCase("ion-base"))
					        category = CommandCategory.STRING_CMD_ION_BASE;
						else if( fileName.equalsIgnoreCase("lcu-base"))
					        category = CommandCategory.STRING_CMD_LCU_BASE;
						else if( fileName.equalsIgnoreCase("lsbase"))
					        category = CommandCategory.STRING_CMD_LP_BASE;
						else if( fileName.equalsIgnoreCase("loadgroup-base"))
					        category = CommandCategory.STRING_CMD_LOAD_GROUP_BASE;
						else if( fileName.equalsIgnoreCase("mct-base"))
					        category = CommandCategory.STRING_CMD_MCT_BASE;
						else if( fileName.equalsIgnoreCase("rtu-base"))
					        category = CommandCategory.STRING_CMD_RTU_BASE;
						else if( fileName.equalsIgnoreCase("repeater-base"))
					        category = CommandCategory.STRING_CMD_REPEATER_BASE;
						else if( fileName.equalsIgnoreCase("tcu-base"))
					        category = CommandCategory.STRING_CMD_TCU_BASE;
						else if( fileName.equalsIgnoreCase("lcrserial"))
					        category = CommandCategory.STRING_CMD_SERIALNUMBER;
						else
						{
						    CTILogger.info("UNknown filename: " + fileName);
						}
	
						if(category != null)
						{
						    parseCommandFile(f.getAbsolutePath()+"/"+fileNames[i], category);
						}
					    writeProcessedFile(fileNames[i]);
					}
				}
			}
			//Force a reload of all commands and deviceTypeCommands!
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			cache.releaseAllCommands();
			cache.releaseAllDeviceTypeCommands();
		}
	}
	
	/**
	 * Parse command file for custom commands.
	 * @param dirFileName
	 * @param category
	 */
	private void parseCommandFile(String dirFileName, String category)
	{
		KeysAndValuesFile kavFile = new KeysAndValuesFile(dirFileName);
		KeysAndValues keysAndValues = kavFile.getKeysAndValues();
		
		if( keysAndValues != null )
		{
			String labels[] = keysAndValues.getKeys();
			String commands[] = keysAndValues.getValues();

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			List allCmds = cache.getAllCommands();

			for (int i = 0; i < labels.length; i++)
			{
			    Command cmd = new Command();
			    cmd.setLabel(labels[i].trim());
			    cmd.setCommand(commands[i].trim());
			    cmd.setCategory(category);
			    boolean exists = false;
				for (int j = 0; j < allCmds.size(); j++)
				{
				    LiteCommand lc = (LiteCommand)allCmds.get(j);
				    if( (lc.getCommand().equalsIgnoreCase(cmd.getCommand()) &&
				            lc.getLabel().equalsIgnoreCase(cmd.getLabel())))
				    {
				        exists = true;
				        break;
				    }
				}
				if( !exists)
				{
				    insertDBPersistent(cmd, Transaction.INSERT);
					//Lazy man's cache reload! (...we don't have a dispatch connection)
				    cache.releaseAllCommands();						
					addDeviceTypeCommand(category, cmd);
				    System.out.println("adding: " + cmd.toString() + " ID:" + cmd.getCommandID().toString());
                }
			}
			
		}
	}
	
	/**
	 * insert item into database.  No DBChange is sent out.
	 * @param item
	 * @param transType
	 */
	public void insertDBPersistent(DBPersistent item, int transType) 
	{
		if( item != null )
		{
			try
			{
				Transaction t = Transaction.createTransaction(transType, item);
				item = t.execute();
			}
			catch( com.cannontech.database.TransactionException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
			catch( NullPointerException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
			
		}
	}	
	/**
	 * Inserts a deviceTypeCommand into the database for the cmd and devType parameters.
	 * If the devType is a category, then an entry for every deviceType in that category is inserted.
	 */
	private void addDeviceTypeCommand(String devType, Command cmd)
	{
		if( CommandCategory.isCommandCategory(devType))
		{
	//		The deviceType is actually a category, not a deviceType from YukonPaobject.paoType column
			ArrayList devTypes = CommandCategory.getAllTypesForCategory(devType);
			DeviceTypeCommand dbP = null;
			for (int i = 0; i < devTypes.size(); i++)
			{
				//Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
				dbP = new DeviceTypeCommand();
				dbP.getDeviceTypeCommand().setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
				dbP.getDeviceTypeCommand().setDeviceType((String)devTypes.get(i));
				dbP.getDeviceTypeCommand().setDisplayOrder(new Integer(20));//hey, default it, we're going to update it in a bit anyway right? 
				dbP.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
				dbP.setCommand(cmd);
				insertDBPersistent(dbP, Transaction.INSERT);
			}
		}
		else
		{
			//Add to DeviceTypeCommand table, entries for all deviceTypes! yikes...I know
			DeviceTypeCommand dbP = new DeviceTypeCommand();
			dbP.getDeviceTypeCommand().setDeviceCommandID(com.cannontech.database.db.command.DeviceTypeCommand.getNextID(CtiUtilities.getDatabaseAlias()));
			dbP.getDeviceTypeCommand().setDeviceType(devType);
			dbP.getDeviceTypeCommand().setDisplayOrder(new Integer(20));//hey, default it, we're going to update it in a bit anyway right? 
			dbP.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
			dbP.setCommand(cmd);
			insertDBPersistent(dbP, Transaction.INSERT);
		}
	}	
	/**
	 * Move the procFileName from /command/custom/ to /command/processed/ 
	 */
	public void writeProcessedFile(String procFileName)
	{
		File  file = new File(CtiUtilities.getCommandsDirPath()+"custom/" + procFileName);
		File destFile = new File(CtiUtilities.getCommandsDirPath()+"processed/");
		destFile.mkdirs();
		destFile = new File(CtiUtilities.getCommandsDirPath()+"processed/" + procFileName);
	    boolean success = file.renameTo(destFile);
	    if( !success)
	    {
	        CTILogger.info("Could not rename file " + procFileName + ".  This file will be deleted.");
	        file.delete();
	    }
	}		
}
